import com.github.matthewdesouza.recipeapp.database.DatabaseConnector;
import com.github.matthewdesouza.recipeapp.database.UserDAO;
import com.github.matthewdesouza.recipeapp.database.exception.UserNotFoundException;
import com.github.matthewdesouza.recipeapp.model.User;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOTest {

    @Test
    @Order(1)
    void testCreateUser() {
        User user = new User();
        user.setUsername("test1");
        user.setPassword("test1");
        UserDAO.createUser(user);
        User user2 = UserDAO.getUserByUsername(user.getUsername());

        assertNotNull(user2);
        assertNotEquals(user, user2);
        assertTrue(BCrypt.checkpw(user.getPassword(), user2.getPassword()));
    }

    @Test
    @Order(2)
    void testUsernameExists() {
        assertTrue(UserDAO.usernameExists("test1"));
        assertFalse(UserDAO.usernameExists("iDoNotExist"));
    }

    @Test
    @Order(3)
    void testAuthenticateUser() throws UserNotFoundException {
        User user = UserDAO.authenticateUser("test1", "test1");
        assertNotNull(user);
    }

    @Test
    @Order(4)
    void testDeleteUserByUserObject() throws UserNotFoundException {
        User user = new User();
        user.setUsername("test2");
        user.setPassword("test2");
        UserDAO.createUser(user);

        User user2 = UserDAO.getUserByUsername(user.getUsername());

        int affectedRows = UserDAO.deleteUser(user2);
        assertEquals(1, affectedRows);

    }

    @Test
    @Order(5)
    void testDeleteUserByUserId() throws UserNotFoundException {
        User user = new User();
        user.setUsername("test3");
        user.setPassword("test3");

        UserDAO.createUser(user);

        User user2 = UserDAO.getUserByUsername(user.getUsername());
        int affectedRows = UserDAO.deleteUser(user2.getId());

        assertEquals(1, affectedRows);
    }

    @Test
    @Order(6)
    void testUpdateUserUsername() throws UserNotFoundException {
        User user = new User();
        user.setUsername("test4");
        user.setPassword("test4");
        UserDAO.createUser(user);

        User createdUser = UserDAO.getUserByUsername("test4");
        createdUser.setUsername("test4updated");
        int affectedRows = UserDAO.updateUserUsername(createdUser);
        assertEquals(1, affectedRows);

        User updatedUser = UserDAO.getUserByUsername("test4updated");
        assertEquals("test4updated", updatedUser.getUsername());
    }

    @Test
    @Order(7)
    void testUpdateUserPassword() throws UserNotFoundException {
        User user = new User();
        user.setUsername("test5");
        user.setPassword("test5");
        UserDAO.createUser(user);

        User createdUser = UserDAO.getUserByUsername("test5");
        createdUser.setPassword("test5updated");
        int affectedRows = UserDAO.updateUserPassword(createdUser);
        assertEquals(1, affectedRows);

        User updatedUser = UserDAO.getUserByUsername("test5");
        assertTrue(BCrypt.checkpw("test5updated", updatedUser.getPassword()));
    }

    @Test
    @Order(8)
    void testUpdateUser() throws UserNotFoundException {
        User user = new User();
        user.setUsername("test6");
        user.setPassword("test6");
        UserDAO.createUser(user);

        User createdUser = UserDAO.getUserByUsername("test6");
        createdUser.setUsername("test6updated");
        createdUser.setPassword("test6updated");
        int affectedRows = UserDAO.updateUser(createdUser);
        assertEquals(2, affectedRows); // Expecting 2 affected rows (1 for username and 1 for password)

        User updatedUser = UserDAO.getUserByUsername("test6updated");
        assertNotNull(updatedUser);
        assertTrue(BCrypt.checkpw("test6updated", updatedUser.getPassword()));
    }

    @AfterAll
    static void resetDatabase() throws SQLException {
        DatabaseConnector.getInstance().executeQuery("DROP SCHEMA recipe");
    }
}
