package com.example.booksharing.controller;

import com.example.booksharing.exception.ResourceNotFoundException;
import com.example.booksharing.model.book;
import com.example.booksharing.model.user;
import com.example.booksharing.repository.BookRepository;
import com.example.booksharing.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(originPatterns = "*")
public class BookController {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/add/{userId}")
    public book addBook(@PathVariable Long userId,
                        @Valid @RequestBody book book) {

        user owner = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        book.setOwner(owner);
        return bookRepo.save(book);
    }

    @GetMapping
    public List<book> getAllBooks() {
        return bookRepo.findAll();
    }

    @GetMapping("/{id}")
    public book getBook(@PathVariable Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id: " + id));
    }
}