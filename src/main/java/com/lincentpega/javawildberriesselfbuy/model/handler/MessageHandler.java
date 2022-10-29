package com.lincentpega.javawildberriesselfbuy.model.handler;

import com.lincentpega.javawildberriesselfbuy.constants.BotMessageEnum;
import com.lincentpega.javawildberriesselfbuy.controller.TelegramApiClient;
import com.lincentpega.javawildberriesselfbuy.exceptions.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.exceptions.TelegramFileUploadException;
import com.lincentpega.javawildberriesselfbuy.service.ChromeSession;
import com.lincentpega.javawildberriesselfbuy.service.SessionState;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriverException;
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

    public BotApiMethod<?> handleUpdate(Update update) throws IllegalInputException, WebDriverException {
        Message message = update.getMessage();

        if (message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    return processStart(message);
                case "/state":
                    return processState(message);
                case "/close":
                    return processClose(message);
                default:
                    return handlePlainMessage(message);
            }
        } else {
            throw new IllegalInputException("Сообщение пусто");
        }
    }

    private BotApiMethod<?> processStart(Message message) throws IllegalInputException {
        String userId = extractUserId(message);

        if (!idSessionHashMap.containsKey(userId)) {
            ChromeSession chromeSession = getChromeSession();
            chromeSession.openWebsite();

            idSessionHashMap.put(userId, chromeSession);

            return new SendMessage(message.getChatId().toString(), BotMessageEnum.SESSION_CREATED.getMessage());
        } else {
            throw new IllegalInputException(BotMessageEnum.SESSION_ALREADY_EXISTS.getMessage());
        }
    }

    private BotApiMethod<?> processState(Message message) throws IllegalInputException {
        String userId = extractUserId(message);

        if (idSessionHashMap.containsKey(userId)) {
            String creationDateTime = idSessionHashMap.get(userId).getCreationDateTime();

            return new SendMessage(message.getChatId().toString(), "Сессия была создана " + creationDateTime);
        } else {
            throw new IllegalInputException(BotMessageEnum.SESSION_DOESNT_EXIST.getMessage());
        }
    }

    private BotApiMethod<?> processClose(Message message) throws IllegalInputException {
        String userId = extractUserId(message);

        if (idSessionHashMap.containsKey(userId)) {
            idSessionHashMap.get(userId).close();
            idSessionHashMap.remove(userId);
            return new SendMessage(message.getChatId().toString(), "Сессия успешно завершена");
        } else {
            throw new IllegalInputException(BotMessageEnum.SESSION_DOESNT_EXIST.getMessage());
        }
    }

    private BotApiMethod<?> handlePlainMessage(Message message) throws IllegalInputException {
        String userId = extractUserId(message);

        if (idSessionHashMap.containsKey(userId)) {
            ChromeSession userSession = idSessionHashMap.get(userId);
            SessionState sessionState = userSession.getState();

            if (sessionState == SessionState.SITE_ENTERED && isPhoneNumber(message)) {
                return processNumber(message, userSession);
            } else if (sessionState == SessionState.CAPTCHA_APPEARED || sessionState == SessionState.CAPTCHA_CODE_WRONG) {
                return processCaptcha(message, userSession);
            } else {
                throw new IllegalInputException(BotMessageEnum.ILLEGAL_INPUT.getMessage());
            }
        } else {
            throw new IllegalInputException(BotMessageEnum.SESSION_DOESNT_EXIST.getMessage());
        }
    }

    private boolean isPhoneNumber(Message message) {
        String messageText = message.getText();
        messageText = messageText.trim();
        return Pattern.matches("9\\d{9}", messageText);
    }

    private BotApiMethod<?> processNumber(Message message, ChromeSession userSession) {
        String userId = extractUserId(message);
        userSession.enterNumber(message.getText());

        if (userSession.getState() == SessionState.CAPTCHA_APPEARED) {
            userSession.takeScreenshot(userId);
            try {
                sendScreenshot(message);
            } catch (IOException | TelegramFileUploadException e) {
                return new SendMessage(message.getChatId().toString(), "Проблема с загрузкой скриншота, обратитесь к разработчику");
            }
            return new SendMessage(message.getChatId().toString(), "Введите капчу с картинки");
        } else {
            return new SendMessage(message.getChatId().toString(), "Номер введён успешно, введите код подтверждения");
        }
    }

    private BotApiMethod<?> processCaptcha(Message message, ChromeSession userSession) {
        String userId = extractUserId(message);
        userSession.enterCaptcha(message.getText());

        if (userSession.getState() == SessionState.CAPTCHA_CODE_WRONG) {
            userSession.takeScreenshot(userId);
            try {
                sendScreenshot(message);
            } catch (IOException | TelegramFileUploadException e) {
                return new SendMessage(message.getChatId().toString(), "Проблема с загрузкой скриншота, обратитесь к разработчику");
            }
            return new SendMessage(message.getChatId().toString(),
                    "Введите капчу с картинки");
        } else {
            return new SendMessage(message.getChatId().toString(),
                    "Капча введена верно, введите код подтверждения, направленный в SMS");
        }
    }

    private void sendScreenshot(Message message) throws IOException, TelegramFileUploadException {
        String userId = message.getFrom().getId().toString();
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(Path.of("src/main/resources/screenshot" + userId + ".png"))) {
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
