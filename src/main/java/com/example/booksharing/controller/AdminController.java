package com.example.booksharing.controller;

import com.example.booksharing.repository.UserRepository;
import com.example.booksharing.repository.BookRepository;
import com.example.booksharing.repository.BookSharingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookSharingRepository bookSharingRepository;

    // 🔐 Admin Check using Spring Security
    private boolean isAdmin() {

        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser")) {
            return false;
        }

        String email = auth.getName();

        System.out.println("Logged in user: " + email);

        return "hafiz@test.com".equals(email);
    }

    // 📊 Stats API
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {

        if (!isAdmin()) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalBooks", bookRepository.count());
        stats.put("totalShares", bookSharingRepository.count());
        stats.put("activeBorrows", bookSharingRepository.countByReturnedFalse());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {

        if (!isAdmin()) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/books")
    public ResponseEntity<?> getAllBooks() {

        if (!isAdmin()) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/shares")
    public ResponseEntity<?> getAllShares() {

        if (!isAdmin()) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        return ResponseEntity.ok(bookSharingRepository.findAll());
    }
}