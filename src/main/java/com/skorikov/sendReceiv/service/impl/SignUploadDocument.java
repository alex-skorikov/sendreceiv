package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.AbstractPayload;
import com.skorikov.sendReceiv.service.Signature;
import com.skorikov.sendReceiv.utils.KeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SignUploadDocument implements Signature.SignDocument {

    private final ObjectMapper objectMapper;
    private final KeyService keyService;

    @Override
    public String getStringKeyEncode(AbstractPayload document) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(document);
        byte[] messageBytes = payload.getBytes();
        // подпись документа
        byte[] sign = keyService.sign(messageBytes);
        // шифрование подписи
        return Base64.getEncoder().encodeToString(sign);
    }
}
