package com.lincentpega.javawildberriesselfbuy.controller;

import com.lincentpega.javawildberriesselfbuy.config.TelegramConfigProperties;
import com.lincentpega.javawildberriesselfbuy.service.ChromeSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;


import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.HashMap;

@Component
public class TelegramBot extends SpringWebhookBot{
    private String botPath;
    private String botUsername;
    private String botToken;

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }
}
