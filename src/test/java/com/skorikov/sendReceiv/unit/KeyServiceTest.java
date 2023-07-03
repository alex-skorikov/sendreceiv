package com.skorikov.sendReceiv.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.utils.KeyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class KeyServiceTest {
    @Autowired
    private KeyService keyService;

    @Test
    public void givenMessageData_whenSignWithSignatureSigning_thenVerify() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        PayloadDto payloadDto = new PayloadDto(1L, "Somedata");
        String payload = objectMapper.writeValueAsString(objectMapper.writeValueAsString(payloadDto));
        byte[] messageBytes = payload.getBytes();

        byte[] digitalSignature = keyService.sign(messageBytes);

        boolean isCorrect = keyService.verifySign(messageBytes, digitalSignature);

        assertTrue(isCorrect);
    }

    @Test
    public void givenMessageData_whenSignWithMessageDigestAndCipher_thenVerify() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        PayloadDto payloadDto = new PayloadDto(2L, "Somedata2");
        String payload = objectMapper.writeValueAsString(objectMapper.writeValueAsString(payloadDto));
        byte[] messageBytes = payload.getBytes();

        byte[] encryptedMessageHash = keyService.decipher(messageBytes);

        boolean isCorrect = keyService.verifyDecipher(messageBytes, encryptedMessageHash);

        assertTrue(isCorrect);
    }

}
