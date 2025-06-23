package com.example.reviewapp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_reviews")
public class Review {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")  // DB上のカラム名に合わせる（念のため明示）
    private Long id;

    private Integer age;

    private String gender;

    private String category;

    private String product;

    @Column(columnDefinition = "TEXT")
    private String review;

    private String sentiment;

    @Column(name = "sentiment_score")
    private Float sentimentScore;

    private LocalDate  date;
}
