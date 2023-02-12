package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in;

import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class BotUtils {
    public static InlineKeyboardMarkup getStartKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Установить User-Agent")
                        .callbackData("set_user_agent")
                        .build()));
        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Установить разрешение")
                        .callbackData("set_resolution")
                        .build()));
        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Установить прокси")
                        .callbackData("set_proxy")
                        .build()));
        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Перейти к вводу номера")
                        .callbackData("enter_number")
                        .build()));
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static InlineKeyboardMarkup getSessionKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Указать товар")
                        .callbackData("enter_good")
                        .build()
        ));
        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Оформить заказ")
                        .callbackData("offer")
                        .build()
        ));

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static InlineKeyboardMarkup getBackKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("Назад")
                        .callbackData("back")
                        .build()
        ));
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static Long extractUserId(Update update) throws IllegalInputException {
        Long userId;
        if (update.hasMessage()) {
            userId = update.getMessage().getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            userId = update.getCallbackQuery().getFrom().getId();
        } else{
            throw new IllegalInputException("Illegal message type");
        }
        return userId;
    }

    public static Long extractChatId(Update update) throws IllegalInputException {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            throw new IllegalInputException();
        }

        return chatId;
    }

}
