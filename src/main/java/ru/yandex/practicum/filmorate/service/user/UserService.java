package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;

import java.util.List;
import java.util.Set;

public interface UserService {

    User add(User user);

    User getUser(int id) throws UserNotFoundException;

    boolean userExist(int id);

    User update(User user) throws UserNotFoundException;

    List<User> getAllUsers();

    boolean makeFriends(int id, int friendId) throws UserNotFoundException;

    boolean deleteFriend(int id, int friendId) throws UserNotFoundException;

    Set<User> getFriends(int id) throws UserNotFoundException;

    Set<User> getCommonFriends(int id, int otherId) throws UserNotFoundException;
}
