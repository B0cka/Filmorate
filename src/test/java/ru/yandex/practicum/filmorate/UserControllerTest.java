package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import org.junit.jupiter.api.Assertions;
import java.time.LocalDate;
import java.util.Collection;

class UserControllerTest {
    private UserController userController;
    private User validUser;

    @Test
    void shouldCreateValidUser() {
        User createdUser = userController.create(validUser);
        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals("test@example.com", createdUser.getEmail());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        validUser.setEmail("invalidemail");

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(validUser);
        });

        Assertions.assertEquals("В имейле должна быть @", thrown.getMessage());
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() {
        validUser.setLogin(" ");

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(validUser);
        });

        Assertions.assertEquals("Логин не может быть пустым", thrown.getMessage());
    }

    @Test
    void shouldNotCreateUserWithLoginContainingSpaces() {
        validUser.setLogin("invalid login");

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(validUser);
        });

        Assertions.assertEquals("Логин не должен содержать пробелы", thrown.getMessage());
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(validUser);
        });

        Assertions.assertEquals("Дата рождения не может быть в будущем", thrown.getMessage());
    }

    @Test
    void shouldAssignLoginAsNameIfNameIsEmpty() {
        validUser.setName("");
        User createdUser = userController.create(validUser);
        Assertions.assertEquals("testUser", createdUser.getName());
    }

    @Test
    void shouldUpdateUser() {
        User createdUser = userController.create(validUser);
        createdUser.setName("Updated Name");

        User updatedUser = userController.update(createdUser);
        Assertions.assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void shouldNotUpdateNonExistentUser() {
        validUser.setId(999L);

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            userController.update(validUser);
        });

        Assertions.assertEquals("Пользователь с id=999 не найден", thrown.getMessage());
    }

    @Test
    void shouldGetAllUsers() {
        userController.create(validUser);
        Collection<User> users = userController.getAll();
        Assertions.assertEquals(1, users.size());
    }
}
