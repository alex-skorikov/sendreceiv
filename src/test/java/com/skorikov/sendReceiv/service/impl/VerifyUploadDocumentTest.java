package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.AbstractPayload;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.Signature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class VerifyUploadDocumentTest {
    @Autowired
    private Signature.VerifyDocument verifyService;
    @Autowired
    private Signature.SignDocument signService;

    @Test
    public void verifyDocument() throws IOException {

        AbstractPayload payloadDto = new PayloadDto(1L, "data");
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(payloadDto);
        String sign = signService.getStringKeyEncode(payloadDto);

        byte[] bytes = payload.getBytes();

        boolean verify = verifyService.verifyDocument(sign, bytes);
        assertTrue(verify);
    }

    @Test
    public void verifyDocumentWithWrongSign() throws IOException {

        AbstractPayload payloadDto = new PayloadDto(1L, "data");
        AbstractPayload wrongPayloadDto = new PayloadDto(1L, "wrong data");
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(payloadDto);
        String wrongSign = signService.getStringKeyEncode(wrongPayloadDto);

        byte[] bytes = payload.getBytes();

        boolean verify = verifyService.verifyDocument(wrongSign, bytes);
        assertFalse(verify);
    }
}