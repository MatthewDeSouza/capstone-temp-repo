package com.github.matthewdesouza.recipeapp.view;

import com.github.matthewdesouza.recipeapp.RecipeApp;
import com.github.matthewdesouza.recipeapp.database.RecipeDAO;
import com.github.matthewdesouza.recipeapp.database.UserDAO;
import com.github.matthewdesouza.recipeapp.model.Recipe;
import com.github.matthewdesouza.recipeapp.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


public class MainController {
    @FXML
    private TableView<Recipe> recipeTableView;

    @FXML
    private TableColumn<Recipe, String> userColumn;

    @FXML
    private TableColumn<Recipe, String> recipeTitleColumn;

    @FXML
    private TabPane recipeTabPane;

    private ObservableList<Recipe> recipesList;

    @FXML
    public void initialize() {
        loadRecipes();
        setupTableViewSelection();
    }

    public void refreshRecipes() {
        // Refresh the list of recipes
        recipesList.setAll(RecipeDAO.getAllRecipes());
    }

    private void loadRecipes() {
        recipesList = FXCollections.observableArrayList(RecipeDAO.getAllRecipes());
        recipeTableView.setItems(recipesList);

        userColumn.setCellValueFactory(cellData -> {
            int userId = cellData.getValue().getUserId();
            User user = UserDAO.getUserById(userId);
            return new SimpleStringProperty(user != null ? user.getUsername() : "<unknown>");
        });
        recipeTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
    }

    private void setupTableViewSelection() {
        recipeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectOrCreateTab(newSelection);
            }
        });
    }

    private void selectOrCreateTab(Recipe recipe) {
        for (Tab tab : recipeTabPane.getTabs()) {
            if (tab.getText().equals(recipe.getTitle())) {
                recipeTabPane.getSelectionModel().select(tab);
                return;
            }
        }
        createTabForRecipe(recipe);
    }

    @FXML
    protected void closeCurrentTab() {
        Tab selectedTab = recipeTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            recipeTabPane.getTabs().remove(selectedTab);
        }
    }

    private void openRecipeEditor(Recipe recipe) {
        try {
            FXMLLoader loader = new FXMLLoader(RecipeApp.class.getResource("fxml/recipe.fxml"));
            Parent root = loader.load();

            RecipeController controller = loader.getController();
            controller.setRecipe(recipe); // Pass the selected recipe or null
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle(recipe == null ? "Create Recipe" : "Edit Recipe");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while opening the editor.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void createTabForRecipe(Recipe recipe) {
        Tab tab = new Tab(recipe.getTitle());
        VBox contentBox = new VBox();

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(recipe.getImageUri()));
        } catch (IllegalArgumentException e) {
            imageView.setImage(new Image(Objects.requireNonNull(RecipeApp.class.getResource("img/image-not-found.jpg")).toExternalForm()));
        }
        imageView.setFitHeight(180);
        imageView.setFitWidth(560);
        imageView.setPreserveRatio(true);
        HBox imageBox = new HBox(imageView);
        imageBox.setAlignment(javafx.geometry.Pos.CENTER);

        TextArea textArea = new TextArea(recipe.getContent());
        textArea.setEditable(false);
        ScrollPane scrollPane = new ScrollPane(textArea);
        HBox textBox = new HBox(scrollPane);
        textBox.setAlignment(javafx.geometry.Pos.CENTER);

        contentBox.getChildren().addAll(imageBox, textBox);
        AnchorPane.setTopAnchor(contentBox, 0.0);
        AnchorPane.setRightAnchor(contentBox, 0.0);
        AnchorPane.setBottomAnchor(contentBox, 0.0);
        AnchorPane.setLeftAnchor(contentBox, 0.0);

        AnchorPane contentPane = new AnchorPane(contentBox);
        tab.setContent(contentPane);
        recipeTabPane.getTabs().add(tab);
    }

    public void handleCreateRecipe() {
        openRecipeEditor(null);
    }

    public void updateTabContent(Recipe recipe) {
        for (Tab tab : recipeTabPane.getTabs()) {
            if (tab.getText().equals(recipe.getTitle())) {
                VBox contentBox = (VBox) ((AnchorPane) tab.getContent()).getChildren().get(0);
                ImageView imageView = (ImageView) ((HBox) contentBox.getChildren().get(0)).getChildren().get(0);
                TextArea textArea = (TextArea) ((ScrollPane) ((HBox) contentBox.getChildren().get(1)).getChildren().get(0)).getContent();

                try {
                    imageView.setImage(new Image(recipe.getImageUri()));
                } catch (IllegalArgumentException e) {
                    imageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/image-not-found.jpg")).toExternalForm()));
                }
                textArea.setText(recipe.getContent());
                break;
            }
        }
    }

    public void handleEditRecipe() {
        Recipe selectedRecipe = recipeTableView.getSelectionModel().getSelectedItem();
        if (selectedRecipe != null) {
            openRecipeEditor(selectedRecipe);
        } else {
            // Show an alert if no recipe is selected
            showAlert("No Selection", "Please select a recipe to edit.");
        }
    }

    public void editProgram() {
        System.exit(1);
    }

    public void handleDeleteRecipe() {
        Recipe selectedRecipe = recipeTableView.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            showAlert("No Selection", "Please select a recipe to delete.");
            return;
        }

        User currentUser = LoginController.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user logged in.");
            return;
        }

        // Show confirmation dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this recipe?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText(null);

        // Wait for user response
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            // User confirmed the deletion
            if (selectedRecipe.getUserId() == currentUser.getId() || "admin".equals(currentUser.getUsername())) {
                // Authorized to delete the recipe
                int success = RecipeDAO.deleteRecipe(selectedRecipe.getId());
                if (success == 1) {
                    showAlert("Success", "Recipe deleted successfully.");
                    closeCurrentTab();
                    refreshRecipes();
                } else {
                    showAlert("Error", "Error deleting recipe.");
                }
            } else {
                // Not authorized to delete the recipe
                showAlert("Unauthorized", "You do not have permission to delete this recipe.");
            }
        } else {
            // User canceled the deletion
            showAlert("Deletion Canceled", "Recipe deletion was canceled.");
        }
    }
}