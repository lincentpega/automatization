package com.lincentpega.javawildberriesselfbuy.controller;

import com.lincentpega.javawildberriesselfbuy.exceptions.TelegramFileUploadException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

@Service
public class TelegramApiClient {
    private final String URL;
    private final String botToken;

    private final RestTemplate restTemplate;

    public TelegramApiClient(@Value("${telegram.api-url}")String URL,
                             @Value("${telegram.bot-token}")String botToken) {
        this.URL = URL;
        this.botToken = botToken;
        this.restTemplate = new RestTemplate();
    }

    public void uploadCaptchaScreenshot(String chatId, ByteArrayResource value) throws TelegramFileUploadException {
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
