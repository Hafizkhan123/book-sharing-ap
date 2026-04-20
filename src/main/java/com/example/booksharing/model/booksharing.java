package com.example.booksharing.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book_sharing")
public class booksharing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private book book;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private user fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private user toUser;

    private LocalDate shareDate;
    private LocalDate returnDate;
    private String status;

    private boolean returned = false;

    // getters and setters
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public book getBook() {
        return book;
    }
 
    public void setBook(book book) {
        this.book = book;
    }
 
    public user getFromUser() {
        return fromUser;
    }
 
    public void setFromUser(user fromUser) {
        this.fromUser = fromUser;
    }
 
    public user getToUser() {
        return toUser;
    }
 
    public void setToUser(user toUser) {
        this.toUser = toUser;
    }
 
    public LocalDate getShareDate() {
        return shareDate;
    }
 
    public void setShareDate(LocalDate shareDate) {
        this.shareDate = shareDate;
    }
 
    public LocalDate getReturnDate() {
        return returnDate;
    }
 
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
}

