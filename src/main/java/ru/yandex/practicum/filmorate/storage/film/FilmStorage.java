package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public Film getFilmById(Long id);

    public List<Film> getAllFilms();

    public void deleteFilmById(Long id);

    public void deleteAllFilms();
}