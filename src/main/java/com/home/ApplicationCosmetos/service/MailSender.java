package com.home.ApplicationCosmetos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;
    @Async
    public CompletableFuture<Void> sendMail(String emailTo, String titleEmail, String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();


        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(titleEmail);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
        return new CompletableFuture<Void>();
    }
}
