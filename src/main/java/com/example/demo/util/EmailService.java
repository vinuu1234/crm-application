package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(
            String email,
            String tempPassword) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("CRM Account Created");

        message.setText(
                "Welcome to CRM\n\n" +
                "Email: " + email + "\n" +
                "Temporary Password: " + tempPassword + "\n\n" +
                "Please change your password after first login."
        );

        mailSender.send(message);
    }
}