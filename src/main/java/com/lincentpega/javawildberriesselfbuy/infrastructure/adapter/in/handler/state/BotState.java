package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state;

import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.IllegalInputException;
import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.TelegramFileUploadException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface BotState {
    BotApiMethod<?> handle(Update update) throws IllegalInputException, IOException, TelegramFileUploadException;
}
