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
                    final PreparedStatement ps = con.prepareStatement("SELECT user_id FROM films_users_likes WHERE user_id = ?");
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
                            "SELECT * FROM films " +
                                    "LEFT JOIN (" +
                                    "SELECT film_id, COUNT(*) as countLikes " +
                                    "FROM films_users_likes GROUP BY film_id ORDER BY countLikes DESC" +
                                    ") as sortByLikes " +
                                    "ON films.id = sortByLikes.film_id LIMIT ?");
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
}
