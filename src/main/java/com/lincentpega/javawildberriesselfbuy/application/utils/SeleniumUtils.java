package com.lincentpega.javawildberriesselfbuy.application.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumUtils {
    public static WebElement waitedFindVisible(WebDriver driver, int seconds, String elementCssSelector) throws TimeoutException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(elementCssSelector)));
        return driver.findElement(By.cssSelector(elementCssSelector));
    }

    public static WebElement waitedFindClickable(WebDriver driver, int seconds, String elementCssSelector) throws TimeoutException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(elementCssSelector)));
        return driver.findElement(By.cssSelector(elementCssSelector));
    }

    public static boolean isElementPresent(WebDriver driver, String elementCssSelector) {
        try {
            waitedFindVisible(driver, 5, elementCssSelector);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
