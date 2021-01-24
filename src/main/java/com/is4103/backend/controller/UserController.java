package com.is4103.backend.controller;

import java.util.List;

import javax.validation.Valid;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.model.User;
import com.is4103.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(value="/create")
    public User createNewUser(@RequestBody @Valid SignupRequest signupRequest) {
        return userService.registerNewUser(signupRequest);
    }
    
}
