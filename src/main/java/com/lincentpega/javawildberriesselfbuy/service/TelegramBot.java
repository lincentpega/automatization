package com.lincentpega.javawildberriesselfbuy.service;

import com.lincentpega.javawildberriesselfbuy.config.TelegramConfigProperties;

import com.lincentpega.javawildberriesselfbuy.constants.BotMessageEnum;
import com.lincentpega.javawildberriesselfbuy.exceptions.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.service.handler.MessageHandler;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;


@Log4j2
@Component
public class TelegramBot extends SpringWebhookBot {
    private final String botPath;
    private final String botUsername;
    private final String botToken;

    private final MessageHandler messageHandler;

    public TelegramBot(SetWebhook setWebhook, TelegramConfigProperties telegramConfigProperties,
                       MessageHandler messageHandler) {
        super(setWebhook);
        this.botPath = telegramConfigProperties.getBotPath();
        this.botUsername = telegramConfigProperties.getBotUsername();
        this.botToken = telegramConfigProperties.getBotToken();
        this.messageHandler = messageHandler;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            return handleUpdate(update);
        } catch (IllegalInputException e) {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    e.getMessage());
        } catch (Exception e) {
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (StackTraceElement element : stackTraceElements) {
                log.error(element);
            }
            log.error(e);
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.UNKNOWN_EXCEPTION.getMessage());
        }
    }

    private BotApiMethod<?> handleUpdate(Update update) throws IllegalInputException {
        if (update.hasMessage()) {
            return messageHandler.handleUpdate(update);
        } else {
            throw new IllegalInputException("Неверный тип сообщения");
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
