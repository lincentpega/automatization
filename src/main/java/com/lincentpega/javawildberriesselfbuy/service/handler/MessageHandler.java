package com.lincentpega.javawildberriesselfbuy.service.handler;

import com.lincentpega.javawildberriesselfbuy.constants.BotMessageEnum;
import com.lincentpega.javawildberriesselfbuy.controller.TelegramApiClient;
import com.lincentpega.javawildberriesselfbuy.exceptions.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.exceptions.TelegramFileUploadException;
import com.lincentpega.javawildberriesselfbuy.service.ChromeSession;
import com.lincentpega.javawildberriesselfbuy.constants.SessionState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Pattern;

@Log4j2
@Component
public class MessageHandler {
    private final HashMap<String, ChromeSession> idSessionHashMap;
    private final TelegramApiClient telegramApiClient;

    public MessageHandler(TelegramApiClient telegramApiClient) {
        idSessionHashMap = new HashMap<>();
        this.telegramApiClient = telegramApiClient;
    }

    public BotApiMethod<?> handleUpdateWithMessage(Update update) throws IllegalInputException {
        Message message = update.getMessage();
        String messageText = message.getText();
        String userId = message.getFrom().getId().toString();

        if (message.hasText()) {
            if (messageText.equals("/help")) {
                return processHelp(message);

            } else if (messageText.equals("/start")) {
                return processStart(message);

            } else if (idSessionHashMap.containsKey(userId)) {
                return handleSessionMessage(message);

            } else {
                throw new IllegalInputException(BotMessageEnum.SESSION_DOESNT_EXIST.getMessage());
            }
        } else {
            throw new IllegalInputException("Сообщение пусто");
        }
    }

    private BotApiMethod<?> handleSessionMessage(Message message) throws IllegalInputException {
        String messageText = message.getText();
        String userId = message.getFrom().getId().toString();
        ChromeSession userSession = idSessionHashMap.get(userId);

        if (messageText.equals("/state")) {
            return processState(message);
        } else if (messageText.equals("/close")) {
            return processClose(message);
        } else if (messageText.equals("/pay") && userSession.getUpdatedState() == SessionState.ADDRESS_CHOSEN) {
            return processPayment(message);
        } else {
            if (message.hasEntities()) {
                var messageEntities = message.getEntities();
                if (userSession.isAuthenticated()
                        && messageEntities.size() == 2
                        && messageEntities.get(0).getText().equals("/offer")) {
                    return processOffer(message);
                }
            }
            return handlePlainMessage(message);
        }
    }

    private BotApiMethod<?> handlePlainMessage(Message message) throws IllegalInputException {
        String userId = extractUserId(message);

        ChromeSession userSession = idSessionHashMap.get(userId);
        SessionState sessionState = userSession.getUpdatedState();

        if (sessionState == SessionState.SITE_ENTERED
                && isPhoneNumber(message)) {
            return processNumber(message, userSession);

        } else if (sessionState == SessionState.CAPTCHA_APPEARED
                || sessionState == SessionState.CAPTCHA_CODE_WRONG) {
            return processCaptcha(message, userSession);

        } else if ((sessionState == SessionState.SMS_REQUESTED
                || sessionState == SessionState.WRONG_CODE_ENTERED)
                && isCode(message)) {
            return processCode(message, userSession);

        } else if (sessionState == SessionState.GOOD_IN_CART) {
            return processAddress(message);

        } else {
            log.warn("Illegal input: state = " + sessionState + " message = " + message);
            throw new IllegalInputException(BotMessageEnum.ILLEGAL_INPUT.getMessage());
        }
    }

    private BotApiMethod<?> processAddress(Message message) {
        String userId = extractUserId(message);

        ChromeSession userSession = idSessionHashMap.get(userId);
        userSession.chooseAddress(message.getText());
        return new SendMessage(message.getChatId().toString(), "Адрес введён успешно, теперь можно оплатить /pay");
    }

    private BotApiMethod<?> processPayment(Message message) {
        String userId = extractUserId(message);

        ChromeSession userSession = idSessionHashMap.get(userId);
        userSession.choosePaymentMethodAndPay(message);
        try {
            userSession.takeScreenshot(userId);
            sendScreenshot(message);
        } catch (IOException | TelegramFileUploadException e) {
            log.error(e.getStackTrace());
            return new SendMessage(message.getChatId().toString(),
                    "Проблема с загрузкой скриншота, обратитесь к разработчику");
        }
        return new SendMessage(message.getChatId().toString(), "Оплатите заказ с помощью QR-кода со скриншота");
    }

    private BotApiMethod<?> processOffer(Message message) throws IllegalInputException {
        String userId = extractUserId(message);

        var messageEntities = message.getEntities();
        String url = messageEntities.get(1).getText();
        if (isGoodUrl(url)) {
            ChromeSession userSession = idSessionHashMap.get(userId);
            userSession.addGoodToCart(url);

            return new SendMessage(message.getChatId().toString(), "Товар выбран и отправлен в корзину,"
                    + " введите адрес доставки в формате <Город, улица, дом>, чтобы прервать ввод, введите cancel");
        } else {
            throw new IllegalInputException("Illegal goods URL");
        }

    }

