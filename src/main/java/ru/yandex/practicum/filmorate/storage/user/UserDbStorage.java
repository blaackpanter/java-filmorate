package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    final PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO users (email , login , name , birthday) VALUES (? , ? , ? , ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                },
                keyHolder);
        int id = keyHolder.getKeyAs(Integer.class);
        user.setId(id);
        return user;
    }

    @Override
    public boolean userExist(int id) {
        try {
            final Integer count = jdbcTemplate.queryForObject(
                    String.format("SELECT count(*) FROM users WHERE id = %s", id),
                    (rs, num) -> rs.getInt(1)
            );
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s", id));
        }
    }

    @Override
    public User get(int id) {
        try {
            User user = jdbcTemplate.queryForObject(
                    String.format("SELECT id, email, login, name, birthday FROM users WHERE id = %s", id),
                    (rs, num) -> extractUser(rs)
            );
            user.setFriends(getFriendIds(id));
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s", id));
        }
    }

    private Set<Integer> getFriendIds(int id) {
        final List<Integer> friendIdList = jdbcTemplate.query(con -> {
                    final PreparedStatement ps = con.prepareStatement("SELECT to_user_id FROM users_friend_list WHERE from_user_id = ?");
                    ps.setInt(1, id);
                    return ps;
                },
                new RowMapperResultSetExtractor<>((rs, rowNum) -> rs.getInt(1))
        );
        if (friendIdList == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(friendIdList);
        }
    }

    private User extractUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt(1))
                .email(rs.getString(2))
                .login(rs.getString(3))
                .name(rs.getString(4))
                .birthday(rs.getDate(5).toLocalDate())
                .build();
    }

    @Override
    public List<User> get(Collection<Integer> id) {
        return id.stream().map(this::get).collect(Collectors.toList());
    }

    @Override
    public User update(User user) {
        final int update = jdbcTemplate.update(
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE ID = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        if (update == 0) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s", user.getId()));
        }
        final Set<Integer> friendIds = user.getFriends();
        if (friendIds != null && !friendIds.isEmpty()) {
            jdbcTemplate.update(
                    "DELETE FROM users_friend_list WHERE from_user_id = ?",
                    user.getId()
            );
            for (Integer friendId : friendIds) {
                jdbcTemplate.update(
                        "INSERT INTO users_friend_list (from_user_id , to_user_id , accepted ) VALUES (? , ? , true)",
                        user.getId(),
                        friendId
                );
            }
        }
        user.setFriends(friendIds == null ? Collections.emptySet() : friendIds);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        final List<User> users = jdbcTemplate.query(
                con -> con.prepareStatement("SELECT id, email, login, name, birthday FROM users"),
                (rs, rowNum) -> extractUser(rs)
        );
        for (User user : users) {
            user.setFriends(getFriendIds(user.getId()));
        }
        return users;
    }
}
