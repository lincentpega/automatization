package com.lincentpega.javawildberriesselfbuy.infrastructure.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.util.WebhookUtils;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
public class WebhookConfig {
    @Value("${telegram.bot-path}")
    private String botPath;

    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(botPath).build();
    }
}