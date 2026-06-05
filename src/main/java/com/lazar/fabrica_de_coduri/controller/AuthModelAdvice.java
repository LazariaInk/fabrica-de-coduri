package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AuthModelAdvice {
    private final UserRepository userRepository;

    public AuthModelAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("authLoggedIn")
    public boolean authLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        if (!authLoggedIn(authentication)) {
            return "";
        }

        return authentication.getName();
    }

    @ModelAttribute("currentUserAvatarUrl")
    public String currentUserAvatarUrl(Authentication authentication) {
        if (!authLoggedIn(authentication)) {
            return "";
        }

        return userRepository.findByUsername(authentication.getName())
                .map(user -> user.getProfileImageUrl() == null ? "" : user.getProfileImageUrl())
                .orElse("");
    }

    @ModelAttribute("currentUserInitial")
    public String currentUserInitial(Authentication authentication) {
        if (!authLoggedIn(authentication) || !StringUtils.hasText(authentication.getName())) {
            return "U";
        }

        return authentication.getName().substring(0, 1).toUpperCase();
    }
}
