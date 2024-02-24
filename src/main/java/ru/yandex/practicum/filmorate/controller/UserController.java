package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();

    @PostMapping
    public void createUser(@Valid @RequestBody User user) {
        log.info("Создаем нового юзера");
        checkName(user);
        int id = users.size() + 1;
        user.setId(id);
        users.put(id, user);
        log.info("Новый юзер успешно создан");
    }

    @PutMapping
    public void updateUser(@Valid @RequestBody User user) {
        log.info("Обновим информацию о юзере");
        if (getAllUsersList().isEmpty()) {
            log.error("Не удалось обновить обновить информацию о юзере. Список юзеров пустой.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователей не найдено, чтобы обновить сначала необходимо добавить пользователя");
        }
        if (!users.containsKey(user.getId())) {
            log.error("Не удалось обновить обновить информацию о юзере. Юзера нет в списке.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдено пользователя с id = %s".formatted(user.getId()));
        }
        checkName(user);
        users.put(user.getId(), user);
        log.info("Информация о юзере успешно обновлена");
    }

    @GetMapping
    public List getAllUsersList() {
        log.info("Возвращаем список юзеров");
        return new ArrayList<>(users.values());
    }

    private void checkName(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Заменили пустое имя на логин");
        }
    }
}
