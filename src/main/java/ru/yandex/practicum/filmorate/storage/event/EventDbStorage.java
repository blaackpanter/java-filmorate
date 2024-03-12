package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public Event createEvent(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO EVENTS (EVENT_TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"EVENT_ID"});
            ps.setLong(1, event.getTimestamp());
            ps.setInt(2, event.getUserId());
            ps.setString(3, event.getEventType().getTitle());
            ps.setString(4, event.getOperation().getTitle());
            ps.setInt(5, event.getEntityId());
            return ps;
        }, keyHolder);

        event.setId(keyHolder.getKey().intValue());
        return event;
    }

    @Override
    public List<Event> findEventsByUserID(Integer id) {

        if (!userStorage.userExist(id)) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapToEvent, id);
    }

    private Event mapToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getInt("EVENT_ID"));
        event.setTimestamp(resultSet.getLong("EVENT_TIMESTAMP"));
        EventType eventType = EventType.valueOf(resultSet.getString("EVENT_TYPE"));
        event.setEventType(eventType);
        OperationType operationType = OperationType.valueOf(resultSet.getString("OPERATION"));
        event.setOperation(operationType);
        event.setUserId(resultSet.getInt("USER_ID"));
        event.setEntityId(resultSet.getInt("ENTITY_ID"));

        return event;
    }
}
