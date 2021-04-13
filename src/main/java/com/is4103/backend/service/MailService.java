package com.is4103.backend.service;

import com.is4103.backend.dto.EmailRequest;
import com.is4103.backend.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;
    
    @Value("${backend.from.email}")
    private String fromEmail;

    public ResponseEntity<String> sendEmailNotif(EmailRequest request) {
        try {
            User sender = userService.getUserById(request.getSenderId());
            User recipient = userService.getUserById(request.getRecipientId());
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(fromEmail);
            email.setTo(recipient.getEmail());
            email.setSubject(request.getSubject());
            email.setText(request.getTextBody());
            email.setCc(sender.getEmail()); // cc the person who submitted this.
            javaMailSender.send(email);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.ok(e.toString());

        }
    }
}
