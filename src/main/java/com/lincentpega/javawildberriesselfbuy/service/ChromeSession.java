package com.lincentpega.javawildberriesselfbuy.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.LocalDateTime;

public class ChromeSession {
    private final String userId;
    private final WebDriver driver;
    private final LocalDateTime creationDateTime;

    public ChromeSession(String userId, ChromeOptions chromeOptions) {
        this.userId = userId;
        this.driver = new ChromeDriver(chromeOptions);
        this.creationDateTime = LocalDateTime.now();
    }

    public void close() {
        this.driver.close();
    }

    public void openWebsite() {
        String authLink = "https://www.wildberries.ru/security/login";
        driver.get(authLink);
    }

    public void enterNumber(String number) {
        WebElement numberInputFormBlock = driver.findElement(
                By.cssSelector("form#spaAuthForm"));

        WebElement numberInputField = numberInputFormBlock.findElement(
                By.cssSelector("input.input-item"));
        numberInputField.sendKeys(number);

        WebElement submitButton = numberInputFormBlock.findElement(
                By.cssSelector("button#requestCode"));
        submitButton.click();
    }

    public String getUserId() {
        return userId;
    }

    public String getCreationDateTime() {
        return creationDateTime.toString();
    }
}
