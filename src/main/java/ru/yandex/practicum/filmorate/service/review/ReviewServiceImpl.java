package ru.yandex.practicum.filmorate.service.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review createReview(Review review) {
        if (filmStorage.get(review.getFilmId()) == null)
            throw new FilmNotFoundException("Фильм с id " + review.getFilmId() + " не найден в БД.");
        if (userStorage.get(review.getUserId()) == null)
            throw new UserNotFoundException("Пользователь с id " + review.getUserId() + " не найден в БД.");
        return reviewStorage.createReview(review);
    }

    @Override
    public Review readReview(int id) {
        return reviewStorage.readReview(id);
    }

    @Override
    public List<Review> readReviews(int filmId, int count) {
        if (filmId == 0)
            return reviewStorage.readReviews(count);
        return reviewStorage.readReviewsByFilm(filmId, count);
    }

    @Override
    public Review updateReview(Review review) {
        if (reviewStorage.readReview(review.getReviewId()) == null)
            throw new ReviewNotFoundException("Отзыв не найден в БД.");

        return reviewStorage.updateReview(review);
    }

    @Override
    public boolean deleteReview(int id) {
        return reviewStorage.deleteReview(id);
    }

    @Override
    public boolean createLike(int reviewId, int userId) {
        checkParametersForLike(reviewId, userId);
        reviewStorage.deleteLikeDislike(reviewId, userId);

        return reviewStorage.createLikeDislike(reviewId, userId, 1);
    }

    @Override
    public boolean createDislike(int reviewId, int userId) {
        checkParametersForLike(reviewId, userId);
        reviewStorage.deleteLikeDislike(reviewId, userId);

        return reviewStorage.createLikeDislike(reviewId, userId, -1);
    }

    @Override
    public boolean deleteLikeDislike(int reviewId, int userId) {
        checkParametersForLike(reviewId, userId);

        return reviewStorage.deleteLikeDislike(reviewId, userId);
    }

    private void checkParametersForLike(int reviewId, int userId) {
        if (reviewStorage.readReview(reviewId) == null
                || userStorage.get(userId) == null)
            throw new ReviewNotFoundException("Не найден отзыв с ID" + reviewId + " или пользователь " + userId);
    }
}
