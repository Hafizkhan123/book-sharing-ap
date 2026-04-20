package com.example.booksharing.repository;

import com.example.booksharing.model.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// ─────────────────────────────────────────────────────────────────
//  UserRepository
//  findByEmail() is required by CustomUserDetailsService for login.
//  Spring Data JPA auto-generates the SQL from the method name.
// ─────────────────────────────────────────────────────────────────
@Repository
public interface UserRepository extends JpaRepository<user, Long> {

    // Used by CustomUserDetailsService to load user during login
    Optional<user> findByEmail(String email);

    // Used by UserController to prevent duplicate registrations
    boolean existsByEmail(String email);
}
