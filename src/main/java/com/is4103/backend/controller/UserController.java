package com.is4103.backend.controller;

import java.util.List;

import javax.validation.Valid;

import com.is4103.backend.config.JwtTokenUtil;
import com.is4103.backend.dto.AuthToken;
import com.is4103.backend.dto.ChangePasswordRequest;
import com.is4103.backend.dto.LoginRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.model.User;
import com.is4103.backend.service.UserService;
import com.is4103.backend.util.validation.registration.OnRegistrationCompleteEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", ResponseCookie.from("token", token)
                .maxAge(3600)
                .httpOnly(true)
                .path("/")
                .build()
                .toString());
        return ResponseEntity.ok().headers(headers).body(new AuthToken(token));
    }

    @PostMapping(value = "/register/user/noverify")
    public User registerNewUserNoVerify(@RequestBody @Valid SignupRequest signupRequest) {
        return userService.registerNewUser(signupRequest, "USER");
    }

    @PostMapping(value = "/register/admin/noverify")
    public User registerNewAdminNoVerify(@RequestBody @Valid SignupRequest signupRequest) {
        return userService.registerNewUser(signupRequest, "ADMIN");
    }

    @PostMapping("/register/user")
    public User registerNewUser(@RequestBody @Valid SignupRequest signupRequest) {
        User user = userService.registerNewUser(signupRequest, "USER");

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return user;
    }

    @GetMapping("/register/confirm")
    public ModelAndView confirmRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equals("Valid Token")) {
            // redirect to login page
            return new ModelAndView("redirect:" + "http://localhost:3000/login?status=success");
        }
        return new ModelAndView("redirect:" + "http://localhost:3000/login?status=failed&token=" + token);
    }

    @GetMapping("/register/resend")
    public User resendRegistrationToken(@RequestParam("token") String token) {
        return userService.resendToken(token);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!userService.checkOldPasswordValid(user, changePasswordRequest.getOldPassword())) {
            return new ResponseEntity<String>("Invalid old password", HttpStatus.UNAUTHORIZED);
        }

        userService.changePassword(user, changePasswordRequest.getNewPassword());
        return ResponseEntity.ok("Success");
    }

    // used to protect routes
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/userping")
    public String userPing() {
        return "Pong User";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/adminping")
    public String adminPing() {
        return "Pong Admin";
    }
}
