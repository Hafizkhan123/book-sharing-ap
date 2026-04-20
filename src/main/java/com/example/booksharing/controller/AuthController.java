package com.example.booksharing.controller;

import com.example.booksharing.model.user;
import com.example.booksharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();

        boolean loggedIn = auth != null
                && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser");

        response.put("loggedIn", loggedIn);

        if (loggedIn) {
            response.put("email", auth.getName());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {

        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser")) {

            return ResponseEntity.status(401)
                    .body(Map.of("error", "Not logged in"));
        }

        String email = auth.getName();

        Optional<user> found = userRepo.findByEmail(email);

        Map<String, Object> response = new HashMap<>();

        if (found.isPresent()) {
            user u = found.get();

            response.put("id", u.getId());
            response.put("name", u.getName());
            response.put("email", u.getEmail());
        } else {
            response.put("email", email);
        }

        return ResponseEntity.ok(response);
    }
}