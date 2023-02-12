package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in;

import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state.BotState;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.BotStateCache;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.TelegramFileUploadException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.IOException;


@Log4j2
@Component
public class TelegramBot extends SpringWebhookBot {
    @Value("${telegram.bot-path}")
    private String botPath;
    @Value("${telegram.bot-username}")
    private String botUsername;
    @Value("${telegram.bot-token}")
    private String botToken;
    BotStateCache botStateCache;

    public TelegramBot(SetWebhook setWebhook,
                       BotStateCache botStateCache) {
        super(setWebhook);
        this.botStateCache = botStateCache;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Long userId;
        if (update.hasMessage()) {
            userId = update.getMessage().getFrom().getId();
        } else {
            userId = update.getCallbackQuery().getFrom().getId();
        }

        BotState botState = botStateCache.getBotState(userId);
        try {
            return botState.handle(update);
        } catch (IllegalInputException | IOException | TelegramFileUploadException e) {
            return new SendMessage(update.getMessage().getChatId().toString(), e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }
}
