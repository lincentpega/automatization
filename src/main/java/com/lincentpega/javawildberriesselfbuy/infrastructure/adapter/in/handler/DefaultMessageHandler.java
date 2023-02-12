package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler;

import com.lincentpega.javawildberriesselfbuy.application.constants.BotMessageEnum;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CreateSessionUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.application.service.CreateSessionService;
import com.lincentpega.javawildberriesselfbuy.domain.SessionParams;
import com.lincentpega.javawildberriesselfbuy.domain.User;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.BotUtils;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@AllArgsConstructor
public class DefaultMessageHandler {
    private final CreateSessionUseCase createSessionUseCase;
    private final UserRepository userRepository;

    @Autowired
    public DefaultMessageHandler(UserRepository userRepository,
                                 SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.createSessionUseCase = new CreateSessionService(userRepository, sessionRepository);
    }

    public BotApiMethod<?> handleMessage(Message message) throws IllegalInputException {
        SendMessage reply = new SendMessage();
        reply.setChatId(message.getChatId());

        switch (message.getText()) {
            case "/start":
                if (createSessionUseCase.isSessionPresent(message.getFrom().getId())) {
                    return new SendMessage(message.getChatId().toString(), BotMessageEnum.SESSION_ALREADY_EXISTS.getMessage());
                }

                InlineKeyboardMarkup startKeyboard = BotUtils.getStartKeyboard();
                reply.setReplyMarkup(startKeyboard);
                User user = userRepository.findById(message.getFrom().getId())
                        .orElseGet(() -> userRepository.save(new User(message.getFrom().getId())));
                SessionParams userSessionParams = user.getSessionParams();
                reply.setText("Текущая конфигурация сессии:\n" +
                        "User-Agent: " + userSessionParams.getUserAgent() + "\n" +
                        "Разрешение: " + userSessionParams.getResolution() + "\n" +
                        "Прокси: " + userSessionParams.getProxy());
                return reply;
        }
        throw new IllegalInputException("Неверный тип сообщения");
    }


}
