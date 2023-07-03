package com.skorikov.sendReceiv.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.utils.KeyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
public class UploadServiceTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KeyService keyService;

    @Test
    public void whenUploadDocumentThenUpload() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(new PayloadDto(1L, "Data"));
        byte[] messageBytes = payload.getBytes();
        byte[] sign = keyService.sign(messageBytes);
        String stringKeyEncode = Base64.getEncoder().encodeToString(sign);
        HttpHeaders headers = new HttpHeaders();
        headers.set("sign", stringKeyEncode);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(post("/upload")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("Upload document.")));
    }

    @Test
    public void whenUploadDocumentThenNotVerifySign() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String payload = objectMapper.writeValueAsString(new PayloadDto(1L, "Data"));
        byte[] messageBytes = payload.getBytes();
        byte[] sign = keyService.sign(messageBytes);
        String stringKeyEncode = Base64.getEncoder().encodeToString(sign);

        String wrongPayload = objectMapper.writeValueAsString(new PayloadDto(2L, "Data2"));
        byte[] wrongMessageBytes = wrongPayload.getBytes();
        byte[] wrongSign = keyService.sign(wrongMessageBytes);
        String wrongStringKeyEncode = Base64.getEncoder().encodeToString(wrongSign);

        HttpHeaders headers = new HttpHeaders();
        headers.set("sign", wrongStringKeyEncode);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(post("/upload")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Can't verify upload document.")));
    }

    @Test
    public void whenUploadDocumentThenNotVerifyContent() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String payload = objectMapper.writeValueAsString(new PayloadDto(1L, "Data"));
        byte[] messageBytes = payload.getBytes();
        byte[] sign = keyService.sign(messageBytes);
        String stringKeyEncode = Base64.getEncoder().encodeToString(sign);

        String wrongPayload = objectMapper.writeValueAsString(new PayloadDto(2L, "Data2"));
        byte[] wrongMessageBytes = wrongPayload.getBytes();
        byte[] wrongSign = keyService.sign(wrongMessageBytes);
        String wrongStringKeyEncode = Base64.getEncoder().encodeToString(wrongSign);

        HttpHeaders headers = new HttpHeaders();
        headers.set("sign", stringKeyEncode);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(post("/upload")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(wrongPayload))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Can't verify upload document.")));
    }
}
