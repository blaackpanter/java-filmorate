package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    User add(User user);

    User getUser(int id);

    boolean userExist(int id);

    User update(User user);

    List<User> getAllUsers();

    boolean makeFriends(int id, int friendId);

    boolean deleteFriend(int id, int friendId);

    Set<User> getFriends(int id);

    Set<User> getCommonFriends(int id, int otherId);
}
