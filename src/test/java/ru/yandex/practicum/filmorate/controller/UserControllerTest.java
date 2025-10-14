package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User validUser;

    @BeforeEach
    void create() {
        validUser = new User();
        validUser.setLogin("testLogin");
        validUser.setName("testName");
        validUser.setEmail("testEmail@mail.ru");
        validUser.setBirthday(LocalDate.of(2000, 3, 12));
    }

    @Test
    void shouldCreateUserWithValidData() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("testEmail@mail.ru")))
                .andExpect(jsonPath("$.login", is("testLogin")))
                .andExpect(jsonPath("$.name", is("testName")))
                .andExpect(jsonPath("$.birthday", is("2000-03-12")));
    }

    @Test
    void createUserWithBlankEmailShouldReturnBadRequest() throws Exception {
        validUser.setLogin("   ");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithNullEmailShouldReturnBadRequest() throws Exception {
        validUser.setEmail(null);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithEmailMissingAtSymbolShouldReturnBadRequest() throws Exception {
        validUser.setEmail("work.email.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithNullLoginShouldReturnBadRequest() throws Exception {
        validUser.setLogin(null);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithBlankLoginShouldReturnBadRequest() throws Exception {
        validUser.setLogin("   ");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithLoginContainingSpacesShouldReturnBadRequest() throws Exception {
        validUser.setLogin("login with spaces");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithNullNameShouldUseLoginAsName() throws Exception {
        validUser.setName(null);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testLogin")));
    }

    @Test
    void createUserWithBlankNameShouldUseLoginAsName() throws Exception {
        validUser.setName("   ");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testLogin")));
    }

    @Test
    void createUserWithFutureBirthdayShouldReturnBadRequest() throws Exception {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithTodayBirthdayShouldBeValid() throws Exception {
        validUser.setBirthday(LocalDate.now());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.birthday", is(LocalDate.now().toString())));
    }

    @Test
    void createUserWithPastBirthdayShouldBeValid() throws Exception {
        validUser.setBirthday(LocalDate.now().minusYears(30));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.birthday", notNullValue()));
    }

    @Test
    void shouldUpdateUserWithValidData() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        User oldUser = objectMapper.readValue(response, User.class);

        User updatedUser = new User();
        updatedUser.setId(oldUser.getId());
        updatedUser.setLogin("updatedLogin");
        updatedUser.setName("updatedName");
        updatedUser.setEmail("updatedEmail@mail.ru");

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(oldUser.getId().intValue())))
                .andExpect(jsonPath("$.login", is("updatedLogin")))
                .andExpect(jsonPath("$.name", is("updatedName")))
                .andExpect(jsonPath("$.email", is("updatedEmail@mail.ru")));
    }

    @Test
    void updateUserWithNonExistentIdShouldReturnNotFound() throws Exception {
        validUser.setId(999L);
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserWithNullIdShouldReturnBadRequest() throws Exception {
        validUser.setId(null);
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canGetAllUsers() throws Exception {
        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.login == 'testLogin')].login", hasItem("testLogin")));
    }
}