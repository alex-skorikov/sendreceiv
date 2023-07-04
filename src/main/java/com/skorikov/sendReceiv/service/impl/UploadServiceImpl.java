package com.skorikov.sendReceiv.service.impl;

import com.skorikov.sendReceiv.dto.AbstractPayload;
import com.skorikov.sendReceiv.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    @Override
    public ResponseEntity<String> uploadDocument(HttpServletRequest request, AbstractPayload payloadDto) {

        // Логика обработки документа
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("Upload document.");
    }
}
