package com.example.controller;

import com.example.dto.UserModel;
import com.example.service.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @NonNull
    private UserDetailsService userDetailsService;

    @PostMapping("/register")
    @ResponseBody
    public Long register(@RequestBody @Valid UserModel user) {
        log.info("register user {}", user);
        return userDetailsService.register(user);
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody @Valid UserModel user) {
        log.info("login user {}", user.getUsername());
        return userDetailsService.login(user);
    }
}
