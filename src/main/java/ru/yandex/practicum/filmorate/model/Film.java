package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validation.MinReleaseDate;

@Data
public class Film {

    private static final int MAX = 200;

    @NotNull(groups = Update.class, message = "ID не может быть пустым")
    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = Create.class)
    private String name;

    @Size(max = MAX, message = "Максимальная длина описания — 200 символов", groups = {Create.class, Update.class})
    private String description;

    @MinReleaseDate(message = "Минимальная дата выхода фильма - 28 декабря 1895 года",
            groups = {Create.class, Update.class})
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом",
            groups = {Create.class, Update.class})
    private Long duration;

    @JsonIgnore
    private Set<Long> likes = new HashSet<>();

    public interface Create {}

    public interface Update {}
}
