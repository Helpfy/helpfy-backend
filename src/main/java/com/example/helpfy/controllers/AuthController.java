package com.example.helpfy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController()
@RequestMapping("/auth")
public class AuthController {
    @GetMapping
    public ModelAndView auth() {
        return new ModelAndView("redirect:" + "/oauth2/authorization/google");
    }
}
