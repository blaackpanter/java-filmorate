package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User add(User user);

    boolean userExist(int id);

    User get(int id);

    List<User> get(Collection<Integer> id);

    User update(User user);

    List<User> getAllUsers();

    boolean deleteUser(int id);
}
