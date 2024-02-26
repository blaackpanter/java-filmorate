package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создаем нового юзера.");
        final User add = userService.add(user);
        log.info("Новый юзер успешно создан");
        return add;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws UserNotFoundException {
        log.info("Обновим информацию о юзере");
        final User update = userService.update(user);
        log.info("Информация о юзере успешно обновлена");
        return update;
    }

    @GetMapping
    public List<User> getAllUsersList() {
        log.info("Возвращаем список юзеров");
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public boolean addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) throws UserNotFoundException {
        return userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) throws UserNotFoundException {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") int id) throws UserNotFoundException {
        return userService.getFriends(id)
                .stream()
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) throws UserNotFoundException {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") int id) throws UserNotFoundException {
        return userService.getUser(id);
    }
}
