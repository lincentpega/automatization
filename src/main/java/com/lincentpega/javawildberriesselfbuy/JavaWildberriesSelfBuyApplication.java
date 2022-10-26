package com.lincentpega.javawildberriesselfbuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaWildberriesSelfBuyApplication {

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");
		SpringApplication.run(JavaWildberriesSelfBuyApplication.class, args);
	}
}
