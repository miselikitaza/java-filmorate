package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        log.info("Получен запрос на создание фильма {}", film.getName());
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Фильм {} успешно создан", film.getName());
        return createdFilm;
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Попытка обновления фильма без указания ID");
            throw new ConditionsNotMetException("ID не может быть пустым");
        }

        Film oldFilm = filmStorage.getFilmById(newFilm.getId());
        if (oldFilm == null) {
            log.warn("Фильм с ID: {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с ID: " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        Film updatedFilm = filmStorage.updateFilm(oldFilm);
        log.info("Фильм с ID: {} успешно обновлен", newFilm.getId());
        return updatedFilm;
    }

    public Film getFilmById(Long id) {
        log.info("Получение фильма с ID: {}", id);
        return getFilmOrThrown(id);
    }

    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.getAllFilms();
    }

    public void deleteFilmById(Long id) {
        log.info("Удаление фильма с ID: {}", id);
        Film film = getFilmOrThrown(id);
        filmStorage.deleteFilmById(film.getId());
    }

    public void deleteAllFilms() {
        log.info("Удаление всех фильмов");
        filmStorage.deleteAllFilms();
    }

    public void like(Long filmId, Long userId) {
        Film film = getFilmOrThrown(filmId);
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Фильм с ID: " + userId + " не найден");
        }

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        Set<Long> newLikes = new HashSet<>(film.getLikes());

        if (!newLikes.add(userId)) {
            throw new ConditionsNotMetException("Пользователь уже лайкнул этот фильм");
        }

        film.setLikes(newLikes);
        filmStorage.updateFilm(film);
        log.info("Фильм с ID: {} успешно оценен пользователем с ID: {}", filmId, userId);
    }


    public void deleteLike(Long filmId, Long userId) {
        Film film = getFilmOrThrown(filmId);
        userStorage.getUserById(userId);

        if (film.getLikes() == null || !film.getLikes().contains(userId)) {
            log.warn("Пользователь с ID: {} не лайкал фильм с ID: {}", userId, filmId);
            throw new NotFoundException("Лайк от пользователя с ID: " + userId + " не найден");
        }

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Лайк пользователя с ID: {} удален с фильма с ID: {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение топ-{} популярных фильмов", count);
        if (count <= 0) {
            throw new ConditionsNotMetException("Количество фильмов должно быть положительным числом");
        }
        return filmStorage.getPopularFilms(count);
    }

    private Film getFilmOrThrown(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID: " + filmId + " не найден");
        }
        return film;
    }
}
