package com.lincentpega.javawildberriesselfbuy.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Component
@Scope("prototype")
@Log4j2
public class ChromeSession {
    private SessionState state;
    private final WebDriver driver;
    private final LocalDateTime creationDateTime;


    @Autowired
    public ChromeSession(ChromeOptions chromeOptions) {
        this.creationDateTime = LocalDateTime.now();
        this.state = SessionState.SESSION_STARTED;
        this.driver = new ChromeDriver(chromeOptions);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
    }

    public void close() {
        driver.close();
        state = SessionState.SESSION_CLOSED;
    }

    public void openWebsite() {
        String authLink = "https://www.wildberries.ru/security/login";
        driver.get(authLink);
        state = SessionState.SITE_ENTERED;
    }

    public void enterNumber(String number) {

        WebElement numberInputFormBlock = driver.findElement(By.cssSelector("form#spaAuthForm"));
        WebElement numberInputField = numberInputFormBlock.findElement(By.cssSelector("input.input-item"));
        numberInputField.sendKeys(number);

        WebElement submitButton = numberInputFormBlock.findElement(By.cssSelector("button#requestCode"));
        submitButton.click();

        state = SessionState.NUMBER_ENTERED;

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(driver -> isCaptchaAppeared() || isCodeRequested());

        if (isCaptchaAppeared()) {
            state = SessionState.CAPTCHA_APPEARED;
        } else if (isCodeRequested()) {
            if (isPushUpSent())
                state = SessionState.PUSH_UP_REQUESTED;
            else
                state = SessionState.SMS_REQUESTED;
        }
    }

    public void takeScreenshot(String userId) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File("src/main/resources/screenshot" + userId + ".png"));
        } catch (IOException e) {
            log.warn("Unable to save screenshot: " + e);
        }
    }

    public void enterCaptcha(String captchaCode) {
        WebElement captchaInputField = driver.findElement(By.cssSelector("#smsCaptchaCode"));
        captchaInputField.sendKeys(captchaCode);

        WebElement button = driver.findElement(
                By.cssSelector("#spaAuthForm > div > div.login__captcha.form-block.form-block--captcha > button"));
        button.click();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            log.warn(e);
        }

        new WebDriverWait(driver, Duration.ofSeconds(3)).until(driver -> isCaptchaCodeWrong() || isCodeRequested());


        if (isCaptchaAppeared())
            state = SessionState.CAPTCHA_CODE_WRONG;
        if (isCodeRequested()) {
            if (isPushUpSent()) {
                state = SessionState.PUSH_UP_REQUESTED;
            }
            else {
                state = SessionState.SMS_REQUESTED;
            }
        }
    }

    public void enterCode(String code) {
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (state == SessionState.PUSH_UP_REQUESTED) {
            requestCodeAsSMS();
        }

        WebElement codeInputFormBlock = driver.findElement(
                By.cssSelector("form#spaAuthForm")
        );
        WebElement codeInputField = codeInputFormBlock.findElement(
                By.cssSelector("input.j-input-confirm-code.val-msg")
        );
        codeInputField.sendKeys(code);

        state = SessionState.AUTHENTICATED;
    }

    private void requestCodeAsSMS() {
        WebElement requestSMSButton = driver.findElement(By.cssSelector("#requestCode"));
        requestSMSButton.click();
        if (isCaptchaAppeared()) {
            state = SessionState.CAPTCHA_APPEARED;
        } else {
            state = SessionState.SMS_REQUESTED;
        }
    }

    private boolean isCaptchaCodeWrong() {
        try {
            WebElement errorMessageBlock = driver.findElement(
                    By.cssSelector("#spaAuthForm > div > div.login__captcha.form-block.form-block--captcha > p"));
            String errorMessage = errorMessageBlock.getText().trim();
            return errorMessage.contains("Код указан неверно");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isCodeRequested() {
        try {
            WebElement loginCodeTitleBlock = driver.findElement(
                    By.cssSelector("#spaAuthForm > div > div.login__code.form-block > div > input")); // #spaAuthForm > div > div.login__code.form-block > div > input
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isPushUpSent() {
        try {
            WebElement codeMessageBlock = driver.findElement(
                    By.cssSelector("#spaAuthForm > div > div.login__code-head > p"));
            String codeMessage = codeMessageBlock.getText().trim();

            return codeMessage.contains("уже выполнен вход");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isCaptchaAppeared(){
        try {
            driver.findElement(By.cssSelector("div.login__captcha.form-block.form-block--captcha"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getCreationDateTime() {
        return creationDateTime.toString();
    }

    public SessionState getState() {
        return state;
    }
}