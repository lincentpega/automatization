package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state;

import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.domain.User;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.BotUtils;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SetUserAgentState implements BotState {
    private ApplicationContext context;
    private UserRepository userRepository;

    @Override
    @Transactional
    public BotApiMethod<?> handle(Update update) throws IllegalInputException {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long userId = message.getFrom().getId();
            String userAgent = update.getMessage().getText();

            User user = userRepository.findById(userId).orElseGet(() -> userRepository.save(new User(userId)));
            user.getSessionParams().setUserAgent(userAgent);
            userRepository.save(user);

            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text("User-Agent установлен: " + user.getSessionParams().getUserAgent())
                    .replyMarkup(BotUtils.getStartKeyboard())
                    .build();

        } else if (update.hasCallbackQuery()) {
            DefaultState defaultState = context.getBean(DefaultState.class);
            return defaultState.handle(update);

        } else {
            throw new IllegalInputException();
        }
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}