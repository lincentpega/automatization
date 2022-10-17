package com.lincentpega.javawildberriesselfbuy.config;

import lombok.Getter;
import org.openqa.selenium.chrome.ChromeOptions;

@Getter
public class ChromeOptionsConfig {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36";
    private static final boolean isHeadless = true;

    public static ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=" + userAgent);
        if (isHeadless) options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        return options;
    }
}
