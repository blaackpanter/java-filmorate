package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User add(User user) {
        checkName(user);
        return userStorage.add(user);
    }

    @Override
    public User getUser(int id) throws UserNotFoundException {
        return userStorage.get(id);
    }

    @Override
    public boolean userExist(int id) {
        return userStorage.userExist(id);
    }

    @Override
    public User update(User user) throws UserNotFoundException {
        if (getAllUsers().isEmpty()) {
            throw new UserNotFoundException("Пользователей не найдено, чтобы обновить сначала необходимо добавить пользователя");
        }
        checkName(user);
        return userStorage.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public boolean makeFriends(int id, int friendId) throws UserNotFoundException {
        final User user = userStorage.get(id);
        if (user.getFriends().contains(friendId)) {
            return false;
        }
        final User friend = userStorage.get(friendId);
        userStorage.update(addFriend(user, friend.getId()));
        userStorage.update(addFriend(friend, user.getId()));
        return true;
    }

    private User addFriend(User user, int friendId) {
        final HashSet<Integer> friendIds = new HashSet<>(user.getFriends());
        friendIds.add(friendId);
        user.setFriends(Set.copyOf(friendIds));
        return user;
    }

    @Override
    public boolean deleteFriend(int id, int friendId) throws UserNotFoundException {
        final User user = userStorage.get(id);
        if (!user.getFriends().contains(friendId)) {
            return false;
        }
        final User friend = userStorage.get(friendId);
        userStorage.update(deleteFriend(user, friend.getId()));
        userStorage.update(deleteFriend(friend, user.getId()));
        return true;
    }

    @Override
    public Set<User> getFriends(int id) throws UserNotFoundException {
        return Set.copyOf(
                userStorage.get(
                        userStorage.get(id).getFriends()
                )
        );
    }

    private User deleteFriend(User user, int friendId) {
        final HashSet<Integer> friendIds = new HashSet<>(user.getFriends());
        friendIds.remove(friendId);
        user.setFriends(Set.copyOf(friendIds));
        return user;
    }

    @Override
    public Set<User> getCommonFriends(int id, int otherId) throws UserNotFoundException {
        final User user = userStorage.get(id);
        if (user.getFriends().isEmpty()) {
            return Collections.emptySet();
        }
        final User other = userStorage.get(otherId);
        if (other.getFriends().isEmpty()) {
            return Collections.emptySet();
        }
        final Set<Integer> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(other.getFriends());
        return Set.copyOf(userStorage.get(commonIds));
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Заменили пустое имя на логин");
        }
    }
}
