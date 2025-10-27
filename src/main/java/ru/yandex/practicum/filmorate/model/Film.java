package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.*;

@Data
public class Film {

    private static final int MAX = 200;

    @NotNull(groups = Update.class, message = "ID не может быть пустым")
    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = Create.class)
    private String name;

    @Size(max = MAX, message = "Максимальная длина описания — 200 символов", groups = {Create.class, Update.class})
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом",
            groups = {Create.class, Update.class})
    private Long duration;

    private Set<Long> likes = new HashSet<>();

    public interface Create {}

    public interface Update {}
}
