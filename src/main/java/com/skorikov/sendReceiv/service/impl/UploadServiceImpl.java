package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.UploadService;
import com.skorikov.sendReceiv.utils.KeyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final ObjectMapper objectMapper;
    private final KeyService keyService;
    private final String keyStorage = "src/main/resources/sendreceiv.pfx";

    @Value(value = "${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Value(value = "${server.ssl.key-store-type}")
    private String keyStoreType;

    @Value(value = "${server.ssl.key-alias}")
    private String alias;

    @Value(value = "${ssl.signature.algorithm}")
    private String signingAlgorithm;

    @Value(value = "${ssl.sigmature.hashing.algorithm}")
    private String hashingAlgorithm;

    @Override
    public ResponseEntity<String> uploadDocument(HttpServletRequest request, PayloadDto payloadDto) {
        try {
            PrivateKey privateKey = keyService.getPrivateKey(keyStorage, keyStorePassword.toCharArray(), keyStoreType, alias);
            String sign = request.getHeader("sign");
            byte[] bytes = sign.getBytes();
            String payload = objectMapper.writeValueAsString(payloadDto);
            byte[] messageBytes = payload.getBytes();
            byte[] encryptedMessageHash = keyService.decipher(bytes, hashingAlgorithm, privateKey);
            PublicKey publicKey = keyService.getPublicKey(keyStorage, keyStorePassword.toCharArray(), keyStoreType, alias);
            boolean isCorrect = keyService.verifyDecipher(messageBytes, hashingAlgorithm, publicKey, encryptedMessageHash);
            if (!isCorrect) {
                return ResponseEntity.badRequest().body("Can't upload document.");
            } else {
                return ResponseEntity.ok().body("Upload document.");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().body("Can't upload document.");
        }
    }
}
