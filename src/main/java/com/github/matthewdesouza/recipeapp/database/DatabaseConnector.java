package com.github.matthewdesouza.recipeapp.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DatabaseConnector {
    private static final String URL = "jdbc:mariadb://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "mariadb";

    private Connection connection;

    private DatabaseConnector() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            // Create schema.
            statement.execute("""
                    CREATE DATABASE IF NOT EXISTS recipe;
                    """);

            // Make schema active.
            statement.execute("""
                    USE recipe;
                    """);

            // Create users table.
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
                    );
                    """);

            // Create recipes table.
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS recipes (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        content TEXT NOT NULL,
                        uri VARCHAR(255),
                        userId INT,
                        FOREIGN KEY (userId) REFERENCES users(id)
                    );
                    """);

            // Create join table for recipes users have liked.
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS user_likes (
                        userId INT,
                        recipeId INT,
                        PRIMARY KEY (userId, recipeId),
                        FOREIGN KEY (userId) REFERENCES users(id),
                        FOREIGN KEY (recipeId) REFERENCES recipes(id)
                    );
                    """);
            // Create admin account with default password `testing`
            statement.execute("""
INSERT INTO users VALUES (0, "admin", "%s");
""".formatted(BCrypt.hashpw("testing123", BCrypt.gensalt())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes an update (INSERT) SQL statement and returns generated keys.
     *
     * @param sql        SQL query to execute.
     * @param parameters Parameters for the prepared statement.
     * @return Generated keys as a ResultSet.
     * @throws SQLException If an error occurs during the query execution.
     */
    public ResultSet executeUpdateWithKeys(String sql, Object... parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        setPreparedStatementParameters(preparedStatement, parameters);
        preparedStatement.executeUpdate();
        return preparedStatement.getGeneratedKeys();
    }

    private void setPreparedStatementParameters(PreparedStatement preparedStatement, Object[] parameters) throws SQLException {
        int i = 1;
        for (Object parameter : parameters) {
            if (parameter instanceof String) {
                preparedStatement.setString(i, (String) parameter);
            } else if (parameter instanceof Integer) {
                preparedStatement.setInt(i, (Integer) parameter);
            } else if (parameter instanceof Double) {
                preparedStatement.setDouble(i, (Double) parameter);
            }
            i++;
        }
    }

    public static DatabaseConnector getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Helper function for executing {@link Statement} and {@link PreparedStatement} queries.
     *
     * @param sql        SQL query to execute.
     * @param parameters Optional vararg parameter for prepared statements.
     * @return {@link ResultSet} values of executed statement.
     * @throws SQLException Thrown in the case that provided parameters are not valid.
     */
    public ResultSet executeQuery(String sql, Object... parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] instanceof String) {
                preparedStatement.setString(i + 1, (String) parameters[i]);
            } else if (parameters[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) parameters[i]);
            }
        }

        return preparedStatement.executeQuery();
    }

    public int executeUpdate(String sql, Object... parameters) throws SQLException {
        if (parameters.length == 0) {
            throw new SQLException("UPDATE query must include at least 1 parameter.");
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object parameter : parameters) {
            if (parameter instanceof String) {
                preparedStatement.setString(i, (String) parameter);
            } else if (parameter instanceof Integer) {
                preparedStatement.setInt(i, (Integer) parameter);
            } else if (parameter instanceof Double) {
                preparedStatement.setDouble(i, (Double) parameter);
            }
            i++;
        }
        return preparedStatement.executeUpdate();
    }

    /**
     * Static inner class for holding the instance (Bill Pugh Singleton Implementation)
     */
    private static class SingletonHelper {
        private static final DatabaseConnector INSTANCE = new DatabaseConnector();
    }
}