    private BotApiMethod<?> processHelp(Message message) {
        return new SendMessage(message.getChatId().toString(), BotMessageEnum.HELP_MESSAGE.getMessage());
    }

    private BotApiMethod<?> processStart(Message message) {
        String userId = extractUserId(message);

        if (idSessionHashMap.containsKey(userId)) {
            return new SendMessage(message.getChatId().toString(), BotMessageEnum.SESSION_ALREADY_EXISTS.getMessage());
        }

        ChromeSession chromeSession = getChromeSession();
        chromeSession.openWebsite();

        idSessionHashMap.put(userId, chromeSession);

        return new SendMessage(message.getChatId().toString(), BotMessageEnum.SESSION_CREATED.getMessage());

    }

    private BotApiMethod<?> processState(Message message) {
        String userId = extractUserId(message);

        String creationDateTime = idSessionHashMap.get(userId).getCreationDateTime();

        return new SendMessage(message.getChatId().toString(), "Сессия была создана " + creationDateTime);
    }

    private BotApiMethod<?> processNumber(Message message, ChromeSession userSession) {
        String userId = extractUserId(message);

        userSession.setNumber(message.getText());

        if (userSession.isCookiesExist()) {
            userSession.authenticateByCookies();
            return new SendMessage(message.getChatId().toString(), "Выполнена авторизация по куки-файлам");
        }

        userSession.enterNumber(message.getText());

        SessionState state = userSession.getUpdatedState();

        if (state == SessionState.PUSH_UP_REQUESTED || state == SessionState.NOTIFICATION_SENT) {
            userSession.requestCodeAsSMS();
            state = userSession.getUpdatedState();
        }

        if (state == SessionState.CAPTCHA_APPEARED) {
            try {
                userSession.takeScreenshot(userId);
                sendScreenshot(message);
            } catch (IOException | TelegramFileUploadException e) {
                return new SendMessage(message.getChatId().toString(),
                        "Проблема с загрузкой скриншота, обратитесь к разработчику");
            }
            return new SendMessage(message.getChatId().toString(),
                    "Введите капчу с картинки");
        } else {
            return new SendMessage(message.getChatId().toString(),
                    "Номер введён успешно, введите код подтверждения");
        }
    }

    private BotApiMethod<?> processCaptcha(Message message, ChromeSession userSession) {
        String userId = extractUserId(message);
        userSession.enterCaptcha(message.getText());

        SessionState state = userSession.getUpdatedState();

        if (state == SessionState.CAPTCHA_CODE_WRONG) {
            try {
                userSession.takeScreenshot(userId);
                sendScreenshot(message);
            } catch (IOException | TelegramFileUploadException e) {
                return new SendMessage(message.getChatId().toString(),
                        "Проблема с загрузкой скриншота, обратитесь к разработчику");
            }
            return new SendMessage(message.getChatId().toString(),
                    "Введите капчу с картинки");
        } else {
            return new SendMessage(message.getChatId().toString(),
                    "Капча введена верно, введите код подтверждения, направленный в SMS");
        }
    }

    private BotApiMethod<?> processCode(Message message, ChromeSession userSession) throws IllegalInputException {
        userSession.enterCode(message.getText());

        SessionState state = userSession.getUpdatedState();

        switch (state) {
            case WRONG_CODE_ENTERED:
                return new SendMessage(message.getChatId().toString(), "Введён неверный код, введите ещё раз");
            case AUTHENTICATED:
                userSession.saveCookies();
                return new SendMessage(message.getChatId().toString(), "Вы усешно вошли, можете продолжить");
            default:
                log.warn("Inappropriate state, provided" + state + ",expected " + SessionState.WRONG_CODE_ENTERED
                        + "or " + SessionState.AUTHENTICATED);
                throw new IllegalInputException("Неверное состояние, перезапустите сессию /close, потом /start");
        }
    }

    private BotApiMethod<?> processClose(Message message) {
        String userId = extractUserId(message);

        idSessionHashMap.get(userId).close();
        idSessionHashMap.remove(userId);

        return new SendMessage(message.getChatId().toString(), "Сессия успешно завершена");
    }

    private boolean isPhoneNumber(Message message) {
        String messageText = message.getText();
        messageText = messageText.trim();
        return Pattern.matches("9\\d{9}", messageText);
    }

    private boolean isCode(Message message) {
        String messageText = message.getText();
        messageText = messageText.trim();
        return Pattern.matches("\\d{4}", messageText);
    }

    private boolean isGoodUrl(String url) {
        url = url.trim();
        return Pattern.matches("https://www.wildberries.ru/catalog/.*", url);
    }

    private void sendScreenshot(Message message) throws IOException, TelegramFileUploadException {
        String userId = message.getFrom().getId().toString();
        ByteArrayResource resource = new ByteArrayResource(
                Files.readAllBytes(Path.of("src/main/resources/screenshots/screenshot" + userId + ".png"))) {
            @Override
            public String getFilename() {
                return "screenshot" + userId + ".png";
            }
        };
        telegramApiClient.uploadCaptchaScreenshot(message.getChatId().toString(), resource);
    }

    private String extractUserId(Message message) {
        return message.getFrom().getId().toString();
    }

    @Lookup
    public ChromeSession getChromeSession() {
        return null;
    }

}
