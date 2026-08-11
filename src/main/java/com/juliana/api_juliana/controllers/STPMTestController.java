package com.juliana.api_juliana.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.net.Socket;

@RestController
@RequestMapping("/api/test")
public class STPMTestController {
    @GetMapping("/test-smtp")
    public String testSmtp() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("smtp.gmail.com", 465), 10000);
            socket.close();

            return "CONEXIÓN SMTP EXITOSA";
        } catch (Exception e) {
            return "ERROR SMTP: " + e.getClass().getName() + " - " + e.getMessage();
        }
    }
}
