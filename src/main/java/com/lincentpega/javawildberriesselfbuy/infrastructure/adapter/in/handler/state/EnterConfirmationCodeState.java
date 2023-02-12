package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state;

import com.lincentpega.javawildberriesselfbuy.application.port.in.LogInUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.application.service.LogInService;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.BotUtils;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.TelegramFileUploadException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Component
public class EnterConfirmationCodeState implements BotState {
    private final LogInUseCase logInUseCase;

    public EnterConfirmationCodeState(SessionRepository sessionRepository,
                                      UserRepository userRepository) {
        this.logInUseCase = new LogInService(sessionRepository, userRepository);
    }

    @Override
    public BotApiMethod<?> handle(Update update) throws IllegalInputException, IOException, TelegramFileUploadException {
        Long userId = BotUtils.extractUserId(update);
        if (update.hasMessage()) {
            logInUseCase.enterCode(userId, update.getMessage().getText());
            var reply = new SendMessage();
            reply.setText("Вы успешно авторизовались");
            reply.setReplyMarkup(BotUtils.getSessionKeyboard());
            return reply;
        } else {
            throw new IllegalInputException("Неверный тип сообщения");
        }
    }
}
