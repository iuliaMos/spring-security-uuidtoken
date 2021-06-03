package com.example.controller;

import com.example.dto.UserModel;
import com.example.entity.User;
import com.example.service.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @NonNull
    private UserDetailsService userDetailsService;

    @PostMapping("/api/logout")
    public void logout(@RequestHeader("Authorization") final String token) {
        userDetailsService.logout(token);
    }

    @GetMapping("/api/user/{id}")
    @ResponseBody
    public User getDetails(@PathVariable final  Long id) {
        return userDetailsService.getUser(id);

    }
}
