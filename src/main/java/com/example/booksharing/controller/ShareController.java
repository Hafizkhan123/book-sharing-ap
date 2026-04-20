package com.example.booksharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.booksharing.model.booksharing;
import com.example.booksharing.service.BookSharingService;

@RestController
@RequestMapping("/share")
public class ShareController {

    @Autowired
    private BookSharingService bookSharingService;

    // ✅ Share a book with another user
    @PostMapping("/{bookId}/{toUserId}")
    public booksharing shareBook(@PathVariable Long bookId,
                                 @PathVariable Long toUserId) {
        return bookSharingService.shareBook(bookId, toUserId);
    }

    // ✅ Get all shared books (history / admin)
    @GetMapping
    public List<booksharing> getAllSharedBooks() {
        return bookSharingService.getAllSharedBooks();
    }

    // ✅ Get books shared TO a user (receiver)
    @GetMapping("/to/{userId}")
    public List<booksharing> getBooksSharedToUser(@PathVariable Long userId) {
        return bookSharingService.getBooksSharedToUser(userId);
    }

    // ✅ Get books shared BY a user (sender)
    @GetMapping("/from/{userId}")
    public List<booksharing> getBooksSharedByUser(@PathVariable Long userId) {
        return bookSharingService.getBooksSharedByUser(userId);
    }

    // ✅ Get borrowed books (only SHARED, not returned yet)
    @GetMapping("/borrowed/{userId}")
    public List<booksharing> getBorrowedBooks(@PathVariable Long userId) {
        return bookSharingService.getBorrowedBooks(userId);
    }

    // ✅ Return a book
    @PutMapping("/return/{shareId}")
    public booksharing returnBook(@PathVariable Long shareId) {
        return bookSharingService.returnBook(shareId);
    }
}