package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CheckSessionStateUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CreateSessionUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.in.LogInUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.application.service.CheckSessionStateService;
import com.lincentpega.javawildberriesselfbuy.application.service.CreateSessionService;
import com.lincentpega.javawildberriesselfbuy.application.service.LogInService;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.BotUtils;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.BotStateCache;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.TelegramFileUploadException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnterCaptchaState implements BotState{
    final ApplicationContext context;
    final CheckSessionStateUseCase checkSessionStateUseCase;
    final LogInUseCase logInUseCase;
    final CreateSessionUseCase createSessionUseCase;
    final BotStateCache botStateCache;

    @Autowired
    public EnterCaptchaState(SessionRepository sessionRepository,
                             UserRepository userRepository,
                             ApplicationContext context,
                             BotStateCache botStateCache) {
        this.createSessionUseCase = new CreateSessionService(userRepository, sessionRepository);
        this.checkSessionStateUseCase = new CheckSessionStateService(sessionRepository);
        this.logInUseCase = new LogInService(sessionRepository, userRepository);
        this.context = context;
        this.botStateCache = botStateCache;
    }

    @Override
    public BotApiMethod<?> handle(Update update) throws IllegalInputException, TelegramFileUploadException, IOException {
        Long userId = BotUtils.extractUserId(update);
        if (!update.hasMessage()) {
            throw new IllegalInputException("Неверный тип сообщения");
        }
            logInUseCase.enterCaptcha(update.getMessage().getFrom().getId(), update.getMessage().getText());

            SessionState sessionState = checkSessionStateUseCase.getSessionState(userId);

            if (sessionState == SessionState.PUSH_UP_REQUESTED ||
                    sessionState == SessionState.NOTIFICATION_SENT) {
                createSessionUseCase.requestCodeAsSMS(userId);
            }

            if (sessionState == SessionState.CAPTCHA_CODE_WRONG) {
                context.getBean(EnterNumberState.class).takeScreenshotAndSend(update);
                return new SendMessage(BotUtils.extractChatId(update).toString(), "Капча введена неверно, введите ещё раз");
            }

            botStateCache.saveBotState(userId, context.getBean(EnterConfirmationCodeState.class));

            SendMessage reply = new SendMessage();
            reply.setChatId(BotUtils.extractChatId(update));
            reply.setText("Введите номер, направленный в SMS");
            return reply;
    }
}
