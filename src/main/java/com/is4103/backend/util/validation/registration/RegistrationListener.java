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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Value("${backend.base.url}")
    private String baseUrl;

    @Value("${backend.from.email}")
    private String fromEmail;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    @Override
    @Async
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = messageSource.getMessage("message.confirmEmailSubject", null, LocaleContextHolder.getLocale());
        String confirmationUrl = baseUrl + "/api/user/register/confirm?token=" + token;
        String message = messageSource.getMessage("message.regSuccPrompt", null, LocaleContextHolder.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n\r\n" + confirmationUrl);
        javaMailSender.send(email);
    }
}
