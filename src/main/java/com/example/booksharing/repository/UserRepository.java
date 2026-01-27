package com.example.booksharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.booksharing.model.user;

public interface UserRepository extends JpaRepository<user, Long> {
}
