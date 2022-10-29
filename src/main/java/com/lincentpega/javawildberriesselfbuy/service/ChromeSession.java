package com.lincentpega.javawildberriesselfbuy.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
    }

    public void takeScreenshot(String userId) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File("src/main/resources/screenshots/screenshot" + userId + ".png"));
    }

    public void enterCaptcha(String captchaCode) {
        WebElement captchaInputField = driver.findElement(By.cssSelector("#smsCaptchaCode"));
        captchaInputField.sendKeys(captchaCode);

        WebElement button = driver.findElement(
                By.cssSelector("#spaAuthForm > div > div.login__captcha.form-block.form-block--captcha > button"));
        button.click();

        state = SessionState.CAPTCHA_ENTERED;
    }

    public void enterCode(String code) {
        WebElement codeInputFormBlock = driver.findElement(
                By.cssSelector("form#spaAuthForm")
        );
        WebElement codeInputField = codeInputFormBlock.findElement(
                By.cssSelector("input.j-input-confirm-code.val-msg")
        );
        codeInputField.sendKeys(code);

        state = SessionState.SMS_CODE_ENTERED;
    }

    public String getCreationDateTime() {
        return creationDateTime.toString();
    }

    public SessionState getUpdatedState() {
        updateState();
        return state;
    }

    public void requestCodeAsSMS() {
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            log.warn(e);
        }

        WebElement requestSMSButton = driver.findElement(By.cssSelector("#requestCode"));
        requestSMSButton.click();

        state = SessionState.REQUESTED_CODE_AS_SMS;
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

    private boolean isCaptchaAppeared() {
        try {
            driver.findElement(By.cssSelector("#smsCaptchaCode"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isCodeWrong() {
        try {
            driver.findElement(By.cssSelector("#spaAuthForm > div > div.login__code.form-block > p:nth-child(7)"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isCodeSent() {
        try {
            driver.findElement(By.cssSelector("#spaAuthForm > div > div.login__code.form-block > div > input")); // FIXME: проверить на индивадуальность селектора
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void updateState() {
        switch (state) {
            case NUMBER_ENTERED:
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    log.warn(e);
                }

                if (isCaptchaAppeared()) {
                    state = SessionState.CAPTCHA_APPEARED;
                } else if (isPushUpSent()) {
                    state = SessionState.PUSH_UP_REQUESTED;
                } else  {
                    state = SessionState.SMS_REQUESTED;
                }
                break;

            case CAPTCHA_ENTERED:

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    log.warn(e);
                }

                if (isCaptchaAppeared()) {
                    state = SessionState.CAPTCHA_CODE_WRONG;
                } else if (isPushUpSent()) {
                    state = SessionState.PUSH_UP_REQUESTED;
                } else if (isCodeSent()) {
                    state = SessionState.SMS_REQUESTED;
                }
                break;

            case REQUESTED_CODE_AS_SMS: //FIXME: капча может появляться после запроса кода через смс, обработать случай
                if (isCaptchaAppeared()) {
                    state = SessionState.CAPTCHA_APPEARED;
                } else {
                    state = SessionState.SMS_REQUESTED;
                }
                break;

            case SMS_CODE_ENTERED:

                if (isCodeWrong()) {
                    state = SessionState.WRONG_CODE_ENTERED;
                } else {
                    state = SessionState.AUTHENTICATED;
                }
        }
    }
}