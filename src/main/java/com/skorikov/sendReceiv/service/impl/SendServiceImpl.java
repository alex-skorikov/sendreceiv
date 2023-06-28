package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.SendService;
import com.skorikov.sendReceiv.utils.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendServiceImpl implements SendService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KeyService keyService;

    @Value(value = "${send.document.url}")
    private String sendDocumentUrl;

    //    @Value(value = "${server.ssl.key-store}")
    private final String keyStorage = "src/main/resources/sendreceiv.pfx";

    @Value(value = "${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Value(value = "${server.ssl.key-store-type}")
    private String keyStoreType;

    @Value(value = "${server.ssl.key-alias}")
    private String alias;

    @Value(value = "${ssl.signature.algorithm}")
    private String signingAlgorithm;

    static {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> {
                    if (hostname.equals("localhost")) {
                        return true;
                    }
                    return false;
                });
    }

    @Override
    public ResponseEntity<String> sendDocument(PayloadDto document) {
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            String payload = objectMapper.writeValueAsString(document);
            PrivateKey privateKey = keyService.getPrivateKey(keyStorage, keyStorePassword.toCharArray(), keyStoreType, alias);
            byte[] messageBytes = payload.getBytes();
            byte[] digitalSignature = keyService.sign(messageBytes, signingAlgorithm, privateKey);
            HttpHeaders headers = new HttpHeaders();
            assert digitalSignature != null;
            headers.set("sign", Arrays.toString(digitalSignature));
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> request =
                    new HttpEntity<>(payload, headers);

            restTemplate.exchange(sendDocumentUrl, HttpMethod.POST, request, String.class);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("Send document.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Can't send document.");
        }
    }

    public String getSendDocumentUrl() {
        return sendDocumentUrl;
    }

    public String getKeyStorage() {
        return keyStorage;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getAlias() {
        return alias;
    }

    public String getSigningAlgorithm() {
        return signingAlgorithm;
    }


}
