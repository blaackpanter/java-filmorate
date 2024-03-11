package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review createReview(Review review);

    Review readReview(int id);

    List<Review> readReviews(int count);

    List<Review> readReviewsByFilm(int filmId, int size);

    Review updateReview(Review review);

    boolean deleteReview(int id);

    boolean createLikeDislike(int id, int userId, int type);

    boolean deleteLikeDislike(int id, int userId);
}
