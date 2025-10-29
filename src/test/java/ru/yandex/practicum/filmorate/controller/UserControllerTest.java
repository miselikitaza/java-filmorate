package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(1L);
        validUser.setEmail("test@mail.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createUserWithValidDataShouldReturn200() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(validUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@mail.com")))
                .andExpect(jsonPath("$.login", is("testlogin")))
                .andExpect(jsonPath("$.name", is("Test User")));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void createUserWithNullEmailShouldReturn400() throws Exception {
        User userWithNullEmail = new User();
        userWithNullEmail.setLogin("testlogin");
        userWithNullEmail.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithNullEmail)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void createUserWithInvalidEmailShouldReturn400() throws Exception {
        User userWithInvalidEmail = new User();
        userWithInvalidEmail.setEmail("invalid-email");
        userWithInvalidEmail.setLogin("testlogin");
        userWithInvalidEmail.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithInvalidEmail)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void createUserWithNullLoginShouldReturn400() throws Exception {
        User userWithNullLogin = new User();
        userWithNullLogin.setEmail("test@mail.com");
        userWithNullLogin.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithNullLogin)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void createUserWithBlankLoginShouldReturn400() throws Exception {
        User userWithBlankLogin = new User();
        userWithBlankLogin.setEmail("test@mail.com");
        userWithBlankLogin.setLogin("   ");
        userWithBlankLogin.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithBlankLogin)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void createUserWithLoginContainingSpacesShouldReturn400() throws Exception {
        User userWithSpacesInLogin = new User();
        userWithSpacesInLogin.setEmail("test@mail.com");
        userWithSpacesInLogin.setLogin("login with spaces");
        userWithSpacesInLogin.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithSpacesInLogin)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void createUserWithNullNameShouldUseLoginAsName() throws Exception {
        User userWithNullName = new User();
        userWithNullName.setEmail("test@mail.com");
        userWithNullName.setLogin("testlogin");
        userWithNullName.setBirthday(LocalDate.of(1990, 1, 1));

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setEmail("test@mail.com");
        expectedUser.setLogin("testlogin");
        expectedUser.setName("testlogin");
        expectedUser.setBirthday(LocalDate.of(1990, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(expectedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithNullName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testlogin")));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void createUserWithFutureBirthdayShouldReturn400() throws Exception {
        User userWithFutureBirthday = new User();
        userWithFutureBirthday.setEmail("test@mail.com");
        userWithFutureBirthday.setLogin("testlogin");
        userWithFutureBirthday.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithFutureBirthday)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void updateUserWithValidDataShouldReturn200() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("updated@mail.com");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1995, 1, 1));

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("updated@mail.com")))
                .andExpect(jsonPath("$.name", is("Updated User")));

        verify(userService).updateUser(any(User.class));
    }

    @Test
    void updateUserWithNonExistentIdShouldReturn404() throws Exception {
        User nonExistentUser = new User();
        nonExistentUser.setId(999L);
        nonExistentUser.setEmail("test@mail.com");
        nonExistentUser.setLogin("testlogin");

        when(userService.updateUser(any(User.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUser)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(any(User.class));
    }

    @Test
    void updateUserWithNullIdShouldReturn400() throws Exception {
        User userWithNullId = new User();
        userWithNullId.setEmail("test@mail.com");
        userWithNullId.setLogin("testlogin");

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithNullId)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    void getUserByIdWithExistingIdShouldReturn200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(validUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@mail.com")));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserByIdWithNonExistentIdShouldReturn404() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    @Test
    void getAllUsersShouldReturn200() throws Exception {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@mail.com");
        user2.setLogin("user2login");

        List<User> users = Arrays.asList(validUser, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("test@mail.com")))
                .andExpect(jsonPath("$[1].email", is("user2@mail.com")));

        verify(userService).getAllUsers();
    }

    @Test
    void deleteUserByIdShouldReturn200() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(1L);
    }

    @Test
    void deleteAllUsersShouldReturn200() throws Exception {
        doNothing().when(userService).deleteAllUsers();

        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());

        verify(userService).deleteAllUsers();
    }

    @Test
    void addFriendShouldReturn204() throws Exception {
        doNothing().when(userService).addFriend(1L, 2L);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNoContent());

        verify(userService).addFriend(1L, 2L);
    }

    @Test
    void addFriendWithNonExistentUserShouldReturn404() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).addFriend(999L, 2L);

        mockMvc.perform(put("/users/999/friends/2"))
                .andExpect(status().isNotFound());

        verify(userService).addFriend(999L, 2L);
    }

    @Test
    void deleteFriendShouldReturn204() throws Exception {
        doNothing().when(userService).deleteFriend(1L, 2L);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isNoContent());

        verify(userService).deleteFriend(1L, 2L);
    }

    @Test
    void getFriendsShouldReturn200() throws Exception {
        User friend = new User();
        friend.setId(2L);
        friend.setEmail("friend@mail.com");
        friend.setLogin("friendlogin");

        List<User> friends = Arrays.asList(friend);
        when(userService.getFriends(1L)).thenReturn(friends);

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].email", is("friend@mail.com")));

        verify(userService).getFriends(1L);
    }

    @Test
    void getFriendsWithNonExistentUserShouldReturn404() throws Exception {
        when(userService.getFriends(999L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/999/friends"))
                .andExpect(status().isNotFound());

        verify(userService).getFriends(999L);
    }

    @Test
    void getMutualFriendsShouldReturn200() throws Exception {
        User mutualFriend = new User();
        mutualFriend.setId(3L);
        mutualFriend.setEmail("mutual@mail.com");
        mutualFriend.setLogin("mutuallogin");

        List<User> mutualFriends = Arrays.asList(mutualFriend);
        when(userService.getMutualFriends(1L, 2L)).thenReturn(mutualFriends);

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].email", is("mutual@mail.com")));

        verify(userService).getMutualFriends(1L, 2L);
    }

    @Test
    void getMutualFriendsWithNonExistentUserShouldReturn404() throws Exception {
        when(userService.getMutualFriends(999L, 2L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/999/friends/common/2"))
                .andExpect(status().isNotFound());

        verify(userService).getMutualFriends(999L, 2L);
    }

    @Test
    void getMutualFriendsWithNonExistentFriendShouldReturn404() throws Exception {
        when(userService.getMutualFriends(1L, 999L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/1/friends/common/999"))
                .andExpect(status().isNotFound());

        verify(userService).getMutualFriends(1L, 999L);
    }
}