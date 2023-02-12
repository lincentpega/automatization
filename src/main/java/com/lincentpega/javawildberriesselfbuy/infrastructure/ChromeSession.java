//package com.lincentpega.javawildberriesselfbuy.infrastructure;
//
//import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;
//import com.lincentpega.javawildberriesselfbuy.dto.CookieDto;
//import com.lincentpega.javawildberriesselfbuy.repository.UserRepository;
//import lombok.extern.log4j.Log4j2;
//import org.apache.commons.io.FileUtils;
//import org.openqa.selenium.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//import static com.lincentpega.javawildberriesselfbuy.application.constants.SelectorEnum.*;
//
//
//@Component
//@Scope("prototype")
//@Log4j2
//public class ChromeSession { // TODO: change to POJO
//    private int timeout = 20;
//    private SessionState state;
//    private String number;
//    private WebDriver driver;
//    private final LocalDateTime creationDateTime;
//    private final UserRepository userRepository;
//    private boolean isAuthenticated;
//
//
//    @Autowired
//    public ChromeSession(UserRepository userRepository) {
//        this.creationDateTime = LocalDateTime.now();
//        this.state = SessionState.SESSION_STARTED;
//        this.userRepository = userRepository;
//        this.isAuthenticated = false;
//    }
//
//    public void setDriver(WebDriver driver) {
//        this.driver = driver;
//    }
//
//    public void close() {
//        driver.close();
//        state = SessionState.SESSION_CLOSED;
//    }
//
//    public void openWebsite() {
//        String authLink = "https://www.wildberries.ru/security/login";
//        driver.get(authLink);
//        state = SessionState.SITE_ENTERED;
//    }
//
//    public void openHomePage() {
//        String link = "https://www.wildberries.ru";
//        driver.get(link);
//    }
//
//    public void enterNumber(String number) {
//        WebElement numberInputFormBlock = waitedFindVisible(timeout, INPUT_FORM_BLOCK.getMessage());
//        WebElement numberInputField = numberInputFormBlock.findElement(By.cssSelector(NUMBER_INPUT_FIELD.getMessage()));
//        numberInputField.sendKeys(number);
//
//        WebElement submitButton = numberInputFormBlock.findElement(By.cssSelector(SUBMIT_BUTTON.getMessage()));
//        submitButton.click();
//
//        state = SessionState.NUMBER_ENTERED;
//    }
//
//    public void takeScreenshot(String userId) throws IOException {
//        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//        FileUtils.copyFile(screenshot, new File("src/main/resources/screenshots/screenshot" + userId + ".png"));
//    }
//
//    public void enterCaptcha(String captchaCode) {
//        WebElement captchaInputField = waitedFindVisible(timeout, CAPTCHA_INPUT_FIELD.getMessage());
//        captchaInputField.sendKeys(captchaCode);
//
//        WebElement button = waitedFindClickable(timeout, CAPTCHA_SUBMIT_BUTTON.getMessage());
//        button.click();
//
//        state = SessionState.CAPTCHA_ENTERED;
//    }
//
//    public void enterCode(String code) {
//        WebElement codeInputFormBlock = waitedFindVisible(timeout, INPUT_FORM_BLOCK.getMessage());
//        WebElement codeInputField = codeInputFormBlock.findElement(
//                By.cssSelector(CODE_INPUT_FIELD.getMessage())
//        );
//        codeInputField.clear();
//        codeInputField.sendKeys(code);
//
//        isAuthenticated = true;
//        state = SessionState.SMS_CODE_ENTERED;
//    }
//
//    public void addGoodToCart(String url) {
//        driver.get(url);
//        addToCart();
//        driver.get("https://www.wildberries.ru/lk/basket");
//        state = SessionState.GOOD_IN_CART;
//    }
//
//    public void chooseAddress(String address) {
//        toAddressChoice();
//        inputAddress(address);
//
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        WebElement firstAddressButton = waitedFindClickable(timeout, FIRST_ADDRESS_BUTTON.getMessage());
//        firstAddressButton.click();
//
//
//        WebElement chooseAddressOnMapButton = waitedFindClickable(timeout, CHOOSE_ADDRESS_ON_MAP_BUTTON.getMessage());
//        chooseAddressOnMapButton.click();
//
//        WebElement finallyChooseButton = waitedFindClickable(timeout, FINALLY_CHOOSE_ADDRESS_BUTTON.getMessage());
//        finallyChooseButton.click();
//
//        state = SessionState.ADDRESS_CHOSEN;
//    }
//
//    public void choosePaymentMethodAndPay() {
//        driver.get("https://www.wildberries.ru/lk/basket");
//
//        WebElement changePaymentMethodButton = waitedFindClickable(timeout, CHOOSE_PAYMENT_METHOD_BUTTON.getMessage());
//        changePaymentMethodButton.click();
//
//        WebElement QRCodePaymentButton = waitedFindClickable(timeout, QR_PAYMENT_BUTTON.getMessage());
//        QRCodePaymentButton.click();
//
//        WebElement chooseOptionButton = waitedFindClickable(timeout, CHOOSE_OPTION_BUTTON.getMessage());
//        chooseOptionButton.click();
//
//        WebElement payButton = waitedFindClickable(timeout, PAY_BUTTON_SELECTOR.getMessage());
//        payButton.click();
//
//        waitedFindVisible(40, QR_CODE.getMessage());
//
//        state = SessionState.QR_CODE_APPEARED;
//    }
//
//    public String getAddress() {
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        WebElement issuePointAddress = waitedFindVisible(timeout, ISSUE_POINT_ADDRESS.getMessage());
//        return issuePointAddress.getText();
//    }
//
//    public String getCreationDateTime() {
//        return creationDateTime.toString();
//    }
//
//    public SessionState getUpdatedState() {
//        updateState();
//        return state;
//    }
//
//    public void requestCodeAsSMS() { // FIXME: webhook response latency equals 60 seconds then new webhook update will be sent, so, while this func executes new message is incorrectly handled
//        WebElement requestSMSButton = waitedFindClickable(70, REQUEST_SMS_BUTTON.getMessage());
//        requestSMSButton.click();
//
//        state = SessionState.REQUESTED_CODE_AS_SMS;
//    }
//
//    public void saveCookies() {
//        Cookie authCookie = driver.manage().getCookieNamed("WILDAUTHNEW_V3");
//        HashSet<Cookie> cookies = new HashSet<>();
//        cookies.add(authCookie);
//        User user = new User(number, cookies);
//        userRepository.save(user);
//    }
//
//    public boolean isCookiesExist() {
//        return userRepository.existsById(number);
//    }
//
//    public void uploadCookies() {
//        if (userRepository.existsById(number)) {
//            Optional<User> optionalUser = userRepository.findById(number);
//            if (optionalUser.isPresent()) {
//                User user = optionalUser.get();
//                var cookies = user.getCookies();
//                for (CookieDto cookie : cookies) {
//                    driver.manage().addCookie(
//                            new Cookie(cookie.getName(),
//                                    cookie.getValue(),
//                                    cookie.getDomain(),
//                                    cookie.getPath(),
//                                    cookie.getExpiry(),
//                                    cookie.isSecure(),
//                                    cookie.isHttpOnly(),
//                                    cookie.getSameSite())
//                    );
//                }
//            }
//        }
//    }
//
//    public void authenticateByCookies() {
//        uploadCookies();
//        openHomePage();
//        isAuthenticated = true;
//        state = SessionState.AUTHENTICATED;
//    }
//
//    public void setNumber(String number) {
//        this.number = number;
//    }
//
//    public boolean isAuthenticated() {
//        return isAuthenticated;
//    }
//
//    private void addToCart() {
//        if (isGoodHaveSizes()) {
//            WebElement sizeButton = waitedFindClickable(timeout, SIZE_BUTTON.getMessage());
//            sizeButton.click();
//        }
//
//        WebElement addToCartButton = waitedFindClickable(timeout, ADD_TO_CART_BUTTON.getMessage());
//        addToCartButton.click();
//    }
//
//    private void toAddressChoice() {
//
//        if (isNoExistingAddress()) {
//            WebElement addressChooseLink = waitedFindClickable(timeout, ADDRESS_CHOOSE_LINK.getMessage());
//            addressChooseLink.click();
//
//        } else {
//            WebElement changeAddressButton = waitedFindClickable(timeout, CHANGE_ADDRESS_BUTTON.getMessage());
//            changeAddressButton.click();
//
//            waitedFindClickable(timeout, ADDRESS_HISTORY_BUTTON.getMessage());
//
//            while (true) {
//                try {
//                    WebElement historyButton = waitedFindClickable(2, ADDRESS_HISTORY_BUTTON.getMessage());
//                    historyButton.click();
//
//                    WebElement deleteButton = waitedFindClickable(timeout, ADDRESS_DELETE_BUTTON.getMessage());
//                    deleteButton.click();
//
//                } catch (NoSuchElementException | TimeoutException e) {
//                    break;
//                }
//            }
//        }
//        WebElement addressChooseButton = waitedFindClickable(timeout, ADDRESS_CHOOSE_BUTTON.getMessage());
//        addressChooseButton.click();
//    }
//
//    private void inputAddress(String address) {
//        WebElement inputField = waitedFindVisible(timeout, ADDRESS_INPUT_FIELD.getMessage());
//        inputField.sendKeys(address);
//
//        WebElement findButton = waitedFindClickable(timeout, FIND_BUTTON.getMessage());
//        findButton.click();
//
//        if (isOptionsSuggested()) {
//            WebElement firstSuggestedOption = waitedFindClickable(timeout, FIRST_ADDRESS_OPTION.getMessage());
//            firstSuggestedOption.click();
//        }
//
//    }
//}