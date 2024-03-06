package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(Review review);

    Review readReview(int id);

    List<Review> readReviews(int filmId, int count);

    Review updateReview(Review review);

    boolean deleteReview(int id);

    boolean createLike(int reviewId, int userId);

    boolean createDislike(int reviewId, int userId);

    boolean deleteLikeDislike(int reviewId, int userId);
}
