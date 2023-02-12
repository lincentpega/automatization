package com.lincentpega.javawildberriesselfbuy.domain;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.lincentpega.javawildberriesselfbuy.application.constants.SelectorEnum.*;
import static com.lincentpega.javawildberriesselfbuy.application.utils.SeleniumUtils.*;

@Log4j2
@AllArgsConstructor
@Getter
public class DriverSession {
    private Long id;
    private WebDriver driver;
    private boolean isAuthenticated;
    private SessionState sessionState;


    public DriverSession(Long id, String userAgent, String proxy, String resolution) {
        this.id = id;

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-gpu", "--ignore-certificate-errors",
                "--disable-extensions", "--no-sandbox", "--disable-dev-shm-usage");

        setUserAgent(chromeOptions, userAgent);
        setProxy(chromeOptions, proxy);
        setResolution(chromeOptions, resolution);

        this.driver = new ChromeDriver(chromeOptions);
        this.isAuthenticated = false;
    }

    public void start(Long number) {
        String authLink = "https://www.wildberries.ru/security/login";
        driver.get(authLink);

        WebElement numberInputFormBlock = waitedFindVisible(driver, 15, INPUT_FORM_BLOCK.getMessage());
        WebElement numberInputField = numberInputFormBlock.findElement(By.cssSelector(NUMBER_INPUT_FIELD.getMessage()));
        numberInputField.sendKeys(number.toString());

        WebElement submitButton = numberInputFormBlock.findElement(By.cssSelector(SUBMIT_BUTTON.getMessage()));
        submitButton.click();
        sessionState = SessionState.NUMBER_ENTERED;
    }

    public void enterCaptcha(String captchaCode) {
        WebElement captchaInputField = waitedFindVisible(driver, 15, CAPTCHA_INPUT_FIELD.getMessage());
        captchaInputField.sendKeys(captchaCode);

        WebElement button = waitedFindClickable(driver, 15, CAPTCHA_SUBMIT_BUTTON.getMessage());
        button.click();
        sessionState = SessionState.CAPTCHA_ENTERED;
    }

    public void enterCode(String code) {
        WebElement codeInputFormBlock = waitedFindVisible(driver, 15, INPUT_FORM_BLOCK.getMessage());
        WebElement codeInputField = codeInputFormBlock.findElement(
                By.cssSelector(CODE_INPUT_FIELD.getMessage())
        );
        codeInputField.clear();
        codeInputField.sendKeys(code);

        isAuthenticated = true;
        sessionState = SessionState.SMS_CODE_ENTERED;
    }

    private void setUserAgent(ChromeOptions chromeOptions, String userAgent) {
        if (userAgent.equals("default")) {
            chromeOptions.addArguments("user-agent=" + "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
        } else {
            chromeOptions.addArguments("user-agent=" + userAgent);
        }
    }

    private void setProxy(ChromeOptions chromeOptions, String proxy) {
        if (!proxy.equals("default")) {
            chromeOptions.addArguments("--proxy-server=" + proxy);
        }
    }

    private void setResolution(ChromeOptions chromeOptions, String resolution) {
        if (resolution.equals("default")) {
            chromeOptions.addArguments("--window-size=1920,1080");
        } else {
            String[] resolutions = resolution.split("x");
            String width = resolutions[0];
            String height = resolutions[1];
            chromeOptions.addArguments("--window-size=" + width + "," + height);
        }
    }

    public void requestCodeAsSMS() { // FIXME: webhook response latency equals 60 seconds then new webhook update will be sent, so, while this func executes new message is incorrectly handled
        WebElement requestSMSButton = waitedFindClickable(driver, 70, REQUEST_SMS_BUTTON.getMessage());
        requestSMSButton.click();

        sessionState = SessionState.REQUESTED_CODE_AS_SMS;
    }

    public void takeScreenshot(Long userId) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File("src/main/resources/screenshots/screenshot" + userId + ".png"));
    }

    public SessionState getState() {
        switch (sessionState) {
            case NUMBER_ENTERED:

                waitSeconds(3);

                if (isCaptchaAppeared()) {
                    sessionState = SessionState.CAPTCHA_APPEARED;
                } else if (isPushUpSent()) {
                    sessionState = SessionState.PUSH_UP_REQUESTED;
                } else if (isNotificationSent()) {
                    sessionState = SessionState.NOTIFICATION_SENT;
                } else {
                    sessionState = SessionState.SMS_REQUESTED;
                }
                break;

            case CAPTCHA_ENTERED:

                waitSeconds(3);

                if (isCaptchaAppeared()) {
                    sessionState = SessionState.CAPTCHA_CODE_WRONG;
                } else if (isPushUpSent()) {
                    sessionState = SessionState.PUSH_UP_REQUESTED;
                } else if (isNotificationSent()) {
                    sessionState = SessionState.NOTIFICATION_SENT;
                } else if (isCodeSent()) {
                    sessionState = SessionState.SMS_REQUESTED;
                }
                break;

            case REQUESTED_CODE_AS_SMS:

                waitSeconds(3);

                if (isCaptchaAppeared()) {
                    sessionState = SessionState.CAPTCHA_APPEARED;
                } else {
                    sessionState = SessionState.SMS_REQUESTED;
                }
                break;

            case SMS_CODE_ENTERED:

                waitSeconds(3);

                if (isCodeWrong()) { // TODO: check if works
                    sessionState = SessionState.WRONG_CODE_ENTERED;
                } else {
                    sessionState = SessionState.AUTHENTICATED;
                }
                break;

            case GOOD_PAGE_REQUESTED:
                if (isGoodPresent()) {
                    sessionState = SessionState.ON_GOOD_PAGE;
                } else {
                    sessionState = SessionState.ON_UNAVAILABLE_URL;
                }
        }
        return sessionState;
    }

    public Set<Cookie> getSessionCookies() {
        return driver.manage().getCookies();
    }

    private void waitSeconds(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.warn(e);
        }
    }

    private boolean isPushUpSent() {
        try {
            WebElement codeMessageBlock = waitedFindVisible(driver, 5, CODE_MESSAGE_BLOCK.getMessage());
            String codeMessage = codeMessageBlock.getText().trim();

            return codeMessage.contains("уже выполнен вход");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isNotificationSent() {
        try {
            WebElement notificationMessageBlock = waitedFindVisible(driver, 5, NOTIFICATION_MESSAGE_BLOCK.getMessage());
            String notificationMessage = notificationMessageBlock.getText().trim();

            return notificationMessage.contains("Код для авторизации отправлен в раздел «Уведомления»");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isOptionsSuggested() {
        return isElementPresent(driver, FIRST_ADDRESS_OPTION.getMessage());
    }

    private boolean isNoExistingAddress() { // searches for "Выбрать адрес доставки" clickable link
        return isElementPresent(driver, ADDRESS_CHOOSE_LINK.getMessage());
    }

    private boolean isGoodHaveSizes() {
        return isElementPresent(driver, ACTIVE_SIZE_BUTTON.getMessage());
    }

    private boolean isCaptchaAppeared() {
        return isElementPresent(driver, CAPTCHA_BLOCK.getMessage());
    }

    private boolean isCodeWrong() {
        return isElementPresent(driver, CODE_WRONG_INDICATOR.getMessage());
    }

    private boolean isCodeSent() {
        return isElementPresent(driver, CODE_SENT_INDICATOR.getMessage());
    }

    private boolean isGoodPresent() {
        return isElementPresent(driver, GOOD_PRESENT_INDICATOR.getMessage());
    }

    public void close() {
        driver.close();
    }
}
