package com.skorikov.sendReceiv.controller;

import com.skorikov.sendReceiv.dto.PayloadDto;
import com.skorikov.sendReceiv.service.SendService;
import com.skorikov.sendReceiv.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final UploadService uploadService;
    private final SendService sendService;

    @PostMapping("/upload")
    public ResponseEntity<String> getPayload(HttpServletRequest request, @RequestBody PayloadDto payload) {
        return uploadService.uploadDocument(request, payload);
    }

    @GetMapping("/sign")
    public ResponseEntity<String> signPayload() {
        var v = new PayloadDto(1L, "Somedata");
        return sendService.sendDocument(v);
    }

}
