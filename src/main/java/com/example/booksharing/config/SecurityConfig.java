package com.example.booksharing.config;

import com.example.booksharing.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // ── CRITICAL: keep session alive after login ──────────
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(5)
            )

            .securityContext(context -> context
                .securityContextRepository(
                    new HttpSessionSecurityContextRepository())
                .requireExplicitSave(false)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login.html",
                    "/register.html",
                    "/css/**",
                    "/js/**",
                    "/auth/login",
                    "/auth/logout",
                    "/auth/status",
                    "/users/register",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginProcessingUrl("/auth/login")
                .successHandler((req, res, auth) -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("success", true);
                    body.put("message", "Login successful");
                    body.put("username", auth.getName());
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(res.getWriter(), body);
                })
                .failureHandler((req, res, ex) -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Invalid email or password");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(res.getWriter(), body);
                })
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("message", "Logged out successfully");
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(res.getWriter(), body);
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    String accept = req.getHeader("Accept");
                    if (accept != null
                            && accept.contains("application/json")) {
                        Map<String, Object> body = new HashMap<>();
                        body.put("error", "Not logged in.");
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        new ObjectMapper().writeValue(res.getWriter(), body);
                    } else {
                        res.sendRedirect("/login.html");
                    }
                })
            );

        return http.build();
    }
}