package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film validFilm;

    @BeforeEach
    void create() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setName("testName");
        validFilm.setDescription("testDescription");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 25));
        validFilm.setDuration(120L);
    }

    @Test
    void shouldCreateFilmWithValidData() {
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("testName", createdFilm.getName());
        assertEquals("testDescription", createdFilm.getDescription());
        assertEquals(LocalDate.of(2000, 1, 25), createdFilm.getReleaseDate());
        assertEquals(120L, createdFilm.getDuration());
    }

    @Test
    void createFilmWithNullNameShouldThrowException() {
        validFilm.setName(null);
        assertThrows(ConditionsNotMetException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void createFilmWithBlankNameShouldThrowException() {
        validFilm.setName("   ");
        assertThrows(ConditionsNotMetException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void createFilmWithNullDescriptionShouldBeValid() {
        validFilm.setDescription(null);
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm);
        assertNull(createdFilm.getDescription());
    }

    @Test
    void createFilmWith199LengthDescriptionShouldBeValid() {
        String maxLengthDescription = "d".repeat(199);
        validFilm.setDescription(maxLengthDescription);
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm);
        assertEquals(maxLengthDescription, createdFilm.getDescription());
    }

    @Test
    void createFilmWith200LengthDescriptionShouldBeValid() {
        String maxLengthDescription = "d".repeat(200);
        validFilm.setDescription(maxLengthDescription);
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm);
        assertEquals(maxLengthDescription, createdFilm.getDescription());
    }

    @Test
    void createFilmWith201LengthDescriptionShouldThrowException() {
        String longDescription = "d".repeat(201);
        validFilm.setDescription(longDescription);
        assertThrows(ConditionsNotMetException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void createFilmWithReleaseDateBeforeMinShouldThrowException() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ConditionsNotMetException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void createFilmWithMinReleaseDateShouldBeValid() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm);
        assertEquals(LocalDate.of(1895, 12, 28), createdFilm.getReleaseDate());
    }

    @Test
    void createFilmWithFutureReleaseDateShouldBeValid() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 29));
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm);
        assertEquals(LocalDate.of(1895, 12, 29), createdFilm.getReleaseDate());
    }

    @Test
    void createFilmWithPositiveDurationShouldBeValid() {
        validFilm.setDuration(1L);
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm);
        assertEquals(1L, createdFilm.getDuration());
    }

    @Test
    void createFilmWithZeroDurationShouldThrowException() {
        validFilm.setDuration(0L);
        assertThrows(ConditionsNotMetException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void createFilmWithNegativeDurationShouldThrowException() {
        validFilm.setDuration(-1L);
        assertThrows(ConditionsNotMetException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void updateFilmWithValidDataShouldUpdateFilm() {
        Film oldFilm = filmController.createFilm(validFilm);
        Film updatedFilm = new Film();
        updatedFilm.setId(oldFilm.getId());
        updatedFilm.setName("updatedName");
        updatedFilm.setDescription("updatedDescription");
        updatedFilm.setReleaseDate(LocalDate.of(2020, 1, 18));
        updatedFilm.setDuration(150L);

        Film result = filmController.updateFilm(updatedFilm);
        assertEquals("updatedName", result.getName());
        assertEquals("updatedDescription", result.getDescription());
        assertEquals(LocalDate.of(2020, 1, 18), result.getReleaseDate());
        assertEquals(150L, result.getDuration());
    }

    @Test
    void updateFilmWithNonExistentIdShouldThrowException() {
        validFilm.setId(999L);
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(validFilm));
    }

    @Test
    void updateFilmWithNullIdShouldThrowException() {
        validFilm.setId(null);
        assertThrows(ConditionsNotMetException.class, () -> filmController.updateFilm(validFilm));
    }

    @Test
    void canGetAllFilms() {
        filmController.createFilm(validFilm);
        assertEquals(1, filmController.getAllFilms().size());
        assertTrue(filmController.getAllFilms().contains(validFilm));
    }

}