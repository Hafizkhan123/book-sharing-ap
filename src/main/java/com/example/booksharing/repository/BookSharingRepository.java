package com.example.booksharing.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.booksharing.model.booksharing;

public interface BookSharingRepository extends JpaRepository<booksharing, Long> {

    // Books shared TO a user (receiver)
    List<booksharing> findByToUserId(Long userId);

    // Books shared BY a user (sender)
    List<booksharing> findByFromUserId(Long userId);

    // ✅ ONLY active borrowed books
    List<booksharing> findByToUserIdAndStatus(Long userId, String status);

    long countByReturnedFalse();

}