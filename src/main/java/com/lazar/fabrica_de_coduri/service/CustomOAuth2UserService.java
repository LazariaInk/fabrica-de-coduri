package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final String ADMIN_EMAIL = "fabricadecoduri@gmail.com";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauthUser = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());

        String email = extractEmail(provider, attributes);
        String imageUrl = extractImageUrl(provider, attributes);
        String role = ADMIN_EMAIL.equalsIgnoreCase(email) ? "ROLE_ADMIN" : "ROLE_USER";

        User user = userRepository.findByUsername(email).orElseGet(User::new);
        user.setUsername(email);
        user.setRole(role);
        user.setEnabled(true);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            user.setProfileImageUrl(imageUrl);
        }
        userRepository.save(user);

        attributes.put("email", email);
        if (imageUrl != null) {
            attributes.put("picture", imageUrl);
        }

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(role)),
                attributes,
                "email"
        );
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        Object email = attributes.get("email");
        if (email instanceof String emailString && !emailString.isBlank()) {
            return emailString;
        }

        Object id = attributes.get("id");
        Object login = attributes.get("login");
        String providerId = id == null ? String.valueOf(login) : String.valueOf(id);
        if (providerId == null || providerId.isBlank() || "null".equals(providerId)) {
            providerId = UUID.randomUUID().toString();
        }

        return provider + "_" + providerId + "@oauth.local";
    }

    @SuppressWarnings("unchecked")
    private String extractImageUrl(String provider, Map<String, Object> attributes) {
        if ("github".equals(provider)) {
            return stringValue(attributes.get("avatar_url"));
        }
        if ("facebook".equals(provider)) {
            Object picture = attributes.get("picture");
            if (picture instanceof Map<?, ?> pictureMap) {
                Object data = pictureMap.get("data");
                if (data instanceof Map<?, ?> dataMap) {
                    return stringValue(dataMap.get("url"));
                }
            }
        }

        String picture = stringValue(attributes.get("picture"));
        if (picture != null) {
            return picture;
        }

        return stringValue(attributes.get("avatar_url"));
    }

    private String stringValue(Object value) {
        if (value instanceof String string && !string.isBlank()) {
            return string;
        }

        return null;
    }
}
