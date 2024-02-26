package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User add(User user);

    boolean userExist(int id);

    User get(int id) throws UserNotFoundException;

    List<User> get(Collection<Integer> id) throws UserNotFoundException;

    User update(User user) throws UserNotFoundException;

    List<User> getAllUsers();
}
