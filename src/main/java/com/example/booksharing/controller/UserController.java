package com.example.booksharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.booksharing.model.user;
import com.example.booksharing.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    // Register a new user
    @PostMapping("/register")
    public user registerUser(@RequestBody user user) {
        return userRepo.save(user);
    }

    // Get user by id
    @GetMapping("/{id}")
    public user getUser(@PathVariable Long id) {
        return userRepo.findById(id).orElse(null);
    }
}
