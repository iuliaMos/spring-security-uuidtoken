package com.example.controller;

import com.example.dto.UserDetailsModel;
import com.example.service.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String BEARER = "Bearer";

    @NonNull
    private UserDetailsService userDetailsService;

    @PostMapping("/api/logout")
    public void logout(@RequestHeader("Authorization") final String auth) {
        String token = StringUtils.removeStart(auth, BEARER).trim();
        userDetailsService.logout(token);
    }

    @GetMapping("/api/user/{id}")
    @ResponseBody
    public UserDetailsModel getDetails(@PathVariable final Long id) {
        return userDetailsService.getUser(id);
    }
}
