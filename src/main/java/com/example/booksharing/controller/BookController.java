package com.example.booksharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.booksharing.model.book;
import com.example.booksharing.model.user;
import com.example.booksharing.repository.BookRepository;
import com.example.booksharing.repository.UserRepository;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserRepository userRepo;

    // Add a book for a user
    @PostMapping("/add/{userId}")
    public book addBook(@RequestBody book book, @PathVariable Long userId) {
        user owner = userRepo.findById(userId).orElse(null);
        book.setOwner(owner);
        return bookRepo.save(book);
    }

    // Get all books
    @GetMapping
    public List<book> getAllBooks() {
        return bookRepo.findAll();
    }
}
