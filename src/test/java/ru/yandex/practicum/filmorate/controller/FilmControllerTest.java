package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film validFilm;

    @BeforeEach
    void create() {
        validFilm = new Film();
        validFilm.setName("testName");
        validFilm.setDescription("testDescription");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 25));
        validFilm.setDuration(120L);
    }

    @Test
    void shouldCreateFilmWithValidData() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("testName")))
                .andExpect(jsonPath("$.description", is("testDescription")))
                .andExpect(jsonPath("$.releaseDate", is("2000-01-25")))
                .andExpect(jsonPath("$.duration", is(120)));
    }


    @Test
    void createFilmWithNullNameShouldReturnBadRequest() throws Exception {
        validFilm.setName(null);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithBlankNameShouldReturnBadRequest() throws Exception {
        validFilm.setName("   ");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithNullDescriptionShouldBeValid() throws Exception {
        validFilm.setDescription(null);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").isEmpty());
    }

    @Test
    void createFilmWith199LengthDescriptionShouldBeValid() throws Exception {
        String lengthDescription = "d".repeat(199);
        validFilm.setDescription(lengthDescription);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(lengthDescription)));
    }

    @Test
    void createFilmWith200LengthDescriptionShouldBeValid() throws Exception {
        String lengthDescription = "d".repeat(200);
        validFilm.setDescription(lengthDescription);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(lengthDescription)));
    }

    @Test
    void createFilmWith201LengthDescriptionShouldReturnBadRequest() throws Exception {
        String lengthDescription = "d".repeat(201);
        validFilm.setDescription(lengthDescription);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithReleaseDateBeforeMinShouldReturnBadRequest() throws Exception {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithMinReleaseDateShouldBeValid() throws Exception {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.releaseDate", is("1895-12-28")));
    }

    @Test
    void createFilmWithFutureReleaseDateShouldBeValid() throws Exception {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 29));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.releaseDate", is("1895-12-29")));
    }

    @Test
    void createFilmWithPositiveDurationShouldBeValid() throws Exception {
        validFilm.setDuration(1L);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration", is(1)));
    }

    @Test
    void createFilmWithZeroDurationShouldReturnBadRequest() throws Exception {
        validFilm.setDuration(0L);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmWithNegativeDurationShouldReturnBadRequest() throws Exception {
        validFilm.setDuration(-1L);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithValidDataShouldUpdateFilm() throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Film oldFilm = objectMapper.readValue(response, Film.class);

        Film updatedFilm = new Film();
        updatedFilm.setId(oldFilm.getId());
        updatedFilm.setName("updatedName");
        updatedFilm.setDescription("updatedDescription");
        updatedFilm.setReleaseDate(LocalDate.of(2020, 1, 18));
        updatedFilm.setDuration(150L);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(oldFilm.getId().intValue())))
                .andExpect(jsonPath("$.name", is("updatedName")))
                .andExpect(jsonPath("$.description", is("updatedDescription")))
                .andExpect(jsonPath("$.releaseDate", is("2020-01-18")))
                .andExpect(jsonPath("$.duration", is(150)));
    }

    @Test
    void updateFilmWithNonExistentIdShouldReturnNotFound() throws Exception {
        validFilm.setId(999L);
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isNotFound());

    }

    @Test
    void updateFilmWithNullIdShouldReturnBadRequest() throws Exception {
        validFilm.setId(null);
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canGetAllFilms() throws Exception {
        mockMvc.perform(delete("/films"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("testName")));
    }
}