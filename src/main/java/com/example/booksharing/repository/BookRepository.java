package com.example.booksharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.booksharing.model.book;

public interface BookRepository extends JpaRepository<book, Long> {
}
