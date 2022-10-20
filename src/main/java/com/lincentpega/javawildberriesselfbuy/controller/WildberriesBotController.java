package com.lincentpega.javawildberriesselfbuy.controller;

import com.lincentpega.javawildberriesselfbuy.config.TelegramConfigProperties;
import com.lincentpega.javawildberriesselfbuy.service.ChromeSession;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

import java.util.HashMap;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Component
public class WildberriesBotController extends AbilityBot {
    private final long creatorId;
    private final HashMap<String, ChromeSession> idSessionHashMap;


    public WildberriesBotController(TelegramConfigProperties telegramConfig) {
        super(telegramConfig.getBotToken(), telegramConfig.getBotUsername());
        this.creatorId = telegramConfig.getCreatorId();
        this.idSessionHashMap = new HashMap<>();
    }

    public Ability createWildberriesSession() {
        return Ability
                .builder()
                .name("session")
                .info("creates wildberries session")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String userId = ctx.user().getId().toString();
                    if (!idSessionHashMap.containsKey(userId))
                        idSessionHashMap.put(userId, getChromeSession());
                    idSessionHashMap.get(userId).openWebsite();
                })
                .post(ctx -> silent.send("Session created successfully", ctx.chatId()))
                .build();
    }

    public Ability closeSession() {
        return Ability
                .builder()
                .name("close")
                .info("closes session")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String userId = ctx.user().getId().toString();
                    if (idSessionHashMap.containsKey(userId)) {
                        idSessionHashMap.get(userId).close();
                        idSessionHashMap.remove(userId);
                        silent.send("Session closed successfully", ctx.chatId());
                    } else {
                        silent.send("Session has already been closed or didn't exist", ctx.chatId());
                    }
                })
                .build();
    }

    public Ability showState() {
        return Ability
                .builder()
                .name("state")
                .info("shows state of a session")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String userId = ctx.user().getId().toString();
                    if (idSessionHashMap.containsKey(userId)) {
                        String creationDateTime = idSessionHashMap.get(userId).getCreationDateTime();
                        silent.send("Session exists and was created on " + creationDateTime, ctx.chatId());
                    }
                    else {
                        silent.send("Session doesn't exist", ctx.chatId());
                    }
                })
                .build();
    }

    @Override
    public long creatorId() {
        return creatorId;
    }

    @Lookup
    public ChromeSession getChromeSession() {
        return null;
    }
}
