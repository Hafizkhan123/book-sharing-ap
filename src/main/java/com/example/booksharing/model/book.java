package com.example.booksharing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private user owner;

    // getters and setters
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
 
    public user getOwner() {
        return owner;
    }
 
    public void setOwner(user owner) {
        this.owner = owner;
    }
}

