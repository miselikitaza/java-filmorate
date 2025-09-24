package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final HashMap<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен запрос на создание нового пользователя {}", user.getName());
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("В качестве имени будет установлен логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно создан", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя без указания ID");
            throw new ConditionsNotMetException("ID не может быть пустым");
        }
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("Пользователь с ID: {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id: " + newUser.getId() + " не найден");
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }
        log.info("Пользователь с iD: {} успешно обновлен", oldUser.getId());
        return oldUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private void validateUser(User user) {
        log.debug("Проверка пользователя {}", user.getName());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Данные почты не заполнены");
            throw new ConditionsNotMetException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("В почте отсутствует знак @");
            throw new ConditionsNotMetException("Почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Данные логина не заполнены");
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Логин содержит пробелы");
            throw new ConditionsNotMetException("логин не должен содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Указана невозможная дата рождения");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
        log.debug("Проверка пользователя {} прошла успешно", user.getName());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
