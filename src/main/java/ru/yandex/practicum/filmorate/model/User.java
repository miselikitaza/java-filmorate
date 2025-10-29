package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    @NotNull(message = "ID не может быть пустым", groups = Update.class)
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

    @JsonIgnore
    private Set<Long> friendIds = new HashSet<>();

    public interface Create {}

    public interface Update {}
}
