package com.example.booksharing.controller;

import com.example.booksharing.exception.ResourceNotFoundException;
import com.example.booksharing.model.user;
import com.example.booksharing.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    // ── Use BCryptPasswordEncoder directly instead of injecting
    //    the bean — this avoids any wiring issues
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public user registerUser(@Valid @RequestBody user user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @GetMapping("/{id}")
    public user getUser(@PathVariable Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
    }

    @GetMapping
    public List<user> getAllUsers() {
        return userRepo.findAll();
    }
}