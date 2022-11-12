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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
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
    private boolean isAuthenticated;


    @Autowired
    public ChromeSession(ChromeOptions chromeOptions, UserRepository userRepository) {
        this.creationDateTime = LocalDateTime.now();
        this.state = SessionState.SESSION_STARTED;
        this.driver = new ChromeDriver(chromeOptions);
        this.userRepository = userRepository;
        this.isAuthenticated = false;

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
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
        codeInputField.clear();
        codeInputField.sendKeys(code);

        isAuthenticated = true;
        state = SessionState.SMS_CODE_ENTERED;
    }

    public void addGoodToCart(String url) {
        driver.get(url);

        String addToCartButtonSelector = "div > div.product-page__aside-container.j-price-block > " +
                "div:nth-child(2) > div > button:nth-child(2)";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(addToCartButtonSelector)));

        addToCart();

        driver.get("https://www.wildberries.ru/lk/basket");

        state = SessionState.GOOD_IN_CART;
    }

    public void chooseAddress(String address) {
        toAddressChoice();

        String addressInputFieldSelector = "ymaps > ymaps.ymaps-2-1-79-searchbox__input-cell > " +
                "ymaps.ymaps-2-1-79-searchbox-input > input";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(addressInputFieldSelector)));

        inputAddress(address);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.warn(e);
        }

        String firstAddressButtonSelector = "#pooList > div.swiper-slide.swiper-slide-active > div";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(firstAddressButtonSelector)));
        WebElement firstAddressButton = driver.findElement(
                By.cssSelector(firstAddressButtonSelector));
        firstAddressButton.click();

        String chooseAddressOnMapButtonSelector = "ymaps > div > div.balloon-content-block > button";

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chooseAddressOnMapButtonSelector)));

        WebElement chooseAddressOnMapButton = driver.findElement(By.cssSelector(chooseAddressOnMapButtonSelector));
        chooseAddressOnMapButton.click();

        String finallyChooseButtonSelector = "body > div.popup.i-popup-choose-address.shown > " +
                "div > div > div.basket-delivery__methods > div.contents > " +
                "div.contents__item.contents__self.active > div > div.popup__btn > button.popup__btn-main";

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(finallyChooseButtonSelector)));

        WebElement finallyChooseButton = driver.findElement(By.cssSelector(finallyChooseButtonSelector));
        finallyChooseButton.click();

        state = SessionState.ADDRESS_SENT;
    }

    public String getCreationDateTime() {
        return creationDateTime.toString();
    }

    public SessionState getUpdatedState() {
        updateState();
        return state;
    }

    public void requestCodeAsSMS() { // FIXME: webhook response latency equals 60 seconds then new webhook update will be sent, so, while this func executes new message is incorrectly handled
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(70));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#requestCode")));

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
                            new Cookie(cookie.getName(),
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

    public void authenticateByCookies() {
        uploadCookies();
        openHomePage();
        isAuthenticated = true;
        state = SessionState.AUTHENTICATED;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    private void addToCart() {
        if (isGoodHaveSizes()) {
            WebElement sizeButton = driver.findElement(By.cssSelector("label.j-size"));
            sizeButton.click();
        }

        WebElement addToCartButton = driver.findElement(
                By.cssSelector("div > div.product-page__aside-container.j-price-block > div:nth-child(2) > div > button:nth-child(2)"));

        addToCartButton.click();
    }

    private void toAddressChoice() {

        if (isNoExistingAddress()) {
            String addressChooseLinkSelector = "div.basket-delivery__choose-address.j-btn-choose-address";
            WebElement addressChooseLink = driver.findElement(By.cssSelector(addressChooseLinkSelector));
            addressChooseLink.click();

            String addressChooseButtonSelector = "body > div.popup.i-popup-choose-address.shown > div > div > " +
                    "div.basket-delivery__methods > div.contents > div.contents__item.contents__self.active > " +
                    "div > div.popup__btn > button";

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(addressChooseButtonSelector)));

            WebElement addressChooseButton = driver.findElement(By.cssSelector(addressChooseButtonSelector));
            addressChooseButton.click();
        } else {
            String changeAddressButtonSelector = "#basketForm > div.basket-form__content.j-basket-form__content > " +
                    "div.basket-form__basket-section.basket-section.basket-delivery.j-b-basket-delivery > " +
                    "div.basket-section__header-wrap > button";
            WebElement changeAddressButton = driver.findElement(By.cssSelector(changeAddressButtonSelector));
            changeAddressButton.click();

            String changeAddressInnerButtonSelector = "body > div.popup.i-popup-choose-address.shown > div > div > " +
                    "div.basket-delivery__methods > div.contents > div.contents__item.contents__self.active > div > " +
                    "div.popup__btn > button.popup__btn-base";

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(changeAddressInnerButtonSelector)));

            WebElement changeAddressInnerButton = driver.findElement(By.cssSelector(changeAddressInnerButtonSelector));
            changeAddressInnerButton.click();
        }
    }

    private void inputAddress(String address) {
        String inputFieldSelector = "ymaps > ymaps.ymaps-2-1-79-searchbox__input-cell > " +
                "ymaps.ymaps-2-1-79-searchbox-input > input";

        WebElement inputField = driver.findElement(By.cssSelector(inputFieldSelector));
        inputField.sendKeys(address);

        WebElement findButton = driver.findElement(
                By.cssSelector("ymaps > ymaps.ymaps-2-1-79-searchbox__button-cell > ymaps"));
        findButton.click();

        if (isOptionsSuggested()) {
            WebElement firstSuggestedOption = driver.findElement(
                    By.cssSelector("ymaps > ymaps:nth-child(1) > ymaps > ymaps"));
            firstSuggestedOption.click();
        }

    }

    private boolean isOptionsSuggested() {
        try {
            driver.findElement(By.cssSelector("ymaps > ymaps:nth-child(1) > ymaps > ymaps"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isNoExistingAddress() { // searches for "Выбрать адрес доставки" clickable link
        try {
            driver.findElement(By.cssSelector("div.basket-delivery__choose-address.j-btn-choose-address"));
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

    private boolean isGoodHaveSizes() {
        try {
            driver.findElement(By.cssSelector("label.j-size.active"));
            return true;
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