package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.service.film.WrongFilmDateException;
import ru.yandex.practicum.filmorate.storage.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserNotFoundException;

import javax.validation.ValidationException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = {WrongFilmDateException.class, ValidationException.class})
    protected ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        log.error("Ошибка, что неправильный запрос.", ex);
        return ResponseEntity.badRequest().body(String.format("Неправильный запрос. Ошибка %s", ex.getMessage()));
    }

    @ExceptionHandler(value = {UserNotFoundException.class, FilmNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        log.error("Ошибка, что объект не найден.", ex);
        return ResponseEntity.badRequest().body(String.format("Ошибка, что объект не найден. Ошибка %s", ex.getMessage()));
    }


}
