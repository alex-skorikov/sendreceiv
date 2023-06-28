package com.skorikov.sendReceiv.service;

import com.skorikov.sendReceiv.dto.PayloadDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface UploadService {
    ResponseEntity<String> uploadDocument(HttpServletRequest request, PayloadDto payload);

}
