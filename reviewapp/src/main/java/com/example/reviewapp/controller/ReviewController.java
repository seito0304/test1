package com.example.reviewapp.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.reviewapp.model.Review;
import com.example.reviewapp.repository.ReviewRepository;
import com.example.reviewapp.service.AzureAiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final AzureAiService azureAiService;

    /**
     * 口コミ投稿フォームを表示
     */
    @GetMapping("/form")
    public String showReviewForm(Model model) {
        model.addAttribute("review", new Review());
        return "review_form"; // templates/review_form.html
    }

    /**
     * フォームからの投稿を受け取り、AI分析後に保存し一覧へリダイレクト
     */
    @PostMapping("/submit")
    public String submitReview(@ModelAttribute Review review) {
        // AI感情分析
        try {
            Map<String, Object> result = azureAiService.analyzeSentiment(review.getReview());
            review.setSentiment((String) result.get("sentiment"));
            review.setSentimentScore(((Double) result.get("score")).floatValue());
        } catch (Exception e) {
            e.printStackTrace();
            review.setSentiment("error");
            review.setSentimentScore(0f);
        }
        review.setDate(LocalDate.now());
        reviewRepository.save(review);
        return "redirect:/reviews";
    }

    /**
     * 口コミ一覧を表示
     */
    @GetMapping
    public String showAllReviews(Model model) {
        List<Review> reviews = reviewRepository.findAll();
        model.addAttribute("reviews", reviews);
        return "review_list"; // templates/review_list.html
    }
    @PostMapping("/submit")
    public String submitReview(@ModelAttribute Review review) {
        try {
            // 感情分析
            Map<String, Object> sentimentResult = azureAiService.analyzeSentiment(review.getReview());
            review.setSentiment((String) sentimentResult.get("sentiment"));
            review.setSentimentScore(((Double) sentimentResult.get("score")).floatValue());

            // キーワード抽出
            List<String> keywords = azureAiService.extractKeywords(review.getReview());
            review.setKeywords(keywords);

        } catch (Exception e) {
            e.printStackTrace();
            review.setSentiment("error");
            review.setSentimentScore(0f);
        }

        review.setDate(LocalDate.now());
        reviewRepository.save(review);
        return "redirect:/reviews";
    }
}
