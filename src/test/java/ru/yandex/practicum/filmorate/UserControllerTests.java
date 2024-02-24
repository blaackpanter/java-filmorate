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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testCreateUser() throws Exception {
        mockMvc.perform(post("/users").content("""
                {
                    "login":"ta",
                    "name":"ivan",
                    "email":"lo@gmai.ru",
                    "birthday":"2008-02-02"	
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful());
    }

    @Test
    void testCheckEmptyLogin() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("""
                                        {
                                            "login":"   ",
                                            "name":"ivan",
                                            "email":"lo@gmai.ru",
                                            "birthday":"2008-02-02"	
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}
