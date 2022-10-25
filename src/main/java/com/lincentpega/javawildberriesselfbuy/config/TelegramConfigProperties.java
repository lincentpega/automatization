package com.lincentpega.javawildberriesselfbuy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfigProperties {
    private String botPath;
    private String botUsername;
    private String botToken;
}
