package com.lincentpega.javawildberriesselfbuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TelegramConfigProperties.class)
public class JavaWildberriesSelfBuyApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaWildberriesSelfBuyApplication.class, args);
	}
}
