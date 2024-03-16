package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final EventService eventService;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, EventService eventService) {
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    @Override
    public User add(User user) {
        checkName(user);
        return userStorage.add(user);
    }

    @Override
    public User getUser(int id) {
        return userStorage.get(id);
    }

    @Override
    public boolean userExist(int id) {
        return userStorage.userExist(id);
    }

    @Override
    public User update(User user) {
        if (getAllUsers().isEmpty()) {
            throw new NotFoundException("Пользователей не найдено, чтобы обновить сначала необходимо добавить пользователя");
        }
        checkName(user);
        return userStorage.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public boolean makeFriends(int id, int friendId) {
        final User user = userStorage.get(id);
        if (user.getFriends().contains(friendId)) {
            return false;
        }
        final User friend = userStorage.get(friendId);
        userStorage.update(addFriend(user, friend.getId()));
        return true;
    }

    private User addFriend(User user, int friendId) {
        final HashSet<Integer> friendIds = new HashSet<>(user.getFriends());
        friendIds.add(friendId);
        user.setFriends(Set.copyOf(friendIds));
        eventService.createAddFriendEvent(user.getId(), friendId);
        return user;
    }

    @Override
    public boolean deleteFriend(int id, int friendId) {
        final User user = userStorage.get(id);
        if (!user.getFriends().contains(friendId)) {
            return false;
        }
        final User friend = userStorage.get(friendId);
        userStorage.update(deleteFriend(user, friend.getId()));
        eventService.createRemoveFriendEvent(id, friendId);
        return true;
    }

    @Override
    public Set<User> getFriends(int id) {
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
    public Set<User> getCommonFriends(int id, int otherId) {
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

    @Override
    public boolean deleteUser(int id) {
        return userStorage.deleteUser(id);
    }

    public User getMatchedUser(List<Film> films, User user) {
        final int userId = user.getId();

        Map<Integer, Integer> countUsersLike = new HashMap<>();

        // проходим по фильмам, чтобы получить Map'у с ID пользователей и количеству лайков
        films.stream()
                // оставляем только фильмы, которым поставил лайк наш юзер
                .filter(film -> film.getLikeUserIds().contains(userId))
                // создаём стрим с id юзеров которые поставили лайк этим фильмам
                .flatMap(film -> film.getLikeUserIds().stream())
                // из полученного стрима удаляем id нашего юзера
                .filter(likeUserId -> likeUserId != userId)
                // наполняем Map'у ID юзеров
                .forEach(reqUserId -> countUsersLike
                        // и каждому проставляем количество вхождений в тот список выше
                        .compute(reqUserId, (id, count) -> (count == null) ? 0 : count + 1));

        Map.Entry<Integer, Integer> getUserMaxValue = countUsersLike.entrySet().stream()
                .max((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .orElse(null);

        if (getUserMaxValue == null)
            return null;
        else
            return getUser(getUserMaxValue.getKey());
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Заменили пустое имя на логин");
        }
    }
}
