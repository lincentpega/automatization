package com.lincentpega.javawildberriesselfbuy.controller;

import org.openqa.selenium.chrome.ChromeOptions;

public class ParameterizedChromeOptions extends ChromeOptions {
    private final static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36";
    private final static boolean isHeadless = true;
    static ChromeOptions chromeOptions = new ChromeOptions();

    static {
        chromeOptions.addArguments("user-agent=" + userAgent);
        if (isHeadless) chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-gpu");
    }

    public static ChromeOptions getChromeOptions() {
        return chromeOptions;
    }
}
