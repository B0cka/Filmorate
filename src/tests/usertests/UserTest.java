package usertests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserDbStorage.class)
public class UserTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    public void testCreateAndFindUserById() {

        User user = new User();
        user.setEmail("mail@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.create(user);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getById(created.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(created.getId());
                    assertThat(u.getEmail()).isEqualTo("mail@example.com");
                    assertThat(u.getLogin()).isEqualTo("testLogin");
                    assertThat(u.getName()).isEqualTo("Test User");
                    assertThat(u.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("mail@example.com");
        user.setLogin("testLogin_2");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.create(user);

        user.setEmail("mail@example.com.yup");
        user.setLogin("testLogin_22211155");
        user.setName("Test User2111");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User updated = userStorage.update(user);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getById(updated.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(updated.getId());
                    assertThat(u.getEmail()).isEqualTo("mail@example.com.yup");
                    assertThat(u.getLogin()).isEqualTo("testLogin_22211155");
                    assertThat(u.getName()).isEqualTo("Test User2111");
                    assertThat(u.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }

    @Test
    public void testCreateUserAndAddFriend() {

        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);
        Long userId = createdUser.getId();

        User friend = new User();
        friend.setEmail("friend@example.com");
        friend.setLogin("testFriend");
        friend.setName("Test Friend");
        friend.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFriend = userStorage.create(friend);
        Long friendId = createdFriend.getId();

        userStorage.sendFriendRequest(userId, friendId);

        List<User> friends = userStorage.getFriends(userId);

        assertThat(friends)
                .isNotEmpty()
                .anyMatch(f -> f.getId() == friendId);
    }

    @Test
    public void testRemoveUserAndAddFriend() {

        User user = new User();
        user.setEmail("user@example.com.but");
        user.setLogin("testUser123");
        user.setName("Test User121212");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);
        Long userId = createdUser.getId();

        User friend = new User();
        friend.setEmail("friend@example.com.dot");
        friend.setLogin("testFriend545");
        friend.setName("Test Friend777");
        friend.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFriend = userStorage.create(friend);
        Long friendId = createdFriend.getId();

        userStorage.sendFriendRequest(userId, friendId);
        userStorage.removeFriend(userId, friendId);

        List<User> friends = userStorage.getFriends(userId);
        assertThat(friends)
                .isEmpty();
    }

    @Test
    public void testGetCommonFriends() {

        User user = new User();
        user.setEmail("user@example.but");
        user.setLogin("testUse1");
        user.setName("Test Use1212");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);
        Long userId = createdUser.getId();

        User friend = new User();
        friend.setEmail("friend@exampl.dot");
        friend.setLogin("testFrien545");
        friend.setName("Test Frien777");
        friend.setBirthday(LocalDate.of(1990, 1, 1));
        User createdFriend = userStorage.create(friend);
        Long friendId = createdFriend.getId();

        User friend2 = new User();  // Исправлено имя переменной
        friend2.setEmail("friend@example.com.dot1");
        friend2.setLogin("testFriend5451");
        friend2.setName("Test Friend7771");
        friend2.setBirthday(LocalDate.of(1990, 11, 1));
        User createdFriend2 = userStorage.create(friend2);  // Исправлено имя переменной
        Long friend2Id = createdFriend2.getId();  // Исправлено имя переменной

        userStorage.sendFriendRequest(friendId, userId);
        userStorage.sendFriendRequest(friend2Id, userId);  // Исправлено имя переменной

        List<User> friends = userStorage.getCommonFriends(friendId, friend2Id);  // Исправлено имя переменной
        assertThat(friends)
                .isNotEmpty();
    }
}
