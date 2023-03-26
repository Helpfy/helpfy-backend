package com.example.helpfy.controllers;

import com.example.helpfy.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("/login-fail")
@CrossOrigin
public class LoginFailController {
    @GetMapping()
    public ResponseEntity<?> fail(HttpServletRequest request) {
        OAuth2AuthenticationException ex = (OAuth2AuthenticationException) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        throw new BadRequestException(ex.getMessage());
    }
}

