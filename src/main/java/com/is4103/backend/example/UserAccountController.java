package com.is4103.backend.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
public class UserAccountController {
    @Autowired
    private UserAccountRepository userRepository;

    @PostMapping(path = "/add")
    public UserAccount addNewUser(@RequestParam String name, @RequestParam String email) {
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setName(name);
        userRepository.save(user);
        return user;
    }

    @GetMapping(path = "/all")
    public Iterable<UserAccount> getAllUsers() {
        return userRepository.findAll();
    }
}
