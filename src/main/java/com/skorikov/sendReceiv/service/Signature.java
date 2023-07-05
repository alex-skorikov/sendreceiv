package com.skorikov.sendReceiv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skorikov.sendReceiv.dto.AbstractPayload;

import java.io.IOException;

public interface Signature {

    interface SignDocument {
        String getStringKeyEncode (AbstractPayload payload) throws JsonProcessingException;
    }

    interface VerifyDocument {
        boolean verifyDocument(String sign, byte[] body) throws IOException;
    }
}
