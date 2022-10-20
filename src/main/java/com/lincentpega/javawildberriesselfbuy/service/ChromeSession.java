package com.lincentpega.javawildberriesselfbuy.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Scope("prototype")
public class ChromeSession {
    private final WebDriver driver;
    private final LocalDateTime creationDateTime;

    @Autowired
    public ChromeSession(@Qualifier("parameterizedChromeOptions") ChromeOptions chromeOptions) {
        this.creationDateTime = LocalDateTime.now();
        this.driver = new ChromeDriver(chromeOptions);
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

    public String getCreationDateTime() {
        return creationDateTime.toString();
    }
}
