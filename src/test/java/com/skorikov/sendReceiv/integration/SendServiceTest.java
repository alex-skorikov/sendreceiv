package com.skorikov.sendReceiv.integration;

import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.SendService;
import com.skorikov.sendReceiv.service.Signature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
public class SendServiceTest {
    @MockBean
    private RestTemplate restTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SendService sendService;

    @Autowired
    private Signature.SignDocument signDocumentService;

    @LocalServerPort
    int port;

    @Test
    public void whenSendDocumentThenBadRequest() throws Exception {
        ResponseEntity<String> responseEntity = ResponseEntity.badRequest().body("Can't send document.");
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(responseEntity);

        String url = "http://localhost:" + port + "/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        String stringKeyEncode = signDocumentService.getStringKeyEncode(payload);

        ResponseEntity<String> response = sendService.sendDocument(url, payload, stringKeyEncode);
        assertEquals("Can't send document.", response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenSendDocumentThenOkRequest() throws Exception {
        ResponseEntity<String> responseEntity = ResponseEntity.ok().body("Send document.");
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(responseEntity);

        String url = "http://localhost:" + port + "/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        String stringKeyEncode = signDocumentService.getStringKeyEncode(payload);

        ResponseEntity<String> response = sendService.sendDocument(url, payload, stringKeyEncode);
        assertEquals("Send document.", response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void whenSendDocumentThenThrowException() throws Exception {
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenThrow(RestClientException.class);

        String url = "http://localhost:" + port + "/upload";
        PayloadDto payload = new PayloadDto(1L, "Data");
        String stringKeyEncode = signDocumentService.getStringKeyEncode(payload);

        ResponseEntity<String> response = sendService.sendDocument(url, payload, stringKeyEncode);
        assertEquals("Can't upload document.", response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}