package com.example.booksharing.service;

import com.example.booksharing.model.user;
import com.example.booksharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // findByEmail() now exists in UserRepository
        user foundUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No account found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                foundUser.getEmail(),
                foundUser.getPassword(),
                new ArrayList<>()
        );
    }
}
