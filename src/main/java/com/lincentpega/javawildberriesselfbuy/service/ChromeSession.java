package com.lincentpega.javawildberriesselfbuy.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;


@Component
@Scope("prototype")
@Log4j2
public class ChromeSession {
    private SessionState state;
    private final WebDriver driver;
    private final LocalDateTime creationDateTime;


    @Autowired
    public ChromeSession(ChromeOptions chromeOptions) {
        ParameterizedChromeOptions parameterizedChromeOptions = (ParameterizedChromeOptions) chromeOptions;
        log.info(parameterizedChromeOptions.toString());
        this.creationDateTime = LocalDateTime.now();
        this.driver = new ChromeDriver(chromeOptions);
        this.state = SessionState.SESSION_STARTED;
    }

    public void close() {
        driver.close();
        state = SessionState.SESSION_CLOSED;
    }

    public void openWebsite() {
        Assert.state(state == SessionState.SESSION_STARTED,
                "Expected " + SessionState.SESSION_STARTED + " state, got "+ state + " state");

        String authLink = "https://www.wildberries.ru/security/login";
        driver.get(authLink);
        state = SessionState.SITE_ENTERED;
    }

    public void enterNumber(String number) throws NoSuchElementException{
        Assert.state(state == SessionState.SITE_ENTERED,
                "Expected "  + SessionState.SITE_ENTERED + " state, got "+ state + " state");

        WebElement numberInputFormBlock = driver.findElement(By.cssSelector("form#spaAuthForm"));
        WebElement numberInputField = numberInputFormBlock.findElement(By.cssSelector("input.input-item"));
        numberInputField.sendKeys(number);

        WebElement submitButton = numberInputFormBlock.findElement(By.cssSelector("button#requestCode"));
        submitButton.click();

        state = SessionState.NUMBER_ENTERED;

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
            FileUtils.copyFile(screenshot, new File("screenshot" + userId + ".png"));
        } catch (IOException e) {
            log.log(Level.WARN, "Unable to save screenshot: " + e);
        }
    }

    public void enterCaptcha(String captchaCode) throws NoSuchElementException{
        Assert.state(state == SessionState.CAPTCHA_APPEARED,
                "Expected " + SessionState.CAPTCHA_APPEARED + " state, got "+ state + " state");

        WebElement captchaInputField = driver.findElement(By.cssSelector("#smsCaptchaCode"));
        captchaInputField.sendKeys(captchaCode);

        WebElement button = driver.findElement(
                By.cssSelector("#spaAuthForm > div > div.login__captcha.form-block.form-block--captcha > button"));
        button.click();

        if (isCaptchaCodeWrong())
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

    public void enterCode(String code) throws NoSuchElementException {
        Assert.state(state == SessionState.SMS_REQUESTED || state == SessionState.PUSH_UP_REQUESTED,
                "Expected " + SessionState.SMS_REQUESTED + " or " + SessionState.PUSH_UP_REQUESTED
                        + " states, got "+ state + " state");

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

    private void requestCodeAsSMS() throws NoSuchElementException {
        Assert.state(state == SessionState.PUSH_UP_REQUESTED,
                "Expected " + SessionState.SMS_REQUESTED + " state, got "+ state + " state");

        WebElement requestSMSButton = driver.findElement(By.cssSelector("#requestCode"));
        requestSMSButton.click();
        if (isCaptchaAppeared()) {
            state = SessionState.CAPTCHA_APPEARED;
        } else {
            state = SessionState.SMS_REQUESTED;
        }
    }

    private boolean isCaptchaCodeWrong() {
        Assert.state(state == SessionState.CAPTCHA_APPEARED, "Expected "
                + SessionState.CAPTCHA_APPEARED + " state, got "+ state + " state");

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
        Assert.state(state == SessionState.CAPTCHA_APPEARED || state == SessionState.NUMBER_ENTERED,
                "Expected " + SessionState.CAPTCHA_APPEARED + " or "
                        +  SessionState.NUMBER_ENTERED + " states, got "+ state + " state");

        try {
            WebElement loginCodeTitleBlock = driver.findElement(
                    By.cssSelector("#spaAuthForm > div > div.login__code-head > h2"));
            String loginCodeTitle = loginCodeTitleBlock.getText().trim();
            return loginCodeTitle.contains("Введите код");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isPushUpSent() {
        Assert.state(state == SessionState.CAPTCHA_APPEARED || state == SessionState.NUMBER_ENTERED,
                "Expected " + SessionState.CAPTCHA_APPEARED + " or "
                        +  SessionState.NUMBER_ENTERED + " states, got "+ state + " state");
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
        Assert.state(state == SessionState.NUMBER_ENTERED,
                "Expected " + SessionState.NUMBER_ENTERED + " state, got "+ state + " state");
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