package com.lincentpega.javawildberriesselfbuy.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramConfigProperties {
    @Value("${telegram.bot-path}")
    String botPath;
    @Value("${telegram.bot-username}")
    String botUsername;
    @Value("${telegram.bot-token}")
    String botToken;
}
