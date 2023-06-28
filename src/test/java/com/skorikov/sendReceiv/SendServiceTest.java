package com.skorikov.sendReceiv;

import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.SendService;
import com.skorikov.sendReceiv.utils.KeyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SendServiceTest {


    @Autowired
    private SendService service;

    @Autowired
    private KeyService keyService;

    @Test
    public void sendDocumentTest() throws Exception {
        Resource stateFile = new ClassPathResource("sendreceiv.pfx");
        File keyStorage = stateFile.getFile();
        PayloadDto payloadDto = new PayloadDto(1L, "Somedata");
        ResponseEntity<String> responseEntity = service.sendDocument(payloadDto);

        String body = responseEntity.getBody();
        HttpHeaders headers = responseEntity.getHeaders();

        System.out.println();
    }
}
