package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание нового пользователя {}", user.getName());

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
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя без указания ID");
            throw new ConditionsNotMetException("ID не может быть пустым");
        }
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("Пользователь с ID: {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с ID: " + newUser.getId() + " не найден");
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Пользователь с iD: {} успешно обновлен", oldUser.getId());
        return oldUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей. Количество: {}", users.size());
        return users.values();
    }

    @DeleteMapping
    public void deleteAllUsers() {
        users.clear();
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
