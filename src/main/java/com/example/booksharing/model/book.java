package com.example.booksharing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

// ─────────────────────────────────────────────────────────────
//  Step D1 — Validation added to book model
//  title and author cannot be blank.
// ─────────────────────────────────────────────────────────────
@Entity
@Table(name = "books")
public class book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // D1: title cannot be blank
    @NotBlank(message = "Title must not be blank")
    private String title;

    // D1: author cannot be blank
    @NotBlank(message = "Author must not be blank")
    private String author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"password"})
    private user owner;

    // ── Constructors ──────────────────────────────────────────
    public book() {}

    public book(String title, String author, user owner) {
        this.title  = title;
        this.author = author;
        this.owner  = owner;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public String getTitle()             { return title; }
    public void setTitle(String title)   { this.title = title; }

    public String getAuthor()              { return author; }
    public void setAuthor(String author)   { this.author = author; }

    public user getOwner()             { return owner; }
    public void setOwner(user owner)   { this.owner = owner; }
}
