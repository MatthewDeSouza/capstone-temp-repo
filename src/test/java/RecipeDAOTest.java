import com.github.matthewdesouza.recipeapp.database.DatabaseConnector;
import com.github.matthewdesouza.recipeapp.database.RecipeDAO;
import com.github.matthewdesouza.recipeapp.database.UserDAO;
import com.github.matthewdesouza.recipeapp.model.Recipe;
import com.github.matthewdesouza.recipeapp.model.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeDAOTest {
    private static User testUser;

    @BeforeAll
    static void setUp() {
        // Assuming UserDAO.createUser is available and correctly implemented, passing tests.
        testUser = new User();
        testUser.setUsername("recipeDAOuser");
        testUser.setPassword("recipeDAOuser");
        UserDAO.createUser(testUser);
    }

    @Test
    @Order(1)
    void testCreateRecipe() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Test Recipe");
        recipe.setContent("Test Content");
        recipe.setImageUri("http://example.com/image.jpg");
        recipe.setUserId(testUser.getId());

        RecipeDAO.createRecipe(recipe, testUser);
        assertEquals(2, recipe.getUserId());

        Recipe recipe2 = RecipeDAO.getRecipeByRecipeId(recipe.getId());

        assertEquals(recipe, recipe2);
    }

    @Test
    @Order(2)
    void testGetRecipeByRecipeId() {
        // Assuming the recipe with ID 1 exists
        Recipe recipe = RecipeDAO.getRecipeByRecipeId(1);
        assertNotNull(recipe);
        assertEquals("Test Recipe", recipe.getTitle());
    }

    @Test
    @Order(3)
    void testGetAllRecipes() {
        Set<Recipe> recipes = RecipeDAO.getAllRecipes();
        assertNotNull(recipes);
        assertFalse(recipes.isEmpty());
    }

    @Test
    @Order(4)
    void testUpdateRecipe() {
        Recipe recipe = RecipeDAO.getRecipeByRecipeId(1); // Assuming this recipe exists
        assertNotNull(recipe);
        recipe.setTitle("Updated Recipe Title");

        int affectedRows = RecipeDAO.updateRecipe(recipe);
        assertEquals(1, affectedRows);

        Recipe updatedRecipe = RecipeDAO.getRecipeByRecipeId(1);
        assertEquals("Updated Recipe Title", updatedRecipe.getTitle());
    }

    @Test
    @Order(5)
    void testSearchRecipesByTitle() {
        // Assuming a recipe with the title "Test Recipe" exists in the database
        Set<Recipe> foundRecipes = RecipeDAO.searchRecipesByTitle("ecip");
        assertFalse(foundRecipes.isEmpty());
        assertTrue(foundRecipes.stream().anyMatch(recipe -> recipe.getTitle().contains("ecip")));
    }

    @Test
    @Order(6)
    void testDeleteRecipeById() {
        int affectedRows = RecipeDAO.deleteRecipe(1); // Assuming the recipe with ID 1 exists
        assertEquals(1, affectedRows);
    }

    @AfterAll
    static void resetDatabase() throws SQLException {
        DatabaseConnector.getInstance().executeQuery("DROP SCHEMA recipe");
    }
}
