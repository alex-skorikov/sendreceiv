package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.UploadService;
import com.skorikov.sendReceiv.utils.KeyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final KeyService keyService;
    private final String keyStorage = "src/main/resources/sendreceiv.pfx";
    private final ObjectMapper objectMapper;

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
            String sign = request.getHeader("sign");
            // дешифрование подписи
            byte[] decodeKey = Base64.getDecoder().decode(sign);

            PrivateKey privateKey = keyService.getPrivateKey(keyStorage, keyStorePassword.toCharArray(), keyStoreType, alias);
            PublicKey publicKey = keyService.getPublicKey(keyStorage, keyStorePassword.toCharArray(), keyStoreType, alias);
            String payload = objectMapper.writeValueAsString(payloadDto);
            byte[] bytes = payload.getBytes();
            byte[] decipher = keyService.decipher(bytes, hashingAlgorithm, privateKey);
            boolean verifyDecipher = keyService.verifyDecipher(bytes, hashingAlgorithm, publicKey, decipher);
            boolean verifySign = keyService.verifySign(bytes, signingAlgorithm, publicKey, decodeKey);
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
