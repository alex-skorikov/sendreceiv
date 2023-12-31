package com.skorikov.sendReceiv.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.SendService;
import com.skorikov.sendReceiv.service.Signature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ThroughTest {

    @Autowired
    private SendService sendService;

    @Autowired
    private Signature.SignDocument signDocumentService;

    @LocalServerPort
    int port;

    @Test
    public void whenSendDocumentThenUpload() throws JsonProcessingException {
        String url = "http://localhost:" + port + "/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        String stringKeyEncode = signDocumentService.getStringKeyEncode(payload);
        ResponseEntity<String> response = sendService.sendDocument(url, payload, stringKeyEncode);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("Upload document."));
    }

    @Test
    public void whenSendDocumentWithWrongURLThenNotUpload() throws JsonProcessingException {
        String wrongUrl = "http://localhost:8080/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        String stringKeyEncode = signDocumentService.getStringKeyEncode(payload);
        ResponseEntity<String> response = sendService.sendDocument(wrongUrl, payload, stringKeyEncode);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is("Can't upload document."));
    }

    @Test
    public void whenSendDocumentWithWrongKeyThenNotUpload() throws JsonProcessingException {
        String url = "http://localhost:" + port + "/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        PayloadDto wrongPayload = new PayloadDto(1L, "Wrong Data");
        String wrongKey = signDocumentService.getStringKeyEncode(wrongPayload);
        ResponseEntity<String> response = sendService.sendDocument(url, payload, wrongKey);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is("Can't upload document."));
    }

    @Test
    public void uploadWithOutFilterTest() throws JsonProcessingException {
        String url = "http://localhost:" + port + "/with-out-filter";
        PayloadDto wrongPayload = new PayloadDto(1L, "Wrong Data");
        String wrongKey = "wrong key";
        ResponseEntity<String> response = sendService.sendDocument(url, wrongPayload, wrongKey);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("Upload without filter."));
    }
}
