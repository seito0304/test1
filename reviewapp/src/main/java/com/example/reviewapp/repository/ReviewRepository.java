package com.example.reviewapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.reviewapp.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 必要ならカスタムメソッドもここに追加できます
}
