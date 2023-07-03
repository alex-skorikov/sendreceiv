package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.AbstractPayload;
import com.skorikov.sendReceiv.service.UploadService;
import com.skorikov.sendReceiv.utils.KeyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final KeyService keyService;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<String> uploadDocument(HttpServletRequest request, AbstractPayload payloadDto) {
        try {
            String sign = request.getHeader("sign");
            // дешифрование подписи
            byte[] decodeKey = Base64.getDecoder().decode(sign);

            String payload = objectMapper.writeValueAsString(payloadDto);
            byte[] bytes = payload.getBytes();
            byte[] decipher = keyService.decipher(bytes);
            boolean verifyDecipher = keyService.verifyDecipher(bytes, decipher);
            boolean verifySign = keyService.verifySign(bytes, decodeKey);
            if (!verifySign || !verifyDecipher) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Can't verify upload document.");
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("Upload document.");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Can't upload document.");
        }
    }
}
