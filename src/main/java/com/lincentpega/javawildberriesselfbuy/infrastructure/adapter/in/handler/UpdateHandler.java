package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler;

import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.domain.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class UpdateHandler {
    UserRepository userRepository;

    public UpdateHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        Long userId = getIdFromUpdate(update);
        Optional<User> savedUser = userRepository.findById(userId);
        if (savedUser.isEmpty()) {
            User user = new User();
            user.setUserId(userId);
        }
        return new SendMessage(update.getMessage().getChatId().toString(), "Hello, user added");
    }

    private Long getIdFromUpdate(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else {
            return update.getCallbackQuery().getFrom().getId();
        }
    }
}
