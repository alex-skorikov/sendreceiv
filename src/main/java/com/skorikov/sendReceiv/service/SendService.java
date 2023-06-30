package com.skorikov.sendReceiv.service;

import com.skorikov.sendReceiv.dto.AbstractPayload;
import org.springframework.http.ResponseEntity;

public interface SendService {
    ResponseEntity<String> sendDocument(AbstractPayload document);
}
