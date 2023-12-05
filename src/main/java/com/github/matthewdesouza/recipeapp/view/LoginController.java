package com.github.matthewdesouza.recipeapp.view;

import com.github.matthewdesouza.recipeapp.RecipeApp;
import com.github.matthewdesouza.recipeapp.database.UserDAO;
import com.github.matthewdesouza.recipeapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Stage primaryStage;

    private static User currentUser;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!isValidUsername(username) || !isValidPassword(password)) {
            showAlert("Invalid Input", "Username or password does not meet the criteria.");
            return;
        }

        try {
            User user = UserDAO.authenticateUser(username, password);
            if (!user.getPassword().isBlank()) {
                currentUser = user;
                showAlert("Login Successful", "Welcome, " + username + "!");
                loadMainView();
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) { // Consider more specific exception handling based on your application's requirements
            showAlert("Error", "An error occurred during login.");
        }
    }

    @FXML
    protected void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!isValidUsername(username) || !isValidPassword(password)) {
            showAlert("Invalid Input", "Username or password does not meet the criteria.");
            return;
        }

        try {
            if (!UserDAO.usernameExists(username)) {
                UserDAO.createUser(new User(username, password));
                currentUser = UserDAO.getUserByUsername(username);
                showAlert("Registration Successful", "User registered successfully.");
                loadMainView();
            } else {
                showAlert("Registration Failed", "Username already in use.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Registration Failed", "An error occurred during registration, try a different username.");
        }
    }

    private boolean isValidUsername(String username) {
        return username.matches("\\w{3,16}"); // Regex for username constraints
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 32;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(RecipeApp.class.getResource("fxml/main.fxml"));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
