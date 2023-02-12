package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler;

import com.lincentpega.javawildberriesselfbuy.application.port.in.CreateSessionUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.in.GetAuthorizedNumbersUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.application.service.CreateSessionService;
import com.lincentpega.javawildberriesselfbuy.application.service.GetAuthorizedNumbersService;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state.EnterNumberState;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state.SetProxyState;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state.SetResolutionState;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state.SetUserAgentState;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.BotStateCache;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DefaultCallBackHandler {
    private final ApplicationContext context;
    private final GetAuthorizedNumbersUseCase getAuthorizedNumbersUseCase;
    private final BotStateCache botStateCache;

    @Autowired
    public DefaultCallBackHandler(ApplicationContext context,
                                  UserRepository userRepository,
                                  BotStateCache botStateCache) {
        this.context = context;
        this.botStateCache = botStateCache;
        this.getAuthorizedNumbersUseCase = new GetAuthorizedNumbersService(userRepository);
    }

    public BotApiMethod<?> handleCallBack(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String callBackData = callbackQuery.getData();

        switch (callBackData) {
            case "set_user_agent":
                botStateCache.saveBotState(userId, context.getBean(SetUserAgentState.class));
                return EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .replyMarkup(callbackQuery.getMessage().getReplyMarkup())
                        .text("Введите значение User-Agent или default, чтобы установить стандартное значение")
                        .build();
            case "set_resolution":

                botStateCache.saveBotState(userId, context.getBean(SetResolutionState.class));
                return EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .replyMarkup(callbackQuery.getMessage().getReplyMarkup())
                        .text("Введите разрешение или default, чтобы установить стардарнтное значение")
                        .build();
            case "set_proxy":
                botStateCache.saveBotState(userId, context.getBean(SetProxyState.class));
                return EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .replyMarkup(callbackQuery.getMessage().getReplyMarkup())
                        .text("Введите адрес прокси-сервера или default, чтобы установить стандартное значение")
                        .build();
            case "enter_number":
                botStateCache.saveBotState(userId, context.getBean(EnterNumberState.class));
                return EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .replyMarkup(getNumbersKeyboard(getAuthorizedNumbersUseCase.getAuthorizedNumbers(userId)))
                        .text("Выберите авторизованный номер из списка или введите новый номер, начиная с цифры 9").
                        build();
        }
        return null;
    }

    private InlineKeyboardMarkup getNumbersKeyboard(List<String> numbers) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (String number: numbers) {
            rowsInline.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(number)
                            .callbackData(number)
                            .build()));
        }

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
