package com.example.GmailPractice;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Value("${sendgrid.api-key:}")
    private String apiKey;

    @Value("${sendgrid.from-email:${spring.mail.username:}}")
    private String fromAddress;

    public void sendEmail(String to, String subject, String text) {
        validateEmail(to);
        validateRequired(subject, "subject");
        validateRequired(text, "text");

        if (apiKey.isBlank()) {
            throw new IllegalStateException("SendGrid API key is not configured");
        }

        try {
            Mail mail = new Mail(
                    new Email(fromAddress),
                    subject.trim(),
                    new Email(to),
                    new Content("text/plain", text.trim())
            );

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            SendGrid sg = new SendGrid(apiKey);
            Response response = sg.api(request);

            log.info("SendGrid response status={} body={} headers={}", response.getStatusCode(), response.getBody(), response.getHeaders());

            if (response.getStatusCode() >= 400) {
                throw new IllegalStateException("SendGrid request failed with status " + response.getStatusCode() + ": " + response.getBody());
            }

            log.info("Email accepted by SendGrid for {} with status {}", to, response.getStatusCode());
        } catch (IOException ex) {
            log.error("Failed to send email to {}", to, ex);
            throw new IllegalStateException("Could not send email via SendGrid", ex);
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Recipient email is invalid");
        }
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}
