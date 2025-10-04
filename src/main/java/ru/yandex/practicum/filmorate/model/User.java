package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class User {

    private Long id;

    @NotBlank(message = "Логин не может быть пустым", groups = Create.class)
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы", groups = {Create.class, Update.class})
    private String login;

    private String name;

    @NotBlank(message = "Электронная почта не может быть пустой", groups = Create.class)
    @Email(message = "Почта должна содержать символ @", groups = {Create.class, Update.class})
    private String email;

    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = {Create.class, Update.class})
    private LocalDate birthday;

    public interface Create {}
    public interface Update {}
}
