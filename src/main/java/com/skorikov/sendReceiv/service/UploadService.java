package com.skorikov.sendReceiv.service;

import com.skorikov.sendReceiv.dto.AbstractPayload;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UploadService {
    ResponseEntity<String> uploadDocument(HttpServletRequest request, AbstractPayload payload);

}
