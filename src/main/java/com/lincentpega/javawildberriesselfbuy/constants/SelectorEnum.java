package com.lincentpega.javawildberriesselfbuy.constants;

public enum SelectorEnum {
    INPUT_FORM_BLOCK("form#spaAuthForm"),
    NUMBER_INPUT_FIELD("input.input-item"),
    SUBMIT_BUTTON("button#requestCode"),
    CAPTCHA_INPUT_FIELD("#smsCaptchaCode"),
    CAPTCHA_SUBMIT_BUTTON("#spaAuthForm > div > div.login__captcha.form-block.form-block--captcha > button"),
    CODE_INPUT_FIELD("input.j-input-confirm-code.val-msg"),
    ADDRESS_INPUT_FIELD("ymaps > ymaps.ymaps-2-1-79-searchbox__input-cell > " +
            "ymaps.ymaps-2-1-79-searchbox-input > input"),
    FIRST_ADDRESS_BUTTON("#pooList > div.swiper-slide.swiper-slide-active > div"),
    FIND_BUTTON("ymaps > ymaps.ymaps-2-1-79-searchbox__button-cell > ymaps"),
    FIRST_ADDRESS_OPTION("ymaps > ymaps:nth-child(1) > ymaps > ymaps"),
    ADDRESS_CHOOSE_LINK("div.basket-delivery__choose-address.j-btn-choose-address"),
    ADDRESS_CHOOSE_BUTTON("body > div.popup.i-popup-choose-address.shown > div > div > " +
            "div.basket-delivery__methods > div.contents > div.contents__item.contents__self.active > " +
            "div > div.popup__btn > button"),
    CHANGE_ADDRESS_BUTTON("#basketForm > div.basket-form__content.j-basket-form__content > " +
            "div.basket-form__basket-section.basket-section.basket-delivery.j-b-basket-delivery > " +
            "div.basket-section__header-wrap > button"),
    CHANGE_ADDRESS_INNER_BUTTON("body > div.popup.i-popup-choose-address.shown > div > div > " +
            "div.basket-delivery__methods > div.contents > div.contents__item.contents__self.active > div > " +
            "div.popup__btn > button.popup__btn-base"),
    CHOOSE_ADDRESS_ON_MAP_BUTTON("ymaps > div > div.balloon-content-block > button"),
    FINALLY_CHOOSE_ADDRESS_BUTTON("body > div.popup.i-popup-choose-address.shown > " +
            "div > div > div.basket-delivery__methods > div.contents > " +
            "div.contents__item.contents__self.active > div > div.popup__btn > button.popup__btn-main"),
    CHOOSE_PAYMENT_METHOD_BUTTON("#basketForm > " +
            "div.basket-form__content.j-basket-form__content > " +
            "div.basket-section__wrap >" +
            " div.basket-form__basket-section.basket-section.basket-pay.j-b-basket-payment >" +
            " div.basket-section__header-wrap > button"),
    QR_PAYMENT_BUTTON("body > div.popup.popup-choose-pay.shown > div > div > div.methods-pay" +
            " > ul > li.methods-pay__item.active > label"),
    CHOOSE_OPTION_BUTTON("body > div.popup.popup-choose-pay.shown > div > div" +
            " > div.popup__btn > button.popup__btn-main"),
    PAY_BUTTON_SELECTOR("#basketForm > div.basket-form__sidebar.sidebar > div > div > div " +
            "> div.basket-order__b-btn.b-btn > button"),
    QR_CODE("body > div.popup.i-popup-pay-qr.shown > div > " +
            "div.qr-code__content > div.qr-code__value"),
    REQUEST_SMS_BUTTON("#requestCode"),
    ACTIVE_SIZE_BUTTON("label.j-size:not(disabled)"),
    SIZE_BUTTON("label.j-size"),
    ADD_TO_CART_BUTTON("div > div.product-page__aside-container.j-price-block > "
            + "div:nth-child(2) > div > button:nth-child(2)"),
    CODE_MESSAGE_BLOCK("#spaAuthForm > div > div.login__code-head > p"),
    NOTIFICATION_MESSAGE_BLOCK("#spaAuthForm > div > div.login__code-head > p"),
    CAPTCHA_BLOCK("#smsCaptchaCode"),
    CODE_WRONG_INDICATOR("#spaAuthForm > div > div.login__code.form-block > p:nth-child(7)"),
    CODE_SENT_INDICATOR("#spaAuthForm > div > div.login__code.form-block > div > input"),
    GOOD_PRESENT_INDICATOR("div.product-page__header-wrap"),
    ISSUE_POINT_ADDRESS(".selected-address__wrap > div:nth-child(2) > span:nth-child(1)"),
    ADDRESS_HISTORY_BUTTON("button.history__menu"),
    ADDRESS_DELETE_BUTTON("button.address-delete");


    private final String message;

    SelectorEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
