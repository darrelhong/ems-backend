package com.is4103.backend.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import com.is4103.backend.dto.DisabledAccountRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdateUserRequest;
import com.is4103.backend.model.PasswordResetToken;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.User;
import com.is4103.backend.model.VerificationToken;
import com.is4103.backend.repository.PasswordResetTokenRepository;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.repository.VerificationTokenRepository;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private PasswordResetTokenRepository prtRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User registerNewUser(SignupRequest signupRequest, String roleStr, boolean enabled)
            throws UserAlreadyExistsException {
        if (emailExists(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
        }

        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setEnabled(true);
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByRoleEnum(RoleEnum.valueOf(roleStr.toUpperCase()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);

        if (enabled) {
            newUser.setEnabled(true);
        }

        return userRepository.save(newUser);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Transactional
    public User updateUser(User user, UpdateUserRequest updateUserRequest) {
        user.setName(updateUserRequest.getName());
        user.setDescription(updateUserRequest.getDescription());
        user.setAddress(updateUserRequest.getAddress());
        user.setPhonenumber(updateUserRequest.getPhonenumber());
     
        return userRepository.save(user);
    }

    public User updateAccountStatus(User user, DisabledAccountRequest updateUserRequest) {
     
        user.setEnabled(false);
    
        return userRepository.save(user);
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
            return "Token Expired";
        }

        vtRepository.delete(vt);
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
        String confirmationUrl = baseUrl + "/api/user/register/confirm?token=" + vt.getToken();
        String message = messageSource.getMessage("message.regSuccPrompt", null, LocaleContextHolder.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n\r\n" + confirmationUrl);
        javaMailSender.send(email);

        return user;
    }

    public boolean checkOldPasswordValid(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public void changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void resetPasswordRequest(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken(token, user);
        prtRepository.save(prt);

        String recipientAddress = user.getEmail();
        String subject = messageSource.getMessage("message.resetPasswordEmailSubject", null,
                LocaleContextHolder.getLocale());
        String confirmationUrl = "http://localhost:3000/register/reset-password/verify?token=" + prt.getToken();
        String message = messageSource.getMessage("message.resetPasswordPrompt", null, LocaleContextHolder.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n\r\n" + confirmationUrl);
        javaMailSender.send(email);
    }

    public User savePassword(String token, String newPassword) {
        User user = prtRepository.findByToken(token).getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return user;
    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken prt = prtRepository.findByToken(token);

        if (prt == null) {
            return "Invalid Token";
        }
        Calendar cal = Calendar.getInstance();
        if ((prt.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            prtRepository.delete(prt);
            return "Token Expired";
        }
        return "Valid Token";
    }

    public Long getCurrentUserId() {
        return getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
    }

    public User enableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User disableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(false);
        return userRepository.save(user);
    }
}
