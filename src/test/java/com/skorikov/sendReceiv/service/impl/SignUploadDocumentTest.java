package com.skorikov.sendReceiv.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SignUploadDocumentTest {
    @Autowired
    private Signature.SignDocument service;

    @Test
    public void getStringKeyEncode() throws JsonProcessingException {
        AbstractPayload payload = new PayloadDto(1L, "data");
        String stringKeyEncode = service.getStringKeyEncode(payload);

        assertNotNull(stringKeyEncode);

        String newStringKeyEncode = service.getStringKeyEncode(payload);

        assertEquals(stringKeyEncode, newStringKeyEncode);
    }
}