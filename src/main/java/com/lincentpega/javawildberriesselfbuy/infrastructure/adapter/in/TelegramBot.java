package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in;

import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.UpdateHandler;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;


@Log4j2
@Component
public class TelegramBot extends SpringWebhookBot {
    @Value("${telegram.bot-path}")
    private String botPath;
    @Value("${telegram.bot-username}")
    private String botUsername;
    @Value("${telegram.bot-token}")
    private String botToken;
    UpdateHandler updateHandler;

    public TelegramBot(SetWebhook setWebhook,
                       UpdateHandler updateHandler) {
        super(setWebhook);
        this.updateHandler = updateHandler;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return updateHandler.handleUpdate(update);
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
