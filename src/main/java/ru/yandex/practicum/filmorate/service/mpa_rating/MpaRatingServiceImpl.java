package ru.yandex.practicum.filmorate.service.mpa_rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa_rating.MpaRatingStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaRatingServiceImpl implements MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    @Override
    public MpaRating get(int id) {
        return mpaRatingStorage.get(id);
    }

    @Override
    public List<MpaRating> getAll() {
        return mpaRatingStorage.getAll();
    }
}
