package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.Signature;
import com.skorikov.sendReceiv.utils.KeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class VerifyUploadDocument implements Signature.VerifyDocument {
    private final ObjectMapper objectMapper;
    private final KeyService keyService;

    @Override
    public boolean verifyDocument(String sign, byte[] body) throws IOException {
        PayloadDto payloadDto = objectMapper.readValue(body, PayloadDto.class);
        String payload = objectMapper.writeValueAsString(payloadDto);

        byte[] bytes = payload.getBytes();
        byte[] decodeKey = Base64.getDecoder().decode(sign);
        byte[] decipher = keyService.decipher(bytes);
        boolean verifyDecipher = keyService.verifyDecipher(bytes, decipher);
        boolean verifySign = keyService.verifySign(bytes, decodeKey);

        return verifyDecipher && verifySign;
    }
}
