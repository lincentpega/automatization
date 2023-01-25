package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out;

import com.lincentpega.javawildberriesselfbuy.infrastructure.exception.TelegramFileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

@Service
public class TelegramApiClient {
    @Value("${telegram.bot-url}")
    private String URL;
    @Value("${telegram.bot-token}")
    private String botToken;
    private final RestTemplate restTemplate;

    public TelegramApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public void sendPhoto(String chatId, ByteArrayResource value) throws TelegramFileUploadException {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("document", value);
        map.add("chat_id", chatId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        try {
            restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/sendDocument", URL, botToken),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch (Exception e) {
            throw new TelegramFileUploadException();
        }
    }
}
