package com.skorikov.sendReceiv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SendReceivApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendReceivApplication.class, args);
    }

}
