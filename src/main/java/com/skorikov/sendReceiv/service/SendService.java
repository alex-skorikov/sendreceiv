package com.skorikov.sendReceiv.service;

import com.skorikov.sendReceiv.dto.PayloadDto;
import org.springframework.http.ResponseEntity;

public interface SendService {
    ResponseEntity<String> sendDocument(PayloadDto document);
}
