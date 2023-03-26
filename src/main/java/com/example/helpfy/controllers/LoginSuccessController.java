package com.example.helpfy.controllers;

import com.example.helpfy.dtos.auth.LoginResponse;
import com.example.helpfy.services.auth.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/login-success")
@CrossOrigin
public class LoginSuccessController {
    private AuthService authService;
    public LoginSuccessController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping()
    public LoginResponse success(@AuthenticationPrincipal OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        return authService.oauth2(email);
    }

}

