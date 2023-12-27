package com.example.redistest.controller;

import com.example.redistest.model.User;
import com.example.redistest.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/v1/user")
    public String getUser(@AuthenticationPrincipal User currentUser) {
        userService.addActivity(currentUser);
        return "Hello Secure User";
    }

}
