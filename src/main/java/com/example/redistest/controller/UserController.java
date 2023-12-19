package com.example.redistest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyAuthority('ROLE_USER')")
public class UserController {

    @GetMapping("/v1/user")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello Secure User");
    }

    @GetMapping("/v2/user")
    public String getUserNotSecure() {
        return "Hello Not Secure User";
    }
}
