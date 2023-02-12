package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out;

import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state.BotState;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.in.handler.state.DefaultState;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotStateCache {
    private final ApplicationContext context;
    private final Map<Long, BotState> userIdStateMap = new HashMap<>();

    public BotStateCache(ApplicationContext context) {
        this.context = context;
    }

    public void saveBotState(Long userId, BotState botState) {
        userIdStateMap.put(userId, botState);
    }

    public BotState getBotState(Long userId) {
        if (!userIdStateMap.containsKey(userId)) {
            userIdStateMap.put(userId, context.getBean(DefaultState.class));
        }
        return userIdStateMap.get(userId);
    }
}
