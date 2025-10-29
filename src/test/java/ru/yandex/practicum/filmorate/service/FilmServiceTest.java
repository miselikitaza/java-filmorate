package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private FilmService filmService;

    private Film validFilm;
    private User validUser;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setId(1L);
        validFilm.setName("Test Film");
        validFilm.setDescription("Test Description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120L);
        validFilm.setLikes(new HashSet<>());

        validUser = new User();
        validUser.setId(1L);
        validUser.setEmail("test@mail.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createFilmWithValidDataShouldCreateFilm() {
        when(filmStorage.createFilm(any(Film.class))).thenReturn(validFilm);

        Film result = filmService.createFilm(validFilm);

        assertNotNull(result);
        assertEquals(validFilm.getId(), result.getId());
        assertEquals(validFilm.getName(), result.getName());
        verify(filmStorage).createFilm(validFilm);
    }

    @Test
    void updateFilmWithValidDataShouldUpdateFilm() {
        Film updatedFilm = new Film();
        updatedFilm.setId(1L);
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(130L);

        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        when(filmStorage.updateFilm(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmService.updateFilm(updatedFilm);

        assertNotNull(result);
        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(filmStorage).updateFilm(any(Film.class));
    }

    @Test
    void updateFilmWithNonExistentIdShouldThrowException() {
        Film nonExistentFilm = new Film();
        nonExistentFilm.setId(999L);
        nonExistentFilm.setName("Non Existent");

        when(filmStorage.getFilmById(999L)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(nonExistentFilm)
        );

        assertEquals("Фильм с ID: 999 не найден", exception.getMessage());
        verify(filmStorage, never()).updateFilm(any(Film.class));
    }

    @Test
    void updateFilmWithoutIdShouldThrowException() {
        Film filmWithoutId = new Film();
        filmWithoutId.setName("No ID Film");

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> filmService.updateFilm(filmWithoutId)
        );

        assertEquals("ID не может быть пустым", exception.getMessage());
    }

    @Test
    void getFilmByIdWithExistingIdShouldReturnFilm() {
        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);

        Film result = filmService.getFilmById(1L);

        assertNotNull(result);
        assertEquals(validFilm.getId(), result.getId());
        verify(filmStorage).getFilmById(1L);
    }

    @Test
    void getFilmByIdWithNonExistentIdShouldThrowException() {
        when(filmStorage.getFilmById(999L)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(999L)
        );

        assertEquals("Фильм с ID: 999 не найден", exception.getMessage());
    }

    @Test
    void getAllFilmsShouldReturnAllFilms() {
        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("Film 2");

        List<Film> films = Arrays.asList(validFilm, film2);
        when(filmStorage.getAllFilms()).thenReturn(films);

        List<Film> result = filmService.getAllFilms();

        assertEquals(2, result.size());
        verify(filmStorage).getAllFilms();
    }

    @Test
    void deleteFilmByIdWithExistingIdShouldDeleteFilm() {
        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        doNothing().when(filmStorage).deleteFilmById(1L);
        filmService.deleteFilmById(1L);
        verify(filmStorage).deleteFilmById(1L);
    }

    @Test
    void deleteFilmByIdWithNonExistentIdShouldThrowException() {
        when(filmStorage.getFilmById(999L)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.deleteFilmById(999L)
        );

        assertEquals("Фильм с ID: 999 не найден", exception.getMessage());
        verify(filmStorage, never()).deleteFilmById(anyLong());
    }

    @Test
    void deleteAllFilmsShouldCallStorage() {
        doNothing().when(filmStorage).deleteAllFilms();
        filmService.deleteAllFilms();
        verify(filmStorage).deleteAllFilms();
    }

    @Test
    void likeWithValidDataShouldAddLike() {
        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(filmStorage.updateFilm(any(Film.class))).thenReturn(validFilm);

        filmService.like(1L, 1L);

        assertTrue(validFilm.getLikes().contains(1L));
        verify(filmStorage).updateFilm(validFilm);
    }

    @Test
    void likeWhenLikesIsNullShouldInitializeLikesAndAddLike() {
        validFilm.setLikes(null);

        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(filmStorage.updateFilm(any(Film.class))).thenReturn(validFilm);

        filmService.like(1L, 1L);

        assertNotNull(validFilm.getLikes());
        assertTrue(validFilm.getLikes().contains(1L));
        verify(filmStorage).updateFilm(validFilm);
    }

    @Test
    void likeWithDuplicateLikeShouldThrowException() {
        validFilm.getLikes().add(1L);

        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        when(userStorage.getUserById(1L)).thenReturn(validUser);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> filmService.like(1L, 1L)
        );

        assertEquals("Пользователь уже лайкнул этот фильм", exception.getMessage());
        verify(filmStorage, never()).updateFilm(any(Film.class));
    }

    @Test
    void deleteLikeWithExistingLikeShouldRemoveLike() {
        validFilm.getLikes().add(1L);

        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(filmStorage.updateFilm(any(Film.class))).thenReturn(validFilm);

        filmService.deleteLike(1L, 1L);

        assertFalse(validFilm.getLikes().contains(1L));
        verify(filmStorage).updateFilm(validFilm);
    }

    @Test
    void deleteLikeWithNonExistentLikeShouldThrowException() {
        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        when(userStorage.getUserById(1L)).thenReturn(validUser);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.deleteLike(1L, 1L)
        );

        assertEquals("Лайк от пользователя с ID: 1 не найден", exception.getMessage());
        verify(filmStorage, never()).updateFilm(any(Film.class));
    }

    @Test
    void deleteLikeWhenLikesIsNullShouldThrowException() {
        validFilm.setLikes(null);

        when(filmStorage.getFilmById(1L)).thenReturn(validFilm);
        when(userStorage.getUserById(1L)).thenReturn(validUser);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.deleteLike(1L, 1L)
        );

        assertEquals("Лайк от пользователя с ID: 1 не найден", exception.getMessage());
        verify(filmStorage, never()).updateFilm(any(Film.class));
    }

    @Test
    void getPopularFilmsShouldReturnSortedFilms() {
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("Film 1");
        film1.setLikes(new HashSet<>(Arrays.asList(1L, 2L)));

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("Film 2");
        film2.setLikes(new HashSet<>(Arrays.asList(1L)));

        Film film3 = new Film();
        film3.setId(3L);
        film3.setName("Film 3");
        film3.setLikes(new HashSet<>());

        List<Film> expectedFilms = Arrays.asList(film1, film2);
        when(filmStorage.getPopularFilms(2)).thenReturn(expectedFilms);

        List<Film> result = filmService.getPopularFilms(2);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(filmStorage).getPopularFilms(2);
    }

    @Test
    void getPopularFilmsWithEmptyStorageShouldReturnEmptyList() {
        when(filmStorage.getPopularFilms(10)).thenReturn(Collections.emptyList());
        List<Film> result = filmService.getPopularFilms(10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPopularFilmsWithNullLikesShouldHandleCorrectly() {
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("Film 1");
        film1.setLikes(null);

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("Film 2");
        film2.setLikes(new HashSet<>(Arrays.asList(1L)));

        List<Film> expectedFilms = Arrays.asList(film2, film1);
        when(filmStorage.getPopularFilms(2)).thenReturn(expectedFilms);

        List<Film> result = filmService.getPopularFilms(2);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        verify(filmStorage).getPopularFilms(2);
    }

    @Test
    void getPopularFilmsWithCountLargerThanListShouldReturnAllFilms() {
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("Film 1");
        film1.setLikes(new HashSet<>(Arrays.asList(1L)));

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("Film 2");
        film2.setLikes(new HashSet<>());

        List<Film> expectedFilms = Arrays.asList(film1, film2);
        when(filmStorage.getPopularFilms(10)).thenReturn(expectedFilms);

        List<Film> result = filmService.getPopularFilms(10);
        assertEquals(2, result.size());
        verify(filmStorage).getPopularFilms(10);
    }

    @Test
    void getPopularFilmsWithNullFilmListShouldReturnEmptyList() {
        when(filmStorage.getPopularFilms(10)).thenReturn(Collections.emptyList());
        List<Film> result = filmService.getPopularFilms(10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPopularFilmsWithSameLikesCountShouldSortById() {
        Film film1 = new Film();
        film1.setId(3L);
        film1.setName("Film 3");
        film1.setLikes(new HashSet<>(Arrays.asList(1L)));

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Film 1");
        film2.setLikes(new HashSet<>(Arrays.asList(2L)));

        Film film3 = new Film();
        film3.setId(2L);
        film3.setName("Film 2");
        film3.setLikes(new HashSet<>(Arrays.asList(3L)));

        List<Film> expectedFilms = Arrays.asList(film2, film3, film1); // Уже отсортированные
        when(filmStorage.getPopularFilms(3)).thenReturn(expectedFilms);

        List<Film> result = filmService.getPopularFilms(3);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
        verify(filmStorage).getPopularFilms(3);
    }
}
