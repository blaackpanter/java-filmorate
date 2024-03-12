package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@Profile("in_memory")
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User add(User user) {
        id += 1;
        user.setId(id);
        user.setFriends(Collections.emptySet());
        users.put(id, user);
        return user;
    }

    @Override
    public boolean userExist(int id) {
        return users.containsKey(id);
    }

    @Override
    public User get(int id) {
        final User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s", id));
        }
        return user;
    }

    @Override
    public List<User> get(Collection<Integer> ids) {
        final List<User> result = new ArrayList<>();
        for (Integer id : ids) {
            result.add(get(id));
        }
        return result;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s", user.getId()));
        }
        if (user.getFriends() == null) {
            user.setFriends(Collections.emptySet());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public boolean deleteUser(int id) {
        return users.remove(id) != null;
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Заменили пустое имя на логин");
        }
    }
}
