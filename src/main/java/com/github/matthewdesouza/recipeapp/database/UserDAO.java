package com.github.matthewdesouza.recipeapp.database;

import com.github.matthewdesouza.recipeapp.database.exception.UserNotFoundException;
import com.github.matthewdesouza.recipeapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class UserDAO {
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Takes a username and password to authenticate a user within the database.
     *
     * @param username Username to check in the database.
     * @param password Password to check in the database.
     * @return A deserialized {@link User} object.
     * @throws UserNotFoundException Thrown if user is not found in the database.
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
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
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
            return db.executeUpdate(sql, user.getUsername(), user.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public static int updateUserPassword(User user) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        DatabaseConnector db = DatabaseConnector.getInstance();
        try {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            return db.executeUpdate(sql, hashedPassword, user.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return affectedRows;
    }

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
                // Handle the case where no user is found
                System.out.println("No user found with username: " + username);
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

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
                return user;
            } else {
                // Handle the case where no user is found
                System.out.println("No user found with id: " + id);
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return rv;
    }
}
