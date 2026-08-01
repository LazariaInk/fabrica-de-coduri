package com.lazar.fabrica_de_coduri.config;


import com.lazar.fabrica_de_coduri.service.CustomUserDetailsService;
import com.lazar.fabrica_de_coduri.service.CustomOAuth2UserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository,
                                           CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/courses/*/buy", "/courses/*/watch", "/courses/*/wishlist", "/courses/*/progress", "/courses/*/comments", "/courses/*/videos/*/stream", "/courses/*/videos/*/progress").authenticated()
                        .requestMatchers("/account/**").authenticated()
                        .requestMatchers("/forgot-password", "/reset-password").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/courses", false)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        if (clientRegistrationRepository.getIfAvailable() != null) {
            http.oauth2Login(oauth -> oauth
                    .loginPage("/login")
                    .defaultSuccessUrl("/courses", false)
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            );
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return bcrypt.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (encodedPassword == null) {
                    return false;
                }

                if (encodedPassword.startsWith("$2a$")
                        || encodedPassword.startsWith("$2b$")
                        || encodedPassword.startsWith("$2y$")) {
                    return bcrypt.matches(rawPassword, encodedPassword);
                }

                return encodedPassword.contentEquals(rawPassword);
            }
        };
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
