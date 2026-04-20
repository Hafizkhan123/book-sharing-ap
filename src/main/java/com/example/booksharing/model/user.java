package com.example.booksharing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// ─────────────────────────────────────────────────────────────
//  Step D1 — Validation added to user model
//
//  @NotBlank  → field cannot be null or empty/whitespace
//  @Email     → must be a valid email format
//
//  These annotations work together with @Valid in the controller.
//  Spring automatically rejects bad input before it hits the DB.
// ─────────────────────────────────────────────────────────────
@Entity
@Table(name = "users")
public class user {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // D1: name cannot be blank
    @NotBlank(message = "Name must not be blank")
    private String name;

    // D1: email must be valid format AND not blank
    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email must not be blank")
    @Column(unique = true)
    private String email;

    // D1: password cannot be blank
    @NotBlank(message = "Password must not be blank")
    private String password;

    // ── Constructors ──────────────────────────────────────────
    public user() {}

    public user(String name, String email, String password) {
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public Long getId()               { return id; }
    public void setId(Long id)        { this.id = id; }

    public String getName()           { return name; }
    public void setName(String name)  { this.name = name; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }
}
