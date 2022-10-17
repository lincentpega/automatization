package com.lincentpega.javawildberriesselfbuy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfigProperties {
    private String botToken;
    private String botUsername;
}
