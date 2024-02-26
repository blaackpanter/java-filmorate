package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.film.WrongFilmDateException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testAddFilm() throws Exception {
        mockMvc.perform(post("/films").content("{\"name\": \"Titanik\",\"description\": \"About ship and love\",\"releaseDate\": \"2004-01-18\",\"duration\": 90}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful());
    }

    @Test
    void testCheckDateBefore() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content("{\"name\": \"Titanik\",\"description\": \"About ship and love\",\"releaseDate\": \"1700-01-18\",\"duration\": 90}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof WrongFilmDateException));
    }

}
