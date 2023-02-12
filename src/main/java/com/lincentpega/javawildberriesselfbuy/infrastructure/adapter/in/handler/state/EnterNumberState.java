package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CheckSessionStateUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CreateSessionUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.application.service.CheckSessionStateService;
import com.lincentpega.javawildberriesselfbuy.application.service.CreateSessionService;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.BotUtils;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.BotStateCache;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.TelegramApiClient;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.TelegramFileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Component
public class EnterNumberState implements BotState {
    private final CheckSessionStateUseCase checkSessionStateUseCase;
    private final CreateSessionUseCase createSessionUseCase;
    private final BotStateCache botStateCache;
    private final ApplicationContext applicationContext;
    private final TelegramApiClient telegramApiClient;


    @Autowired
    public EnterNumberState(UserRepository userRepository,
                            SessionRepository sessionRepository,
                            BotStateCache botStateCache,
                            ApplicationContext applicationContext,
                            TelegramApiClient telegramApiClient) {
        this.createSessionUseCase = new CreateSessionService(userRepository, sessionRepository);
        this.botStateCache = botStateCache;
        this.applicationContext = applicationContext;
        this.telegramApiClient = telegramApiClient;
        this.checkSessionStateUseCase = new CheckSessionStateService(sessionRepository);
    }

    @Override
    public BotApiMethod<?> handle(Update update) throws IllegalInputException, IOException, TelegramFileUploadException {
        Long userId = BotUtils.extractUserId(update);

        String number;
        if (update.hasMessage()) {
            number = update.getMessage().getText().trim();
        } else if (update.hasCallbackQuery()) {
            number = update.getCallbackQuery().getData().trim();
        } else {
            throw new IllegalInputException("Неверный тип сообщения");
        }

        if (isNumberWrong(number)) {
            throw new IllegalInputException("Введен неверный номер, введите номер заново, начиная с цифры 9");
        }

        createSessionUseCase.createSession(userId, Long.valueOf(number));

        SessionState sessionState = checkSessionStateUseCase.getSessionState(userId);
        if (sessionState == SessionState.PUSH_UP_REQUESTED ||
        sessionState == SessionState.NOTIFICATION_SENT) {
            createSessionUseCase.requestCodeAsSMS(userId);
        }

        if (checkSessionStateUseCase.getSessionState(userId) == SessionState.CAPTCHA_APPEARED) {
            botStateCache.saveBotState(userId, applicationContext.getBean(EnterCaptchaState.class));
            takeScreenshotAndSend(update);
            return new SendMessage(BotUtils.extractChatId(update).toString(), "Введите капчу с картинки");
        }

        botStateCache.saveBotState(userId, applicationContext.getBean(EnterConfirmationCodeState.class));
        SendMessage reply = new SendMessage();
        reply.setText("Введите код подтверждения, направленный в СМС");
        reply.setChatId(BotUtils.extractChatId(update));
        return reply;
    }

    public void takeScreenshotAndSend(Update update) throws IllegalInputException, IOException, TelegramFileUploadException {
        Long userId = BotUtils.extractUserId(update);
        createSessionUseCase.takeScreenshot(userId);

        Path screenshotPath = Path.of("src/main/resources/screenshots/screenshot" + userId + ".png");
        ByteArrayResource resource = new ByteArrayResource(
                Files.readAllBytes(screenshotPath)) {
            @Override
            public String getFilename() {
                return "screenshot" + userId + ".png";
            }
        };
        telegramApiClient.sendPhoto(BotUtils.extractChatId(update).toString(), resource);
    }
    private boolean isNumberWrong(String number) {
        return !Pattern.matches("9\\d{9}", number);
    }
}