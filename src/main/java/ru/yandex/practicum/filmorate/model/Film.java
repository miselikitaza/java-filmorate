package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

/**
 * Film.
 */
@Data
public class Film {

    private static final int MAX = 200;

    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = Create.class)
    private String name;

    @Size(max = MAX, message = "Максимальная длина описания — 200 символов", groups = {Create.class, Update.class})
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом",
            groups = {Create.class, Update.class})
    private Long duration;

    public interface Create {}
    public interface Update {}
}
