package com.github.matthewdesouza.recipeapp.database;

import com.github.matthewdesouza.recipeapp.database.exception.UserNotFoundException;
import com.github.matthewdesouza.recipeapp.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    /**
     * Creates a new user in the database.
     * @param user The user object to be created.
     */
    public static void createUser(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        DatabaseConnector db = DatabaseConnector.getInstance();

        try (ResultSet generatedKeys = db.executeUpdateWithKeys(sql,
                user.getUsername(),
                BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))) {
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1)); // Set the generated ID back to the user object
            }
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
        }
        logger.info("User {} created successfully.", user);
    }

    /**
     * Authenticates a user based on username and password.
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @return A User object if authentication is successful.
     * @throws UserNotFoundException if no matching user is found.
     */
    public static User authenticateUser(String username, String password) throws UserNotFoundException {
        String sql = """
                SELECT * FROM users WHERE username = ?
                """;
        DatabaseConnector db = DatabaseConnector.getInstance();
        try (ResultSet rs = db.executeQuery(sql, username)) {
            if (rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setLikedRecipes(getUserLikedRecipe(user.getId()));
                logger.info("User {} successfully authenticated, welcome.", user);
                return user;
            }
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
        }
        throw new UserNotFoundException("Username-password combo not found in database.");
    }

    public static int updateUser(User user) {
        int affectedRows = 0;

        // Update username
        affectedRows += updateUserUsername(user);

        // Update password
        affectedRows += updateUserPassword(user);

        return affectedRows;
    }

    public static int updateUserUsername(User user) {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        DatabaseConnector db = DatabaseConnector.getInstance();
        try {
            int rv = db.executeUpdate(sql, user.getUsername(), user.getId());
            if (rv == 1) {
                logger.info("User {} username successfully updated.", user);
            }
            return rv;
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
            return -1;
        }
    }

    public static int updateUserPassword(User user) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        DatabaseConnector db = DatabaseConnector.getInstance();
        try {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            int rv = db.executeUpdate(sql, hashedPassword, user.getId());
            if (rv == 1) {
                logger.info("User {} password successfully updated.", user);
            }
            return db.executeUpdate(sql, hashedPassword, user.getId());
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
            return 0;
        }
    }

    public static int deleteUser(int userId) {
        String sql = """
                DELETE FROM users WHERE id = ?
                """;

        int affectedRows = 0;
        DatabaseConnector db = DatabaseConnector.getInstance();
        try {
            affectedRows = db.executeUpdate(sql, userId);
            if (affectedRows == 1) {
                logger.info("User (id={}) deleted successfully.", userId);
            }
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
        }
        return affectedRows;
    }

    public static int deleteUser(User user) {
        String sql = """
                DELETE FROM users WHERE id = ?
                """;

        int affectedRows = 0;
        DatabaseConnector db = DatabaseConnector.getInstance();
        try {
            affectedRows = db.executeUpdate(sql, user.getId());
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
        }
        return affectedRows;
    }

    /**
     * Retrieves a user by username.
     * @param username The username of the user to retrieve.
     * @return A User object if found, null otherwise.
     */
    public static User getUserByUsername(String username) {
        String sql = """
            SELECT * FROM users WHERE username = ?
            """;
        DatabaseConnector db = DatabaseConnector.getInstance();

        try (ResultSet rs = db.executeQuery(sql, username)) {
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setLikedRecipes(getUserLikedRecipe(user.getId()));
                return user;
            } else {
                logger.warn("Username not found: `{}`, returning null.", username);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
            return null;
        }
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user to retrieve.
     * @return A User object if found, null otherwise.
     */
    public static User getUserById(int id) {
        String sql = """
            SELECT * FROM users WHERE id = ?
            """;
        DatabaseConnector db = DatabaseConnector.getInstance();

        try (ResultSet rs = db.executeQuery(sql, id)) {
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setLikedRecipes(getUserLikedRecipe(user.getId()));
                logger.info("User {} found by id.", user);
                return user;
            } else {
                logger.warn("User id not found: {}", id);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
            return null;
        }
    }

    public static boolean usernameExists(String username) {
        return getUserByUsername(username) != null;
    }

    public static Set<Integer> getUserLikedRecipe(int id) {
        Set<Integer> rv = new HashSet<>();
        String sql = """
                SELECT recipeId FROM user_likes WHERE userId = ?
                """;
        DatabaseConnector db = DatabaseConnector.getInstance();
        try (ResultSet rs = db.executeQuery(sql, id)) {
            while (rs.next()) {
                rv.add(rs.getInt("recipeId"));
            }
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
        }
        return rv;
    }
}
