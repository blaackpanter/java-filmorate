package ru.yandex.practicum.filmorate.service.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    @Override
    public Review createReview(Review review) {
        if (filmStorage.get(review.getFilmId()) == null)
            throw new NotFoundException("Фильм с id " + review.getFilmId() + " не найден в БД.");
        if (userStorage.get(review.getUserId()) == null)
            throw new NotFoundException("Пользователь с id " + review.getUserId() + " не найден в БД.");
        Review createdReview = reviewStorage.createReview(review);
        eventService.createReviewEvent(createdReview.getUserId(), OperationType.ADD, createdReview.getReviewId());
        return createdReview;
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
        Review reviewLoaded = reviewStorage.readReview(review.getReviewId());
        if (reviewLoaded == null) {
            throw new NotFoundException("Отзыв не найден в БД.");
        }
        eventService.createReviewEvent(reviewLoaded.getUserId(), OperationType.UPDATE, reviewLoaded.getReviewId());
        return reviewStorage.updateReview(review);
    }

    @Override
    public boolean deleteReview(int id) {
        Review review = reviewStorage.readReview(id);
        if (review == null) {
            throw new NotFoundException("Отзыв не найден в БД.");
        }
        eventService.createReviewEvent(review.getUserId(), OperationType.REMOVE, review.getReviewId());
        return reviewStorage.deleteReview(review.getReviewId());
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
            throw new NotFoundException("Не найден отзыв с ID" + reviewId + " или пользователь " + userId);
    }
}
