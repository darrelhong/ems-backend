package com.is4103.backend.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.User;
import com.is4103.backend.model.VerificationToken;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.repository.VerificationTokenRepository;
import com.is4103.backend.util.validation.errors.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${backend.base.url}")
    private String baseUrl;

    @Value("${backend.from.email}")
    private String fromEmail;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository vtRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User registerNewUser(SignupRequest signupRequest, String roleStr) throws UserAlreadyExistsException {
        if (emailExists(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
        }

        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());

        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByName(roleStr);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);

        return userRepository.save(newUser);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken vt = new VerificationToken(token, user);
        vtRepository.save(vt);
    }

    public String validateVerificationToken(String token) {
        VerificationToken vt = vtRepository.findByToken(token);

        if (vt == null) {
            return "Invalid Token";
        }

        User user = vt.getUser();
        Calendar cal = Calendar.getInstance();
        if ((vt.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            vtRepository.delete(vt);
            return "Token Expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid Token";
    }
    
    public User resendToken(String token) {
        VerificationToken vt = vtRepository.findByToken(token);
        vt.updateToken(UUID.randomUUID().toString());
        vt = vtRepository.save(vt);

        User user = vt.getUser();

        String recipientAddress = user.getEmail();
        String subject = messageSource.getMessage("message.confirmEmailSubject", null, LocaleContextHolder.getLocale());
        String confirmationUrl = "http://" + baseUrl + "/user/register/confirm?token=" + vt.getToken();
        String message = messageSource.getMessage("message.regSuccPrompt", null, LocaleContextHolder.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n\r\n" + confirmationUrl);
        javaMailSender.send(email);

        return user;
    }
}
