package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.lazar.fabrica_de_coduri.utils.MessageConstants.EMAIL_NOT_FOUND_MESSAGE;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String FACEBOOK_KEY = "FACEBOOK";

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String email = null;
        String name = null;

        switch (registrationId) {
            case FACEBOOK_KEY -> {
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
            }
            case "GITHUB" -> {
                email = (String) attributes.get("email");
                if (email == null) {
                    String token = request.getAccessToken().getTokenValue();
                    var restTemplate = new org.springframework.web.client.RestTemplate();

                    var headers = new org.springframework.http.HttpHeaders();
                    headers.setBearerAuth(token);
                    var entity = new org.springframework.http.HttpEntity<>(headers);

                    var response = restTemplate.exchange(
                            "https://api.github.com/user/emails",
                            org.springframework.http.HttpMethod.GET,
                            entity,
                            List.class
                    );

                    List<Map<String, Object>> emails = response.getBody();
                    if (emails != null) {
                        for (Map<String, Object> mail : emails) {
                            if (Boolean.TRUE.equals(mail.get("primary")) && Boolean.TRUE.equals(mail.get("verified"))) {
                                email = (String) mail.get("email");
                                break;
                            }
                        }
                    }

                    if (email == null) {
                        email = attributes.get("login") + "@github.com";
                    }
                }

                name = (String) attributes.get("name");
                if (name == null) {
                    name = (String) attributes.get("login");
                }
            }

        }

        if (email == null)
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user"), EMAIL_NOT_FOUND_MESSAGE + registrationId);

        attributes.put("email", email);

        String finalEmail = email;
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(finalEmail);
                    newUser.setEnabled(true);
                    newUser.setRole("ROLE_USER");
                    return newUser;
                });

        user.setUsername(name);
        user.setProvider(registrationId);
        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                "email"
        );
    }
}