package com.example.helpfy.services.auth;

import com.example.helpfy.dtos.auth.LoginRequest;
import com.example.helpfy.dtos.auth.LoginResponse;
import com.example.helpfy.dtos.user.UserMapper;
import com.example.helpfy.exceptions.BadRequestException;
import com.example.helpfy.exceptions.Constants;
import com.example.helpfy.exceptions.NotFoundException;
import com.example.helpfy.models.User;
import com.example.helpfy.repositories.UserRepository;
import com.example.helpfy.services.encoder.Encoder;
import com.example.helpfy.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final Encoder encoder;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, Encoder encoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponse auth(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> {
            throw new NotFoundException(Constants.EMAIL_NOT_FOUND);
        });

        boolean validPassword = encoder.matches(loginRequest.getPassword(), user.getPassword());
        if (!validPassword) {
            throw new BadRequestException(Constants.INVALID_PASSWORD);
        }

        var userResponse = userMapper.fromUserToResponse(user);
        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponse(token, userResponse);
    }

    @Override
    public LoginResponse oauth2(String email) {
        User user = userRepository.findByEmail(email).get();

        var userResponse = userMapper.fromUserToResponse(user);
        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponse(token, userResponse);
    }
}
