package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private User validUser;

    @BeforeEach
    void create() {
        userController = new UserController();
        validUser = new User();
        validUser.setLogin("testLogin");
        validUser.setName("testName");
        validUser.setEmail("testEmail@mail.ru");
        validUser.setBirthday(LocalDate.of(2000, 3, 12));
    }

    @Test
    void shouldCreateUserWithValidData() {
        User createdUser = userController.createUser(validUser);
        assertNotNull(createdUser.getId());
        assertEquals("testEmail@mail.ru", createdUser.getEmail());
        assertEquals("testLogin", createdUser.getLogin());
        assertEquals("testName", createdUser.getName());
        assertEquals(LocalDate.of(2000, 3, 12), createdUser.getBirthday());
    }

    @Test
    void createUserWithBlankEmailShouldThrowException() {
        validUser.setEmail("   ");
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.createUser(validUser);
        });
    }

    @Test
    void createUserWithNullEmailShouldThrowException() {
        validUser.setEmail(null);
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.createUser(validUser);
        });
    }

    @Test
    void createUserWithEmailMissingAtSymbolShouldThrowException() {
        validUser.setEmail("work.email.com");
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.createUser(validUser);
        });
    }

    @Test
    void createUserWithNullLoginShouldThrowException() {
        validUser.setLogin(null);
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.createUser(validUser);
        });
    }

    @Test
    void createUserWithBlankLoginShouldThrowException() {
        validUser.setLogin("   ");
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.createUser(validUser);
        });
    }

    @Test
    void createUserWithLoginContainingSpacesShouldThrowException() {
        validUser.setLogin("логин с пробелами");
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.createUser(validUser);
        });
    }

    @Test
    void createUserWithNullNameShouldUseLoginAsName() {
        validUser.setName(null);
        User createdUser = userController.createUser(validUser);
        assertEquals("testLogin", createdUser.getName());
    }

    @Test
    void createUserWithBlankNameShouldUseLoginAsName() {
        validUser.setName("   ");
        User createdUser = userController.createUser(validUser);
        assertEquals("testLogin", createdUser.getName());
    }

    @Test
    void createUserWithFutureBirthdayShouldThrowException() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.createUser(validUser);
        });
    }

    @Test
    void createUserWithTodayBirthdayShouldBeValid() {
        validUser.setBirthday(LocalDate.now());
        User createdUser = userController.createUser(validUser);
        assertNotNull(createdUser);
        assertEquals(LocalDate.now(), createdUser.getBirthday());
    }

    @Test
    void createUserWithPastBirthdayShouldBeValid() {
        validUser.setBirthday(LocalDate.now().minusYears(30));
        User createdUser = userController.createUser(validUser);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getBirthday());
    }

    @Test
    void shouldUpdateUserWithValidData() {
        User oldUser = userController.createUser(validUser);
        User updatedUser = new User();
        updatedUser.setId(oldUser.getId());
        updatedUser.setLogin("updatedLogin");
        updatedUser.setName("updatedName");
        updatedUser.setEmail("updatedEmail@mail.ru");

        userController.updateUser(updatedUser);
        assertEquals("updatedLogin", oldUser.getLogin());
        assertEquals("updatedName", oldUser.getName());
        assertEquals("updatedEmail@mail.ru", oldUser.getEmail());
    }

    @Test
    void updateUserWithNonExistentIdShouldThrowException() {
        validUser.setId(999L);
        assertThrows(NotFoundException.class, () -> {
            userController.updateUser(validUser);
        });
    }

    @Test
    void updateUserWithNullIdShouldThrowException() {
        validUser.setId(null);
        assertThrows(ConditionsNotMetException.class, () -> {
            userController.updateUser(validUser);
        });
    }

    @Test
    void canGetAllUsers() {
        userController.createUser(validUser);
        assertEquals(1, userController.getAllUsers().size());
        assertTrue(userController.getAllUsers().contains(validUser));
    }
}