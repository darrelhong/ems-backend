package com.is4103.backend.util.validation.registration;

import java.util.UUID;

import com.is4103.backend.model.User;
import com.is4103.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Value("${backend.base.url}")
    private String baseUrl;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Confirm your email address (Event Management System)";
        String confirmationUrl = "http://" + baseUrl + "/user/confirm-registration?token=" + token;
        String message = messageSource.getMessage("message.regSuccPrompt", null, LocaleContextHolder.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("darrel.hong@gmail.com");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n\r\n" + confirmationUrl);
        javaMailSender.send(email);
    }
}
