package com.example.booksharing.controller;

import com.example.booksharing.exception.ResourceNotFoundException;
import com.example.booksharing.model.book;
import com.example.booksharing.model.user;
import com.example.booksharing.repository.BookRepository;
import com.example.booksharing.repository.UserRepository;
import com.example.booksharing.service.S3Service;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(originPatterns = "*")
public class BookController {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private S3Service s3Service;


    // =========================================================
    // ADD BOOK WITHOUT IMAGE
    // =========================================================

    @PostMapping("/add/{userId}")
    public book addBook(
            @PathVariable Long userId,
            @Valid @RequestBody book book) {

        user owner = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        book.setOwner(owner);

        return bookRepo.save(book);
    }


    // =========================================================
    // ADD BOOK WITH IMAGE
    // =========================================================
@PostMapping(
        value = "/add-with-image/{userId}",
        consumes = "multipart/form-data"
)
public book addBookWithImage(
        @PathVariable Long userId,
        @RequestParam("title") String title,
        @RequestParam("author") String author,
        @RequestParam(value = "file", required = false) MultipartFile file)
        throws IOException {

    user owner = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found with id: " + userId));

    book newBook = new book();

    newBook.setTitle(title);
    newBook.setAuthor(author);
    newBook.setOwner(owner);

    // Upload image to S3 if provided
    if (file != null && !file.isEmpty()) {

    String imageKey = s3Service.uploadFile(file);

    String imageUrl = s3Service.getFileUrl(imageKey);

    newBook.setImageUrl(imageUrl);
}

    return bookRepo.save(newBook);
}


    // =========================================================
    // GET ALL BOOKS
    // =========================================================

    @GetMapping
public List<book> getAllBooks() {

    List<book> books = bookRepo.findAll();

    for (book b : books) {

        if (b.getImageUrl() != null && !b.getImageUrl().isBlank()) {

            String imageUrl = b.getImageUrl();

            // If database contains only the S3 key,
            // convert it into the complete S3 URL.
            if (!imageUrl.startsWith("http")) {
                imageUrl = s3Service.getFileUrl(imageUrl);
                b.setImageUrl(imageUrl);
            }
        }
    }

    return books;
}


    // =========================================================
    // GET BOOK BY ID
    // =========================================================

    @GetMapping("/{id}")
    public book getBook(@PathVariable Long id) {

        return bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id: " + id));
    }
}