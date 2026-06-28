package com.example.GmailPractice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/mail")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String sendMail(@RequestBody EmailRequest request) {
        emailService.sendEmail(request.to(), request.subject(), request.text());
        return "Email sent";
    }
}
