package com.example.booksharing.service;

import com.example.booksharing.exception.ResourceNotFoundException;
import com.example.booksharing.model.book;
import com.example.booksharing.model.booksharing;
import com.example.booksharing.model.user;
import com.example.booksharing.repository.BookRepository;
import com.example.booksharing.repository.BookSharingRepository;
import com.example.booksharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// ─────────────────────────────────────────────────────────────
//  Step D2 — Every .orElseThrow() now throws ResourceNotFoundException
//  instead of crashing silently or returning null.
//
//  Before:  bookRepo.findById(bookId).orElseThrow()     ← generic error
//  After:   .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId))
//                                                        ← 404 + message
// ─────────────────────────────────────────────────────────────
@Service
public class BookSharingService {

    @Autowired
    private BookSharingRepository shareRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserRepository userRepo;

    // ── Share a book ──────────────────────────────────────────
    public booksharing shareBook(Long bookId, Long toUserId) {

        // D2: descriptive 404 if book doesn't exist
        book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id: " + bookId));

        user fromUser = book.getOwner();

        // D2: descriptive 404 if recipient user doesn't exist
        user toUser = userRepo.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + toUserId));

        booksharing bs = new booksharing();
        bs.setBook(book);
        bs.setFromUser(fromUser);
        bs.setToUser(toUser);
        bs.setShareDate(LocalDate.now());
        bs.setStatus("SHARED");

        return shareRepo.save(bs);
    }

    // ── Get all sharing records ───────────────────────────────
    public List<booksharing> getAllSharedBooks() {
        return shareRepo.findAll();
    }

    // ── Books shared TO a user ────────────────────────────────
    public List<booksharing> getBooksSharedToUser(Long userId) {
        return shareRepo.findByToUserId(userId);
    }

    // ── Books shared BY a user ────────────────────────────────
    public List<booksharing> getBooksSharedByUser(Long userId) {
        return shareRepo.findByFromUserId(userId);
    }

    // ── Currently borrowed (status = SHARED) ─────────────────
    public List<booksharing> getBorrowedBooks(Long userId) {
        return shareRepo.findByToUserIdAndStatus(userId, "SHARED");
    }

    // ── Return a book ─────────────────────────────────────────
    public booksharing returnBook(Long shareId) {

        // D2: descriptive 404 if share record doesn't exist
        booksharing bs = shareRepo.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sharing record not found with id: " + shareId));

        bs.setStatus("RETURNED");
        bs.setReturnDate(LocalDate.now());

        return shareRepo.save(bs);
    }
}
