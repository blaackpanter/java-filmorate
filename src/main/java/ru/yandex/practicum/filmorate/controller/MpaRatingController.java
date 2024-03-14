package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.mpa_rating.MpaRatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingService mpaRatingService;

    @GetMapping("/mpa")
    public List<MpaRating> getAllMpa() {
        return mpaRatingService.getAll();
    }

    @GetMapping("mpa/{id}")
    public MpaRating getMpaId(@PathVariable("id") int id) {
        return mpaRatingService.get(id);
    }
}
