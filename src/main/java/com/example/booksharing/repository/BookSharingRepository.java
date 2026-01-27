package com.example.booksharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.booksharing.model.booksharing;

public interface BookSharingRepository extends JpaRepository<booksharing, Long> {
}
