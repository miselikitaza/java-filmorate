package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    private Film validFilm;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setId(1L);
        validFilm.setName("testName");
        validFilm.setDescription("testDescription");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 25));
        validFilm.setDuration(120L);
    }

    @Test
    void createFilmWithValidDataShouldReturn200() throws Exception {
        when(filmService.createFilm(any(Film.class))).thenReturn(validFilm);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("testName")))
                .andExpect(jsonPath("$.description", is("testDescription")));

        verify(filmService).createFilm(any(Film.class));
    }

    @Test
    void createFilmWithNullNameShouldReturn400() throws Exception {
        validFilm.setName(null);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).createFilm(any(Film.class));
    }

    @Test
    void createFilmWithBlankNameShouldReturn400() throws Exception {
        validFilm.setName("   ");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).createFilm(any(Film.class));
    }

    @Test
    void createFilmWithLongDescriptionShouldReturn400() throws Exception {
        String longDescription = "d".repeat(201);
        validFilm.setDescription(longDescription);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).createFilm(any(Film.class));
    }

    @Test
    void createFilmWithEarlyReleaseDateShouldReturn400() throws Exception {
        Film filmWithEarlyDate = new Film();
        filmWithEarlyDate.setName("testName");
        filmWithEarlyDate.setDescription("testDescription");
        filmWithEarlyDate.setReleaseDate(LocalDate.of(1895, 12, 27));
        filmWithEarlyDate.setDuration(120L);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmWithEarlyDate)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void createFilmWithNegativeDurationShouldReturn400() throws Exception {
        validFilm.setDuration(-1L);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).createFilm(any(Film.class));
    }

    @Test
    void updateFilmWithValidDataShouldReturn200() throws Exception {
        Film updatedFilm = new Film();
        updatedFilm.setId(1L);
        updatedFilm.setName("updatedName");
        updatedFilm.setDescription("updatedDescription");
        updatedFilm.setReleaseDate(LocalDate.of(2020, 1, 18));
        updatedFilm.setDuration(150L);

        when(filmService.updateFilm(any(Film.class))).thenReturn(updatedFilm);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("updatedName")))
                .andExpect(jsonPath("$.duration", is(150)));

        verify(filmService).updateFilm(any(Film.class));
    }

    @Test
    void updateFilmWithNonExistentIdShouldReturn404() throws Exception {
        validFilm.setId(999L);

        when(filmService.updateFilm(any(Film.class)))
                .thenThrow(new NotFoundException("Фильм не найден"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isNotFound());

        verify(filmService).updateFilm(any(Film.class));
    }

    @Test
    void updateFilmWithNullIdShouldReturn400() throws Exception {
        validFilm.setId(null);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).updateFilm(any(Film.class));
    }

    @Test
    void getAllFilmsShouldReturn200() throws Exception {
        List<Film> films = Arrays.asList(validFilm);
        when(filmService.getAllFilms()).thenReturn(films);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("testName")));

        verify(filmService).getAllFilms();
    }

    @Test
    void getFilmByIdWithExistingIdShouldReturn200() throws Exception {
        when(filmService.getFilmById(1L)).thenReturn(validFilm);

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testName")));

        verify(filmService).getFilmById(1L);
    }

    @Test
    void getFilmByIdWithNonExistentIdShouldReturn404() throws Exception {
        when(filmService.getFilmById(999L))
                .thenThrow(new NotFoundException("Фильм не найден"));

        mockMvc.perform(get("/films/999"))
                .andExpect(status().isNotFound());

        verify(filmService).getFilmById(999L);
    }

    @Test
    void likeFilmShouldReturn200() throws Exception {
        doNothing().when(filmService).like(1L, 1L);

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        verify(filmService).like(1L, 1L);
    }

    @Test
    void deleteLikeShouldReturn200() throws Exception {
        doNothing().when(filmService).deleteLike(1L, 1L);

        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());

        verify(filmService).deleteLike(1L, 1L);
    }

    @Test
    void getPopularFilmsShouldReturn200() throws Exception {
        List<Film> films = Arrays.asList(validFilm);
        when(filmService.getPopularFilms(10)).thenReturn(films);

        mockMvc.perform(get("/films/popular?count=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(filmService).getPopularFilms(10);
    }
}