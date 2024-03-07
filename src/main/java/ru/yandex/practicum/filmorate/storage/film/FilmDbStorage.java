package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film add(Film film) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    final PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO films (name , description , release_date , duration, mpa_id) VALUES (? , ? , ? , ? , ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setLong(4, film.getDuration());
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                },
                keyHolder);
        final Integer id = keyHolder.getKeyAs(Integer.class);
        final Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(
                        "INSERT INTO films_genres (film_id, genre_id) VALUES (? , ?)",
                        id,
                        genre.getId()
                );
            }
        }
        return get(id);
    }

    @Override
    public Film get(int id) {
        try {
            Film film = jdbcTemplate.queryForObject(
                    String.format("SELECT f.id, f.name , f.description , f.release_date , f.duration, m.id, m.name FROM films as f LEFT JOIN mpa_ratings as m ON f.mpa_id = m.id WHERE f.id = %s", id),
                    (rs, num) -> extractFilm(rs)
            );
            film.setLikeUserIds(getLikeUserIds(id));
            film.setGenres(getGenres(id));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s ", id));
        }
    }

    private Set<Integer> getLikeUserIds(int id) {
        final List<Integer> likeUserIds = jdbcTemplate.query(con -> {
                    final PreparedStatement ps = con.prepareStatement("SELECT user_id FROM films_users_likes WHERE film_id = ?");
                    ps.setInt(1, id);
                    return ps;
                },
                new RowMapperResultSetExtractor<>((rs, rowNum) -> rs.getInt(1))
        );
        if (likeUserIds == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(likeUserIds);
        }
    }

    private Set<Genre> getGenres(int id) {
        final List<Genre> genres = jdbcTemplate.query(con -> {
                    final PreparedStatement ps = con.prepareStatement("SELECT g.id, g.name FROM films_genres as f LEFT JOIN genres as g ON f.genre_id = g.id WHERE f.film_id = ?");
                    ps.setInt(1, id);
                    return ps;
                },
                new RowMapperResultSetExtractor<>(
                        (rs, rowNum) -> Genre.builder()
                                .id(rs.getInt(1))
                                .name(rs.getString(2))
                                .build()
                )
        );
        if (genres == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(genres);
        }
    }

    private Film extractFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt(1))
                .name(rs.getString(2))
                .description(rs.getString(3))
                .releaseDate(rs.getDate(4).toLocalDate())
                .duration(rs.getLong(5))
                .mpa(
                        MpaRating.builder()
                                .id(rs.getInt(6))
                                .name(rs.getString(7))
                                .build()
                )
                .build();
    }

    @Override
    public Film update(Film film) {
        final int update = jdbcTemplate.update(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE ID = ?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (update == 0) {
            throw new FilmNotFoundException(String.format("Не найдено фильма с id = %s", film.getId()));
        }
        final Set<Integer> likeUserIds = film.getLikeUserIds();
        if (likeUserIds != null) {
            jdbcTemplate.update(
                    "DELETE FROM films_users_likes WHERE film_id = ?",
                    film.getId()
            );
            for (Integer userId : likeUserIds) {
                jdbcTemplate.update(
                        "INSERT INTO films_users_likes (film_id , user_id) VALUES (? , ?)",
                        film.getId(),
                        userId
                );
            }
        } else {
            film.setLikeUserIds(Collections.emptySet());
        }
        final Set<Genre> genres = film.getGenres();
        if (genres != null) {
            jdbcTemplate.update(
                    "DELETE FROM films_genres WHERE film_id = ?",
                    film.getId()
            );
            for (Genre genre : genres) {
                jdbcTemplate.update(
                        "INSERT INTO films_genres (film_id , genre_id) VALUES (? , ?)",
                        film.getId(),
                        genre.getId()
                );
            }
        } else {
            film.setGenres(Collections.emptySet());
        }
        return get(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        final List<Film> films = jdbcTemplate.query(
                con -> con.prepareStatement("SELECT f.id, f.name , f.description , f.release_date , f.duration, m.id, m.name FROM films as f LEFT JOIN mpa_ratings as m ON f.mpa_id = m.id"),
                (rs, rowNum) -> extractFilm(rs)
        );
        for (Film film : films) {
            film.setLikeUserIds(getLikeUserIds(film.getId()));
            film.setGenres(getGenres(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getFilmsSortByLike(int limit) {
        final List<Film> films = jdbcTemplate.query(
                con -> {
                    final PreparedStatement ps = con.prepareStatement(
                            "SELECT f.id, f.name , f.description , f.release_date , f.duration, m.id, m.name, sortByLikes.countLikes FROM films as f LEFT JOIN (SELECT film_id, COUNT(*) as countLikes FROM films_users_likes GROUP BY film_id) as sortByLikes ON f.id = sortByLikes.film_id LEFT JOIN mpa_ratings as m ON f.mpa_id = m.id ORDER BY countLikes DESC LIMIT ?");
                    ps.setInt(1, limit);
                    return ps;
                },
                (rs, rowNum) -> extractFilm(rs)
        );
        for (Film film : films) {
            film.setLikeUserIds(getLikeUserIds(film.getId()));
            film.setGenres(getGenres(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(int firstUserId, int secondUserId) {
        String sqlQuery = "SELECT u.film_id " +
                "FROM films_users_likes AS u " +
                "INNER JOIN (SELECT film_id FROM films_users_likes WHERE user_id = ?) AS f " +
                "ON u.film_id = f.film_id " +
                "WHERE user_id = ?";

        List<Integer> commonFilmIds = jdbcTemplate.queryForList(sqlQuery, Integer.class, firstUserId, secondUserId);
        Set<Film> resultSet = new LinkedHashSet<>();
        for (int i = 0; i <= commonFilmIds.size() - 1; i++) {
            resultSet.add(get(commonFilmIds.get(i)));
        }
        return resultSet.stream()
                .sorted((firstFilm, secondFilm) -> secondFilm.getLikeUserIds().size() - firstFilm.getLikeUserIds().size())
                .collect(Collectors.toList());
    }

    private Set<Director> getDirectors(int filmId) {
        final List<Director> directors = jdbcTemplate.query(
                con -> {
                    final PreparedStatement ps = con.prepareStatement("SELECT d.id, d.name FROM film_directors as fd LEFT JOIN directors as d ON fd.director_id = d.id WHERE fd.film_id = ?");
                    ps.setInt(1, filmId);
                    return ps;
                },
                (rs, rowNum) -> Director.builder()
                        .id(rs.getInt(1))
                        .name(rs.getString(2))
                        .build()
        );
        return new HashSet<>(directors);
    }

    @Override
    public List<Film> findByDirectorIdAndSortBy(String directorId, String sortBy) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM directors WHERE id = ?", Integer.class, directorId);
        if (count == 0) {
            throw new DirectorNotFoundException("Режиссер с ID " + directorId + " не найден");
        }
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, m.id, m.name, d.id, d.name " +
                "FROM films AS f " +
                "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.id " +
                "LEFT JOIN films_users_likes AS ful ON f.id = ful.film_id " +
                "WHERE d.id = ? ";

        switch (sortBy) {
            case "year":
                query += "GROUP BY f.id ORDER BY f.release_date ASC";
                break;
            case "likes":
                query += "GROUP BY f.id ORDER BY COUNT(ful.user_id) DESC";
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter");
        }

        return jdbcTemplate.query(query, new Object[]{directorId}, (rs, rowNum) -> {
            Film film = extractFilm(rs);
            Director director = Director.builder()
                    .id(rs.getInt(8))
                    .name(rs.getString(9))
                    .build();
            film.setDirectors(Collections.singleton(director));
            film.setGenres(getGenres(film.getId()));
            film.setLikeUserIds(getLikeUserIds(film.getId()));
            return film;
        });
    }


}
