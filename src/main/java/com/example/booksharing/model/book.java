package com.example.booksharing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

// ─────────────────────────────────────────────────────────────
// Book Entity
// Stores book details + S3 image reference
// ─────────────────────────────────────────────────────────────
@Entity
@Table(name = "books")
public class book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Book title cannot be blank
    @NotBlank(message = "Title must not be blank")
    private String title;

    // Book author cannot be blank
    @NotBlank(message = "Author must not be blank")
    private String author;

    // S3 object key / image filename
    // Example:
    // 550e8400-e29b-41d4-a716-446655440000-book.png
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"password"})
    private user owner;

    // ── Constructors ──────────────────────────────────────────

    public book() {}

    public book(String title, String author, user owner) {
        this.title = title;
        this.author = author;
        this.owner = owner;
    }

    // Constructor with image
    public book(String title, String author, String imageUrl, user owner) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.owner = owner;
    }

    // ── Getters & Setters ─────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public user getOwner() {
        return owner;
    }

    public void setOwner(user owner) {
        this.owner = owner;
    }
}