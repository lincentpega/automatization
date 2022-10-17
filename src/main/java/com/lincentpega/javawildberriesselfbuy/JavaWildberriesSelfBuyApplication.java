package com.lincentpega.javawildberriesselfbuy;

import com.lincentpega.javawildberriesselfbuy.config.ChromeOptionsConfig;
import com.lincentpega.javawildberriesselfbuy.config.TelegramConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({TelegramConfigProperties.class})
public class JavaWildberriesSelfBuyApplication {

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");
		SpringApplication.run(JavaWildberriesSelfBuyApplication.class, args);
	}
}
