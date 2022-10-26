package com.lincentpega.javawildberriesselfbuy.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class ApplicationConfig {
    private final TelegramConfigProperties telegramConfigProperties;

    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(telegramConfigProperties.getBotPath()).build();
    }


}
