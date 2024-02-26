package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        int id = users.size() + 1;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public boolean userExist(int id) {
        return users.containsKey(id);
    }

    @Override
    public User get(int id) throws UserNotFoundException {
        final User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s", user.getId()));
        }
        return user;
    }

    @Override
    public List<User> get(Collection<Integer> ids) throws UserNotFoundException {
        final List<User> result = new ArrayList<>();
        for (Integer id : ids) {
            result.add(get(id));
        }
        return result;
    }

    @Override
    public User update(User user) throws UserNotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(String.format("Не найдено пользователя с id = %s", user.getId()));
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Заменили пустое имя на логин");
        }
    }
}
