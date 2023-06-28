package com.skorikov.sendReceiv;

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

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class KeyServiceTest {
    @Autowired
    private KeyService keyService;

    String keyStore = "src/test/resources/sendreceiv.pfx";
    String storeType = "PKCS12";
    String alias = "sendreceiv";
    char[] password = "rootroot".toCharArray();
    String signingAlgorithm = "SHA256withRSA";
    String hashingAlgorithm = "SHA-256";

    @Test
    public void givenMessageData_whenSignWithSignatureSigning_thenVerify() throws Exception {
        PrivateKey privateKey = keyService.getPrivateKey(keyStore, password, storeType, alias);

        ObjectMapper objectMapper = new ObjectMapper();
        PayloadDto payloadDto = new PayloadDto(1L, "Somedata");
        String payload = objectMapper.writeValueAsString(objectMapper.writeValueAsString(payloadDto));
        byte[] messageBytes = payload.getBytes();

        byte[] digitalSignature = keyService.sign(messageBytes, signingAlgorithm, privateKey);

        PublicKey publicKey = keyService.getPublicKey(keyStore, password, storeType, alias);
        boolean isCorrect = keyService.verifySign(messageBytes, signingAlgorithm, publicKey, digitalSignature);

        assertTrue(isCorrect);
    }

    @Test
    public void givenMessageData_whenSignWithMessageDigestAndCipher_thenVerify() throws Exception {
        PrivateKey privateKey = keyService.getPrivateKey(keyStore, password, storeType, alias);

        ObjectMapper objectMapper = new ObjectMapper();
        PayloadDto payloadDto = new PayloadDto(2L, "Somedata2");
        String payload = objectMapper.writeValueAsString(objectMapper.writeValueAsString(payloadDto));
        byte[] messageBytes = payload.getBytes();

        byte[] encryptedMessageHash = keyService.decipher(messageBytes, hashingAlgorithm, privateKey);

        PublicKey publicKey = keyService.getPublicKey(keyStore, password, storeType, alias);
        boolean isCorrect = keyService.verifyDecipher(messageBytes, hashingAlgorithm, publicKey, encryptedMessageHash);

        assertTrue(isCorrect);
    }

}
