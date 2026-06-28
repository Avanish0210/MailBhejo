package com.example.GmailPractice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void shouldRejectInvalidRecipientAddress() {
        ReflectionTestUtils.setField(emailService, "fromAddress", "sender@example.com");

        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendEmail("not-an-email", "Subject", "Body"));
    }
}
