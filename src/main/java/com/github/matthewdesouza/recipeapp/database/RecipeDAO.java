package com.github.matthewdesouza.recipeapp.database;

import com.github.matthewdesouza.recipeapp.model.Recipe;
import com.github.matthewdesouza.recipeapp.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RecipeDAO {
    public static void createRecipe(Recipe recipe, User user) {
        String sql = """
                INSERT INTO recipes (title, content, uri, userId) VALUES (?, ?, ?, ?)
                """;
        DatabaseConnector db = DatabaseConnector.getInstance();
        try (ResultSet generatedKeys = db.executeUpdateWithKeys(sql,
                recipe.getTitle(),
                recipe.getContent(),
                recipe.getImageUri(),
                user.getId())) {
            if (generatedKeys.next()) {
                recipe.setId(generatedKeys.getInt(1)); // Set the generated ID back to the recipe object
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static Recipe getRecipeByRecipeId(int id) {
        String sql = """
            SELECT * FROM recipes WHERE id = ?
            """;
        DatabaseConnector db = DatabaseConnector.getInstance();
        try (ResultSet rs = db.executeQuery(sql, id)) {
            if (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(id);
                recipe.setTitle(rs.getString("title"));
                recipe.setContent(rs.getString("content"));
                recipe.setImageUri(rs.getString("uri"));
                recipe.setUserId(rs.getInt("userId"));
                return recipe;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static int updateRecipe(Recipe recipe) {
        String sql = """
                UPDATE recipes
                SET title = ?, content = ?, uri = ?, userId = ?
                WHERE id = ?
                """;

        int affectedRows = 0;
        DatabaseConnector db = DatabaseConnector.getInstance();
        try {
            affectedRows = db.executeUpdate(sql,
                    recipe.getTitle(),
                    recipe.getContent(),
                    recipe.getImageUri(),
                    recipe.getUserId(),
                    recipe.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return affectedRows;
    }

    public static int deleteRecipe(int recipeId) {
        String sql = """
                DELETE FROM recipes WHERE id = ?
                """;
        DatabaseConnector db = DatabaseConnector.getInstance();
        int affectedRows = 0;
        try {
            affectedRows = db.executeUpdate(sql, recipeId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return affectedRows;
    }

    public static int deleteRecipe(Recipe recipe) {
        String sql = """
                DELETE FROM recipes WHERE id = ?
                """;
        DatabaseConnector db = DatabaseConnector.getInstance();
        int affectedRows = 0;
        try {
            affectedRows = db.executeUpdate(sql, recipe.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return affectedRows;
    }

    public static Set<Recipe> searchRecipesByTitle(String titlePattern) {
        Set<Recipe> recipes = new HashSet<>();
        String sql = "SELECT * FROM recipes WHERE LOWER( title ) LIKE ?";
        DatabaseConnector db = DatabaseConnector.getInstance();

        try (ResultSet rs = db.executeQuery(sql, "%" + titlePattern + "%")) {
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setTitle(rs.getString("title"));
                recipe.setContent(rs.getString("content"));
                recipe.setImageUri(rs.getString("uri"));
                recipe.setUserId(rs.getInt("userId"));
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return recipes;
    }

    public static Set<Recipe> getAllRecipes() {
        Set<Recipe> recipes = new HashSet<>();
        String sql = "SELECT * FROM recipes";
        DatabaseConnector db = DatabaseConnector.getInstance();

        try (ResultSet rs = db.executeQuery(sql)) {
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setTitle(rs.getString("title"));
                recipe.setContent(rs.getString("content"));
                recipe.setImageUri(rs.getString("uri"));

                // Fetch the user and check if it's not null before getting the id
                User user = UserDAO.getUserById(rs.getInt("userId"));
                if (user != null) {
                    recipe.setUserId(user.getId());
                } else {
                    recipe.setUserId(0); // <unknown> user
                }

                recipes.add(recipe);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return recipes;
    }
}
