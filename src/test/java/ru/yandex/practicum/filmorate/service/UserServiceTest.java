package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    private User validUser;
    private User friendUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(1L);
        validUser.setEmail("test@mail.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
        validUser.setFriendIds(new HashSet<>());

        friendUser = new User();
        friendUser.setId(2L);
        friendUser.setEmail("friend@mail.com");
        friendUser.setLogin("friendlogin");
        friendUser.setName("Friend User");
        friendUser.setBirthday(LocalDate.of(1992, 1, 1));
        friendUser.setFriendIds(new HashSet<>());
    }

    @Test
    void createUserWithValidDataShouldCreateUser() {
        when(userStorage.createUser(any(User.class))).thenReturn(validUser);

        User result = userService.createUser(validUser);

        assertNotNull(result);
        assertEquals(validUser.getId(), result.getId());
        assertEquals(validUser.getName(), result.getName());
        verify(userStorage).createUser(validUser);
    }

    @Test
    void createUserWithNullNameShouldSetLoginAsName() {
        User userWithNullName = new User();
        userWithNullName.setEmail("test@mail.com");
        userWithNullName.setLogin("testlogin");
        userWithNullName.setBirthday(LocalDate.of(1990, 1, 1));

        when(userStorage.createUser(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        User result = userService.createUser(userWithNullName);

        assertNotNull(result);
        assertEquals("testlogin", result.getName());
        verify(userStorage).createUser(any(User.class));
    }

    @Test
    void createUserWithBlankNameShouldSetLoginAsName() {
        User userWithBlankName = new User();
        userWithBlankName.setEmail("test@mail.com");
        userWithBlankName.setLogin("testlogin");
        userWithBlankName.setName("   ");
        userWithBlankName.setBirthday(LocalDate.of(1990, 1, 1));

        when(userStorage.createUser(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        User result = userService.createUser(userWithBlankName);

        assertNotNull(result);
        assertEquals("testlogin", result.getName());
        verify(userStorage).createUser(any(User.class));
    }

    @Test
    void updateUserWithValidDataShouldUpdateUser() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("updated@mail.com");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1995, 1, 1));

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.updateUser(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(updatedUser);

        assertNotNull(result);
        assertEquals("updated@mail.com", result.getEmail());
        assertEquals("updatedlogin", result.getLogin());
        assertEquals("Updated User", result.getName());
        verify(userStorage).updateUser(any(User.class));
    }

    @Test
    void updateUserWithNonExistentIdShouldThrowException() {
        User nonExistentUser = new User();
        nonExistentUser.setId(999L);
        nonExistentUser.setEmail("test@mail.com");

        when(userStorage.getUserById(999L)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(nonExistentUser)
        );

        assertEquals("Пользователь с ID: 999 не найден", exception.getMessage());
        verify(userStorage, never()).updateUser(any(User.class));
    }

    @Test
    void updateUserWithoutIdShouldThrowException() {
        User userWithoutId = new User();
        userWithoutId.setEmail("test@mail.com");
        userWithoutId.setLogin("testlogin");

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> userService.updateUser(userWithoutId)
        );

        assertEquals("ID не может быть пустым", exception.getMessage());
        verify(userStorage, never()).updateUser(any(User.class));
    }

    @Test
    void updateUserWithPartialDataShouldUpdateOnlyProvidedFields() {
        User partialUpdate = new User();
        partialUpdate.setId(1L);
        partialUpdate.setName("Updated Name Only");

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.updateUser(any(User.class))).thenReturn(validUser);

        User result = userService.updateUser(partialUpdate);

        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
        assertEquals("testlogin", result.getLogin());
        verify(userStorage).updateUser(any(User.class));
    }

    @Test
    void getUserByIdWithExistingIdShouldReturnUser() {
        when(userStorage.getUserById(1L)).thenReturn(validUser);

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(validUser.getId(), result.getId());
        verify(userStorage).getUserById(1L);
    }

    @Test
    void getUserByIdWithNonExistentIdShouldThrowException() {
        when(userStorage.getUserById(999L)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertEquals("Пользователь с ID: 999 не найден", exception.getMessage());
    }

    @Test
    void getAllUsersShouldReturnAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@mail.com");

        List<User> users = Arrays.asList(validUser, user2);
        when(userStorage.getAllUsers()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userStorage).getAllUsers();
    }

    @Test
    void deleteUserByIdWithExistingIdShouldDeleteUser() {
        when(userStorage.getUserById(1L)).thenReturn(validUser);
        doNothing().when(userStorage).deleteUserById(1L);

        userService.deleteUserById(1L);

        verify(userStorage).deleteUserById(1L);
    }

    @Test
    void deleteUserByIdWithNonExistentIdShouldThrowException() {
        when(userStorage.getUserById(999L)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUserById(999L)
        );

        assertEquals("Пользователь с ID: 999 не найден", exception.getMessage());
        verify(userStorage, never()).deleteUserById(anyLong());
    }

    @Test
    void deleteAllUsersShouldCallStorage() {
        doNothing().when(userStorage).deleteAllUsers();
        userService.deleteAllUsers();
        verify(userStorage).deleteAllUsers();
    }

    @Test
    void addFriendWithValidDataShouldAddFriend() {
        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);
        when(userStorage.updateUser(any(User.class))).thenReturn(validUser);

        userService.addFriend(1L, 2L);

        assertTrue(validUser.getFriendIds().contains(2L));
        assertTrue(friendUser.getFriendIds().contains(1L));
        verify(userStorage, times(2)).updateUser(any(User.class));
    }

    @Test
    void addFriendWhenAddingSelfShouldThrowException() {
        when(userStorage.getUserById(1L)).thenReturn(validUser);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> userService.addFriend(1L, 1L)
        );

        assertEquals("Нельзя добавить в друзья самого себя", exception.getMessage());
        verify(userStorage, never()).updateUser(any(User.class));
    }

    @Test
    void addFriendWithDuplicateFriendShouldThrowException() {
        validUser.getFriendIds().add(2L); // Уже друзья

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> userService.addFriend(1L, 2L)
        );

        assertEquals("Пользователи уже друзья", exception.getMessage());
        verify(userStorage, never()).updateUser(any(User.class));
    }

    @Test
    void addFriendWhenFriendIdsIsNullShouldInitializeAndAddFriend() {
        validUser.setFriendIds(null);
        friendUser.setFriendIds(null);

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);
        when(userStorage.updateUser(any(User.class))).thenReturn(validUser);

        userService.addFriend(1L, 2L);

        assertNotNull(validUser.getFriendIds());
        assertNotNull(friendUser.getFriendIds());
        assertTrue(validUser.getFriendIds().contains(2L));
        assertTrue(friendUser.getFriendIds().contains(1L));
        verify(userStorage, times(2)).updateUser(any(User.class));
    }

    @Test
    void deleteFriendWithExistingFriendShouldRemoveFriend() {
        validUser.getFriendIds().add(2L);
        friendUser.getFriendIds().add(1L);

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);
        when(userStorage.updateUser(any(User.class))).thenReturn(validUser);

        userService.deleteFriend(1L, 2L);

        assertFalse(validUser.getFriendIds().contains(2L));
        assertFalse(friendUser.getFriendIds().contains(1L));
        verify(userStorage, times(2)).updateUser(any(User.class));
    }

    @Test
    void deleteFriendWhenFriendIdsIsNullShouldHandleGracefully() {
        validUser.setFriendIds(null);
        friendUser.setFriendIds(null);

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);
        when(userStorage.updateUser(any(User.class))).thenReturn(validUser);

        userService.deleteFriend(1L, 2L);
        verify(userStorage, times(2)).updateUser(any(User.class));
    }

    @Test
    void deleteFriendWithNonExistentFriendShouldRemoveNothing() {
        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);
        when(userStorage.updateUser(any(User.class))).thenReturn(validUser);

        userService.deleteFriend(1L, 2L);

        verify(userStorage, times(2)).updateUser(any(User.class));
    }

    @Test
    void getFriendsShouldReturnFriendsList() {
        User friend1 = new User();
        friend1.setId(2L);
        friend1.setEmail("friend1@mail.com");

        User friend2 = new User();
        friend2.setId(3L);
        friend2.setEmail("friend2@mail.com");

        validUser.getFriendIds().addAll(Arrays.asList(2L, 3L));

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friend1);
        when(userStorage.getUserById(3L)).thenReturn(friend2);

        List<User> friends = userService.getFriends(1L);

        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(f -> f.getId().equals(2L)));
        assertTrue(friends.stream().anyMatch(f -> f.getId().equals(3L)));
    }

    @Test
    void getFriendsWhenNoFriendsShouldReturnEmptyList() {
        when(userStorage.getUserById(1L)).thenReturn(validUser);
        List<User> friends = userService.getFriends(1L);
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriendsWhenFriendIdsIsNullShouldReturnEmptyList() {
        validUser.setFriendIds(null);
        when(userStorage.getUserById(1L)).thenReturn(validUser);

        List<User> friends = userService.getFriends(1L);
        assertTrue(friends.isEmpty());
    }

    @Test
    void getFriendsWithNonExistentFriendIdShouldFilterOutNull() {
        validUser.getFriendIds().addAll(Arrays.asList(2L, 999L));

        User friend = new User();
        friend.setId(2L);
        friend.setEmail("friend@mail.com");

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friend);
        when(userStorage.getUserById(999L)).thenReturn(null);

        List<User> friends = userService.getFriends(1L);

        assertEquals(1, friends.size());
        assertEquals(2L, friends.get(0).getId());
    }

    @Test
    void getMutualFriendsShouldReturnCommonFriends() {
        User mutualFriend1 = new User();
        mutualFriend1.setId(3L);
        mutualFriend1.setEmail("mutual1@mail.com");

        User mutualFriend2 = new User();
        mutualFriend2.setId(4L);
        mutualFriend2.setEmail("mutual2@mail.com");

        validUser.getFriendIds().addAll(Arrays.asList(3L, 4L));
        friendUser.getFriendIds().addAll(Arrays.asList(3L, 4L, 5L));

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);
        when(userStorage.getUserById(3L)).thenReturn(mutualFriend1);
        when(userStorage.getUserById(4L)).thenReturn(mutualFriend2);

        List<User> mutualFriends = userService.getMutualFriends(1L, 2L);

        assertEquals(2, mutualFriends.size());
        assertTrue(mutualFriends.stream().anyMatch(f -> f.getId().equals(3L)));
        assertTrue(mutualFriends.stream().anyMatch(f -> f.getId().equals(4L)));
    }

    @Test
    void getMutualFriendsWhenNoCommonFriendsShouldReturnEmptyList() {
        validUser.getFriendIds().add(3L);
        friendUser.getFriendIds().add(4L);

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);

        List<User> mutualFriends = userService.getMutualFriends(1L, 2L);

        assertTrue(mutualFriends.isEmpty());
    }

    @Test
    void getMutualFriendsWhenFriendIdsIsNullShouldReturnEmptyList() {
        validUser.setFriendIds(null);
        friendUser.setFriendIds(null);

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);

        List<User> mutualFriends = userService.getMutualFriends(1L, 2L);

        assertTrue(mutualFriends.isEmpty());
    }

    @Test
    void getMutualFriendsWithNonExistentMutualFriendShouldFilterOutNull() {
        validUser.getFriendIds().addAll(Arrays.asList(3L, 999L));
        friendUser.getFriendIds().addAll(Arrays.asList(3L, 999L));

        User mutualFriend = new User();
        mutualFriend.setId(3L);
        mutualFriend.setEmail("mutual@mail.com");

        when(userStorage.getUserById(1L)).thenReturn(validUser);
        when(userStorage.getUserById(2L)).thenReturn(friendUser);
        when(userStorage.getUserById(3L)).thenReturn(mutualFriend);
        when(userStorage.getUserById(999L)).thenReturn(null);

        List<User> mutualFriends = userService.getMutualFriends(1L, 2L);

        assertEquals(1, mutualFriends.size());
        assertEquals(3L, mutualFriends.get(0).getId());
    }
}
