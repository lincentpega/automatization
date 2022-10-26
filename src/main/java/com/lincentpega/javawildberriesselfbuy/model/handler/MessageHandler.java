package com.lincentpega.javawildberriesselfbuy.model.handler;

import com.lincentpega.javawildberriesselfbuy.constants.BotMessageEnum;
import com.lincentpega.javawildberriesselfbuy.service.ChromeSession;
import org.openqa.selenium.WebDriverException;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

@Component
public class MessageHandler {
    private final HashMap<String, ChromeSession> idSessionHashMap;

    public MessageHandler() {
        idSessionHashMap = new HashMap<>();
    }

    public BotApiMethod<?> handleUpdate(Update update) throws IllegalArgumentException, WebDriverException {
        Message message = update.getMessage();

        if (message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    return processStart(message);
                case "/state":
                    return processState(message);
                case "/close":
                    return processClose(message);
                default:
                    throw new IllegalArgumentException("Неизвестная команда/сообщение");
            }
        } else {
            throw new IllegalArgumentException("Сообщение пусто");
        }
    }

    public BotApiMethod<?> processStart(Message message) {
        String userId = extractUserId(message);

        if (!idSessionHashMap.containsKey(userId)) {
            ChromeSession chromeSession = getChromeSession();
            chromeSession.openWebsite();

            idSessionHashMap.put(userId, chromeSession);

            return new SendMessage(message.getChatId().toString(), BotMessageEnum.SESSION_CREATED.getMessage());
        } else {
            return new SendMessage(message.getChatId().toString(), BotMessageEnum.SESSION_ALREADY_EXISTS.getMessage());
        }
    }

    public BotApiMethod<?> processState(Message message) {
        String userId = extractUserId(message);

        if (idSessionHashMap.containsKey(userId)) {
            String creationDateTime = idSessionHashMap.get(userId).getCreationDateTime();

            return new SendMessage(message.getChatId().toString(), "Сессия была создана " + creationDateTime);
        } else {
            throw new IllegalArgumentException(BotMessageEnum.SESSION_DOESNT_EXIST.getMessage());
        }
    }

    public BotApiMethod<?> processClose(Message message) {
        String userId = extractUserId(message);

        if (idSessionHashMap.containsKey(userId)) {
            idSessionHashMap.get(userId).close();
            idSessionHashMap.remove(userId);
            return new SendMessage(message.getChatId().toString(), "Сессия успешно завершена");
        } else {
            throw new IllegalArgumentException(BotMessageEnum.SESSION_DOESNT_EXIST.getMessage());
        }
    }

    private String extractUserId(Message message) {
        return message.getFrom().getId().toString();
    }

    @Lookup
    public ChromeSession getChromeSession() {
        return null;
    }

}
