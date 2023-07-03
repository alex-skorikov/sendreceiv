package com.skorikov.sendReceiv.integration;

import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.SendService;
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

    @LocalServerPort
    int port;

    @Test
    public void whenSendDocumentThenUpload() {
        String url = "http://localhost:" + port + "/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        ResponseEntity<String> response = sendService.sendDocument(url, payload);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is("Upload document."));
    }

    @Test
    public void whenSendDocumentWithWrongURLThenNotUpload() {
        String wrongUrl = "http://localhost:8080/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        ResponseEntity<String> response = sendService.sendDocument(wrongUrl, payload);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is("Can't upload document."));
    }
}
