package com.lincentpega.javawildberriesselfbuy.service;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ParameterizedChromeOptions extends ChromeOptions {
    @Value("${chromedriver.user-agent}")
    private String userAgent;
    @Value("${chromedriver.isheadless}")
    private boolean isHeadless;

    public ParameterizedChromeOptions() {
        super();
        this.addArguments("user-agent=" + userAgent);
        if (isHeadless) this.addArguments("--headless");
        this.addArguments("--disable-gpu");
    }
}
