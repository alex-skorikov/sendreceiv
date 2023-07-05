package com.skorikov.sendReceiv.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skorikov.sendReceiv.configuration.wrapper.RequestWrapper;
import com.skorikov.sendReceiv.service.Signature;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class SignRequestFilter extends OncePerRequestFilter {

    private final Signature.VerifyDocument verifyDocumentService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestWrapper wrapper = new RequestWrapper(request);

        String sign = wrapper.getHeader("sign");
        byte[] body = StreamUtils.copyToByteArray(wrapper.getInputStream());

        boolean verifyDocument = verifyDocumentService.verifyDocument(sign, body);
        if (!verifyDocument) {
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
