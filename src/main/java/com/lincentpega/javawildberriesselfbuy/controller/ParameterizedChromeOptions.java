package com.lincentpega.javawildberriesselfbuy.controller;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ParameterizedChromeOptions extends ChromeOptions {
    @Value("${chromedriver.user-agent}")
    private String userAgent;
    @Value("${chromedriver.isheadless}")
    private boolean isHeadless;

    private ParameterizedChromeOptions() {
        super();
        this.addArguments("user-agent=" + userAgent);
        if (isHeadless) this.addArguments("--headless");
        this.addArguments("--disable-gpu");
    }
}
