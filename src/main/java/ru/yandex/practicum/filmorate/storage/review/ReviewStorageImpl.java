package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Component
public class ReviewStorageImpl implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {
        String sqlQuery = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
                stmt.setString(1, review.getContent());
                stmt.setBoolean(2, review.getIsPositive());
                stmt.setInt(3, review.getUserId());
                stmt.setInt(4, review.getFilmId());
                stmt.setInt(5, 0);
                return stmt;
            }, keyHolder);
            review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            log.info("В таблице REVIEWS успешно создана запись с ID {}.", review.getReviewId());
        } catch (Exception e) {
            log.warn("Попытка создания записи в таблице REVIEWS. Запрос: {}. Ошибка: {}.",
                    sqlQuery, "Ошибка: " + e.getMessage());
            throw e;
        }

        return review;
    }

    @Override
    public Review readReview(int id) {
        Review review;
        String sqlQuery = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, SUM(IFNULL(RL.TYPE, 0)) AS USEFUL " +
                "FROM REVIEWS as R " +
                "         LEFT JOIN REVIEWS_USERS_LIKES RL ON RL.REVIEW_ID = R.REVIEW_ID " +
                "WHERE R.REVIEW_ID = ?" +
                "GROUP BY R.REVIEW_ID;";
        try {
            review = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
            log.info("В таблице REVIEW успешно выбрали запись с id {}.", id);
        } catch (Exception e) {
            String warning = "Попытка выборки записи с id " + id + " в таблице REVIEW. " +
                    "Запрос: " + sqlQuery
                    + "Ошибка: " + e.getMessage();
            log.warn(warning);
            throw new ReviewNotFoundException(warning);
        }

        return review;
    }

    @Override
    public List<Review> readReviews(int size) {
        String sqlQuery = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, SUM(IFNULL(RL.TYPE, 0)) AS USEFUL " +
                "FROM REVIEWS as R " +
                "         LEFT JOIN REVIEWS_USERS_LIKES RL ON RL.REVIEW_ID = R.REVIEW_ID " +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?;";

        log.info("Выборка записей из таблицы REVIEWS.");
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, size);
    }

    @Override
    public List<Review> readReviewsByFilm(int filmId, int size) {
        String sqlQuery = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, SUM(IFNULL(RL.TYPE, 0)) AS USEFUL " +
                "FROM REVIEWS as R " +
                "         LEFT JOIN REVIEWS_USERS_LIKES RL ON RL.REVIEW_ID = R.REVIEW_ID " +
                "WHERE R.FILM_ID = ? " +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";

        log.info("Выборка записей из таблицы REVIEWS.");
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, size);
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";
        int resultUpdate = jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (resultUpdate <= 0) {
            String warning = "Попытка обновления записи в таблице REVIEWS. " +
                    "Запрос: " + sqlQuery;
            log.warn(warning);
            throw new ReviewNotFoundException(warning);
        }

        log.info("В таблице REVIEW обновление записи с ID {}.",
                review.getReviewId());
        return readReview(review.getReviewId());
    }

    @Override
    public boolean deleteReview(int id) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";

        if (jdbcTemplate.update(sqlQuery, id) <= 0) {
            log.warn("Попытка удаления записи в таблице REVIEWS. Запрос: {}. ID обзора - {}.",
                    sqlQuery, id);
            return false;
        } else {
            log.info("В таблице REVIEWS_USERS_LIKES успешно удалена запись с REVIEW_ID - {}.",
                    id);
            return true;
        }
    }

    @Override
    public boolean createLikeDislike(int reviewId, int userId, int type) {
        String sqlQuery = "INSERT INTO REVIEWS_USERS_LIKES (REVIEW_ID, USER_ID, TYPE) " +
                "VALUES (?, ?, ?)";

        if (jdbcTemplate.update(sqlQuery, reviewId, userId, type) <= 0) {
            log.warn("Попытка создания записи в таблице REVIEWS_USERS_LIKES. Запрос: {}. ID обзора - {}. ID пользователя - {}. Тип - {}.",
                    sqlQuery, reviewId, userId, type);
            return false;
        } else {

            log.info("В таблице REVIEWS_USERS_LIKES успешно создана запись с REVIEW_ID - {}, USER_ID - {}, Тип - {}",
                    reviewId, userId, type);
            return true;
        }
    }

    @Override
    public boolean deleteLikeDislike(int reviewId, int userId) {
        String sqlQuery = "DELETE FROM REVIEWS_USERS_LIKES " +
                "WHERE REVIEW_ID = ? AND USER_ID = ?";

        if (jdbcTemplate.update(sqlQuery, reviewId, userId) <= 0) {
            log.warn("Попытка удаления записи в таблице REVIEWS_USERS_LIKES. Запрос: {}. ID обзора - {}. ID пользователя - {}.",
                    sqlQuery, reviewId, userId);
            return false;
        } else {
            log.info("В таблице REVIEWS_USERS_LIKES успешно удалена запись с REVIEW_ID - {} и USER_ID - {}.",
                    reviewId, userId);
            return true;
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        Integer reviewId = rs.getInt("REVIEW_ID");
        String content = rs.getString("CONTENT");
        Boolean isPositive = rs.getBoolean("IS_POSITIVE");
        Integer userId = rs.getInt("USER_ID");
        Integer filmId = rs.getInt("FILM_ID");
        Integer useful = rs.getInt("USEFUL");

        return new Review(reviewId, content, isPositive, userId, filmId, useful);
    }
}
