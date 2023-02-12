package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state;

import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.domain.User;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.BotUtils;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.DefaultCallBackHandler;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.DefaultMessageHandler;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DefaultState implements BotState {
    DefaultMessageHandler defaultMessageHandler;
    DefaultCallBackHandler defaultCallBackHandler;
    UserRepository userRepository;

    @Override
    public BotApiMethod<?> handle(Update update) throws IllegalInputException {
        Long userId = BotUtils.extractUserId(update);
        if (userRepository.findById(userId).isEmpty()) {
            userRepository.save(new User(userId));
        }

        if (update.hasMessage()) {
            return defaultMessageHandler.handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            return defaultCallBackHandler.handleCallBack(update.getCallbackQuery());
        } else throw new IllegalInputException();
    }

    @Autowired
    public void setMessageHandler(DefaultMessageHandler defaultMessageHandler) {
        this.defaultMessageHandler = defaultMessageHandler;
    }

    @Autowired
    public void setCallBackHandler(DefaultCallBackHandler defaultCallBackHandler) {
        this.defaultCallBackHandler = defaultCallBackHandler;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
