package com.example.helpfy.services.oauth2;

import com.example.helpfy.models.User;
import com.example.helpfy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User user = delegate.loadUser(userRequest);

        String email = user.getAttribute("email");
        if (!(email.endsWith("@ccc.ufcg.edu.br") || email.endsWith("@computacao.ufcg.edu.br"))) {
            throw new OAuth2AuthenticationException(new OAuth2Error("email_invalid"), "O email não faz parte do curso CC@UFCG");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            User userToSave = new User();
            userToSave.setEmail(email);
            userToSave.setName(user.getAttribute("given_name"));
            userToSave.setLastName(user.getAttribute("family_name"));
            userToSave.setAvatarLink(user.getAttribute("picture"));

            userRepository.save(userToSave);
        }

        return user;
    }
}
