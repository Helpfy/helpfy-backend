package com.example.helpfy.services.auth;

import com.example.helpfy.dtos.auth.LoginRequest;
import com.example.helpfy.dtos.auth.LoginResponse;

public interface AuthService {
    LoginResponse auth(LoginRequest loginRequest);
    LoginResponse oauth2(String email);
}
