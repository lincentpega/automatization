package com.lincentpega.javawildberriesselfbuy.controller;

import com.lincentpega.javawildberriesselfbuy.config.ChromeOptionsConfig;
import com.lincentpega.javawildberriesselfbuy.config.TelegramConfigProperties;
import com.lincentpega.javawildberriesselfbuy.service.ChromeSession;
import org.springframework.stereotype.Controller;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

import java.util.HashMap;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Controller
public class WildberriesBotController extends AbilityBot {
    private final long creatorId;

    private final HashMap<String, ChromeSession> sessions;

    public WildberriesBotController(TelegramConfigProperties telegramConfig) {
        super(telegramConfig.getBotToken(), telegramConfig.getBotUsername());
        this.creatorId = telegramConfig.getCreatorId();
        this.sessions = new HashMap<>();
    }

    @Override
    public long creatorId() {
        return creatorId;
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
                    if (!sessions.containsKey(userId))
                        sessions.put(userId, new ChromeSession(userId, ChromeOptionsConfig.createChromeOptions()));
                    sessions.get(userId).openWebsite();
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
                    sessions.get(userId).close();
                    sessions.remove(userId);
                })
                .post(ctx -> silent.send("Session closed successfully", ctx.chatId()))
                .build();
    }

//    public Ability showState() {
//        return Ability
//                .builder()
//                .name("state")
//                .info("prints state of session")
//                .locality(USER)
//                .privacy(PUBLIC)
//                .
//    }
}
