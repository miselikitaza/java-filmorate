package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        log.info("Попытка создания пользователя");
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("В качестве имени будет установлен логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        User createdUser = userStorage.createUser(user);
        log.info("Пользователь {} успешно содан", user.getName());
        return createdUser;
    }

    public User updateUser(User newUser) {
        log.info("Попытка обновления пользователя");
        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя без указания ID");
            throw new ConditionsNotMetException("ID не может быть пустым");
        }
        User oldUser = userStorage.getUserById(newUser.getId());
        if (oldUser == null) {
            log.warn("Пользователь с ID: {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с ID: " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            if (userStorage.isEmailExists(newUser.getEmail())) {
                log.warn("Пользователь с email {} уже существует", newUser.getEmail());
                throw new ConditionsNotMetException("Пользователь с email " + newUser.getEmail() + " уже существует");
            }
        }

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
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
        User updatedUser = userStorage.updateUser(oldUser);
        log.info("Пользователь {} успешно обновлен", newUser.getName());
        return updatedUser;
    }

    public User getUserById(Long id) {
        log.info("Получение пользователя с ID: {}", id);
        return validateUser(id);
    }

    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userStorage.getAllUsers();
    }

    public void deleteUserById(Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        User user = validateUser(id);
        userStorage.deleteUserById(user.getId());
    }

    public void deleteAllUsers() {
        log.info("Удаление всех пользователей");
        userStorage.deleteAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Пользователь с ID {} добавляет в друзья пользователя с ID {}", userId, friendId);
        User user = validateUser(userId);
        User friend = validateUser(friendId);

        if (userId.equals(friendId)) {
            log.warn("Пользователь с ID {} пытается добавить в друзья самого себя", userId);
            throw new ConditionsNotMetException("Нельзя добавить в друзья самого себя");
        }

        if (user.getFriendIds() == null) {
            user.setFriendIds(new HashSet<>());
        }
        if (friend.getFriendIds() == null) {
            friend.setFriendIds(new HashSet<>());
        }

        if (user.getFriendIds().contains(friendId)) {
            log.warn("Пользователи {} и {} уже друзья", userId, friendId);
            throw new ConditionsNotMetException("Пользователи уже друзья");
        }

        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Пользователь с ID: {} удаляет из друзей пользователя с ID: {}", userId, friendId);

        User user = validateUser(userId);
        User friend = validateUser(friendId);

        if (user.getFriendIds() != null) {
            user.getFriendIds().remove(friendId);
        }

        if (friend.getFriendIds() != null) {
            friend.getFriendIds().remove(userId);
        }

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.debug("Пользователь с ID {} успешно удалил из друзей пользователя с ID {}", userId, friendId);
    }

    public List<User> getFriends(Long id) {
        log.info("Получение друзей пользователя с ID: {}", id);
        validateUser(id);
        List<User> friends = userStorage.getFriends(id);
        log.info("Найдено {} друзей", friends.size());
        return friends;
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        log.debug("Поиск общих друзей между пользователями с ID {} и {}", userId, friendId);
        validateUser(userId);
        validateUser(friendId);
        List<User> mutualFriends = userStorage.getMutualFriends(userId, friendId);
        log.debug("Найдено {} общих друзей", mutualFriends.size());
        return mutualFriends;
    }

    private User validateUser(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        return user;
    }
}