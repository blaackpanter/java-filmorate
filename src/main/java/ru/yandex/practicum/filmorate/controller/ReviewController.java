package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * Создание отзыва
     */
    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    /**
     * Получение всех отзывов
     */
    @GetMapping
    public List<Review> readReviews(@RequestParam(value = "filmId", defaultValue = "0", required = false) int filmId,
                                    @RequestParam(value = "count", defaultValue = "10", required = false) int count) {

        return reviewService.readReviews(filmId, count);
    }

    /**
     * Получение отзыва по ID
     */
    @GetMapping("/{id}")
    public Review readReview(@PathVariable("id") int id) {
        return reviewService.readReview(id);
    }

    /**
     * Изменение отзыва
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    /**
     * Удаление отзыва
     */
    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable("id") int id) {
        return reviewService.deleteReview(id);
    }

    /**
     * Пользователь ставит лайк отзыву
     */
    @PutMapping("/{id}/like/{userId}")
    public boolean createLike(
            @PathVariable("id") int id,
            @PathVariable("userId") int userId) {
        return reviewService.createLike(id, userId);
    }

    /**
     * Пользователь ставит дизлайк отзыву
     */
    @PutMapping("/{id}/dislike/{userId}")
    public boolean createDislike(
            @PathVariable("id") int id,
            @PathVariable("userId") int userId) {
        return reviewService.createDislike(id, userId);
    }

    /**
     * Пользователь удаляет лайк
     */
    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(
            @PathVariable("id") int id,
            @PathVariable("userId") int userId) {
        return reviewService.deleteLikeDislike(id, userId);
    }

    /**
     * Пользователь удаляет дизлайк
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public boolean deleteDislike(
            @PathVariable("id") int id,
            @PathVariable("userId") int userId) {
        return reviewService.deleteLikeDislike(id, userId);
    }
}
