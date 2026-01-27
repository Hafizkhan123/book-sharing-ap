package com.example.booksharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import com.example.booksharing.model.book;
import com.example.booksharing.model.booksharing;
import com.example.booksharing.model.user;
import com.example.booksharing.repository.BookRepository;
import com.example.booksharing.repository.BookSharingRepository;
import com.example.booksharing.repository.UserRepository;

@RestController
@RequestMapping("/share")
public class ShareController {

    @Autowired
    private BookSharingRepository shareRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserRepository userRepo;

    // Share a book with another user
    @PostMapping("/{bookId}/{toUserId}")
    public booksharing shareBook(@PathVariable Long bookId, @PathVariable Long toUserId) {

        book book = bookRepo.findById(bookId).orElse(null);
        user fromUser = book.getOwner();
        user toUser = userRepo.findById(toUserId).orElse(null);

        booksharing bs = new booksharing();
        bs.setBook(book);
        bs.setFromUser(fromUser);
        bs.setToUser(toUser);
        bs.setShareDate(LocalDate.now());
        bs.setStatus("SHARED");

        return shareRepo.save(bs);
    }
}
