package com.skorikov.sendReceiv.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.configuration.wrapper.RequestWrapper;
import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.utils.KeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignRequestFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final KeyService keyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestWrapper wrapper = new RequestWrapper(request);

        String sign = wrapper.getHeader("sign");
        byte[] body = StreamUtils.copyToByteArray(wrapper.getInputStream());
        PayloadDto payloadDto = new ObjectMapper().readValue(body, PayloadDto.class);
        String payload = objectMapper.writeValueAsString(payloadDto);

        byte[] bytes = payload.getBytes();
        byte[] decodeKey = Base64.getDecoder().decode(sign);
        byte[] decipher = keyService.decipher(bytes);

        boolean verifyDecipher = keyService.verifyDecipher(bytes, decipher);
        boolean verifySign = keyService.verifySign(bytes, decodeKey);
        if (!verifySign || !verifyDecipher) {
            log.error("Can't verify upload document.");

            response.resetBuffer();
            response.setStatus(500);
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            response.getOutputStream().print(new ObjectMapper().writeValueAsString("Can't verify upload document."));
            response.flushBuffer();
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(wrapper, response);
    }
}
