package com.github.matthewdesouza.recipeapp.view;

import com.github.matthewdesouza.recipeapp.database.RecipeDAO;
import com.github.matthewdesouza.recipeapp.model.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class RecipeController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField imageField;

    @FXML
    private TextArea contentArea;

    @FXML
    private Button submitButton;

    private Recipe recipe;
    private boolean isEditMode = false;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        isEditMode = recipe != null;

        if (isEditMode) {
            // Edit mode
            titleField.setText(recipe.getTitle());
            imageField.setText(recipe.getImageUri());
            contentArea.setText(recipe.getContent());
            submitButton.setText("Edit");
        } else {
            // Add mode
            submitButton.setText("Add");
        }
    }

    @FXML
    private void handleSubmit() {
        String title = titleField.getText();
        String imageUri = imageField.getText();
        String content = contentArea.getText();
        mainController.closeCurrentTab();

        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Error", "Title and content cannot be empty.");
            return;
        }

        if (isEditMode) {
            // Handle updating an existing recipe
            recipe.setTitle(title);
            recipe.setImageUri(imageUri);
            recipe.setContent(content);
            int success = RecipeDAO.updateRecipe(recipe);
            if (success == 1) {
                showAlert("Success", "Recipe updated successfully.");
                mainController.refreshRecipes();
                mainController.updateTabContent(recipe);
                closeWindow();
            } else {
                showAlert("Error", "Error updating recipe.");
            }
        } else {
            // Handle adding a new recipe
            Recipe newRecipe = new Recipe();
            newRecipe.setTitle(title);
            newRecipe.setImageUri(imageUri);
            newRecipe.setContent(content);
            RecipeDAO.createRecipe(newRecipe, LoginController.getCurrentUser());
            mainController.refreshRecipes();
            showAlert("Success", "Recipe added successfully.");
            closeWindow();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
