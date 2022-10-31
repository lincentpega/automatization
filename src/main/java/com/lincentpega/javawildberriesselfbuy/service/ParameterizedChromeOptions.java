package com.lincentpega.javawildberriesselfbuy.service;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Log4j2
@Component("parameterizedChromeOptions")
public class ParameterizedChromeOptions extends ChromeOptions {
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36";
    private final boolean isHeadless = false;

    public ParameterizedChromeOptions() {
        super();
        this.addArguments(userAgent);
        if (isHeadless) this.addArguments("--headless");
        this.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors",
                "--disable-extensions", "--no-sandbox", "--disable-dev-shm-usage");
    }
}
