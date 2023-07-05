package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.AbstractPayload;
import com.skorikov.sendReceiv.service.SendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendServiceImpl implements SendService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<String> sendDocument(String url, AbstractPayload document, String stringKeyEncode) {
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            String payload = objectMapper.writeValueAsString(document);

            HttpHeaders headers = new HttpHeaders();
            headers.set("sign", stringKeyEncode);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            HttpStatusCode statusCode = exchange.getStatusCode();
            String body = exchange.getBody();
            return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_JSON).body(body);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Can't upload document.");
        }
    }
}
