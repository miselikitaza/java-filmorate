package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public User createUser(User user);

    public User updateUser(User newUser);

    public User getUserById(Long id);

    public List<User> getAllUsers();

    public void deleteUserById(Long id);

    public void deleteAllUsers();
}
