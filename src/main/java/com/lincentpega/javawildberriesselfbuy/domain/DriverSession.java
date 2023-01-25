package com.lincentpega.javawildberriesselfbuy.domain;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static com.lincentpega.javawildberriesselfbuy.application.utils.SeleniumUtils.isElementPresent;
import static com.lincentpega.javawildberriesselfbuy.application.utils.SeleniumUtils.waitedFindVisible;
import static com.lincentpega.javawildberriesselfbuy.application.constants.SelectorEnum.*;

@Log4j2
@AllArgsConstructor
public class DriverSession {
    private WebDriver driver;
    private SessionState sessionState;
    private boolean isAuthenticated;

    public void updateState() {
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
}
