package com.lincentpega.javawildberriesselfbuy.service;

import com.lincentpega.javawildberriesselfbuy.constants.SessionState;
import com.lincentpega.javawildberriesselfbuy.model.User;
import com.lincentpega.javawildberriesselfbuy.dto.CookieDto;
import com.lincentpega.javawildberriesselfbuy.repository.UserRepository;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Component
@Scope("prototype")
@Log4j2
public class ChromeSession {
    private SessionState state;
    private String number;
    private final WebDriver driver;
    private final LocalDateTime creationDateTime;
    private final UserRepository userRepository;


    @Autowired
    public ChromeSession(ChromeOptions chromeOptions, UserRepository userRepository) {
        this.creationDateTime = LocalDateTime.now();
        this.state = SessionState.SESSION_STARTED;
        this.driver = new ChromeDriver(chromeOptions);
        this.userRepository = userRepository;

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

    public void openHomePage() {
        String link = "https://www.wildberries.ru";
        driver.get(link);
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

    public void addGoodToCart(String url) {
        driver.get(url);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.warn(e);
        }
        addToCart();
        driver.get("https://www.wildberries.ru/lk/basket");
    }

    public void chooseAddress(String address) {
        toAddressChoose();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.warn(e);
        }

        inputAddress(address);
        state = SessionState.ADDRESS_SENT;
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
            TimeUnit.SECONDS.sleep(70);
        } catch (InterruptedException e) {
            log.warn(e);
        }

        WebElement requestSMSButton = driver.findElement(By.cssSelector("#requestCode"));
        requestSMSButton.click();

        state = SessionState.REQUESTED_CODE_AS_SMS;
    }

    public void saveCookies() {
        Cookie authCookie = driver.manage().getCookieNamed("WILDAUTHNEW_V3");
        HashSet<Cookie> cookies = new HashSet<>();
        cookies.add(authCookie);
        User user = new User(number, cookies);
        userRepository.save(user);
    }

    public boolean isCookiesExist() {
        return userRepository.existsById(number);
    }

    public void uploadCookies() {
        if (userRepository.existsById(number)) {
            Optional<User> optionalUser = userRepository.findById(number);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                var cookies = user.getCookies();
                for (CookieDto cookie : cookies) {
                    driver.manage().addCookie(
                            new Cookie(cookie.getName(), //TODO: Add cookies expiration date check
                                    cookie.getValue(),
                                    cookie.getDomain(),
                                    cookie.getPath(),
                                    cookie.getExpiry(),
                                    cookie.isSecure(),
                                    cookie.isHttpOnly(),
                                    cookie.getSameSite())
                    );
                }
            }
        }
    }

    public void authByCookies() {

    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private void addToCart() {
        WebElement addToCartButton = driver.findElement(
                By.cssSelector("div > div.product-page__aside-container.j-price-block > div:nth-child(2) > div > button:nth-child(2)"));

        addToCartButton.click();
    }

    private void toOffer() {
        WebElement offerButton = driver.findElement(
                By.cssSelector("#basketForm > div.basket-form__sidebar.sidebar > div > div > div > div.basket-order__b-btn.b-btn > button"));
        offerButton.click();
    }

    private void toAddressChoose() {
        WebElement addressChooseLink = driver.findElement(
                By.cssSelector("div.basket-delivery__choose-address.j-btn-choose-address"));
        addressChooseLink.click();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.warn(e);
        }

        WebElement addressChooseButton = driver.findElement(
                By.cssSelector("body > div.popup.i-popup-choose-address.shown > div > div > " +
                        "div.basket-delivery__methods > div.contents > div.contents__item.contents__self.active > " +
                        "div > div.popup__btn > button"));
        addressChooseButton.click();
    }

    private void inputAddress(String address) {
        WebElement inputField = driver.findElement(
                By.cssSelector("ymaps > ymaps.ymaps-2-1-79-searchbox__input-cell > " +
                        "ymaps.ymaps-2-1-79-searchbox-input > input"));
        inputField.sendKeys(address);

        WebElement findButton = driver.findElement(
                By.cssSelector("ymaps > ymaps.ymaps-2-1-79-searchbox__button-cell > ymaps"));
        findButton.click();
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

    private boolean isNotificationSent() {
        try {
            WebElement notificationMessageBlock = driver.findElement(
                    By.cssSelector("#spaAuthForm > div > div.login__code-head > p"));
            String notificationMessage = notificationMessageBlock.getText().trim();

            return notificationMessage.contains("Код для авторизации отправлен в раздел «Уведомления»");
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
            driver.findElement(By.cssSelector("#spaAuthForm > div > div.login__code.form-block > div > input"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isGoodPresent() {
        try {
            driver.findElement(By.cssSelector("div.product-page__header-wrap"));
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
                } else if (isNotificationSent()) {
                    state = SessionState.NOTIFICATION_SENT;
                } else {
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
                } else if (isNotificationSent()) {
                    state = SessionState.NOTIFICATION_SENT;
                } else if (isCodeSent()) {
                    state = SessionState.SMS_REQUESTED;
                }
                break;

            case REQUESTED_CODE_AS_SMS:

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    log.warn(e);
                }

                if (isCaptchaAppeared()) {
                    state = SessionState.CAPTCHA_APPEARED;
                } else {
                    state = SessionState.SMS_REQUESTED;
                }
                break;

            case SMS_CODE_ENTERED:

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    log.warn(e);
                }

                if (isCodeWrong()) { // TODO: check if works
                    state = SessionState.WRONG_CODE_ENTERED;
                } else {
                    state = SessionState.AUTHENTICATED;
                }
                break;

            case GOOD_PAGE_REQUESTED:
                if (isGoodPresent()) {
                    state = SessionState.ON_GOOD_PAGE;
                } else {
                    state = SessionState.ON_UNAVAILABLE_URL;
                }

        }
    }
}