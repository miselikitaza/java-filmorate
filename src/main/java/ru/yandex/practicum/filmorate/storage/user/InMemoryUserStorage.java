package ru.yandex.practicum.filmorate.storage.user;

import lombok.Generated;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    @Generated
    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> userEmails = new HashSet<>();

    @Override
    public User createUser(User user) {
        if (isEmailExists(user.getEmail())) {
            return null;
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        userEmails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        User existingUser = users.get(user.getId());
        if (!existingUser.getEmail().equals(user.getEmail())) {
            userEmails.remove(existingUser.getEmail());
            userEmails.add(user.getEmail());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long id) {
        User user = users.get(id);
        userEmails.remove(user.getEmail());
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    public List<User> getFriends(Long id) {
        User user = getUserById(id);
        if (user.getFriendIds() == null) {
            return Collections.emptyList();
        }
        return user.getFriendIds().stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user == null || friend == null) {
            return Collections.emptyList();
        }

        Set<Long> friends1 = user.getFriendIds() != null ? user.getFriendIds() : Collections.emptySet();
        Set<Long> friends2 = friend.getFriendIds() != null ? friend.getFriendIds() : Collections.emptySet();
        Set<Long> mutualFriendIds = new HashSet<>(friends1);
        mutualFriendIds.retainAll(friends2);

        return mutualFriendIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmailExists(String email) {
        return userEmails.contains(email);
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
