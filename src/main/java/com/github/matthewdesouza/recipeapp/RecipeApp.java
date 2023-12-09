package com.github.matthewdesouza.recipeapp;

import atlantafx.base.theme.PrimerDark;
import com.github.matthewdesouza.recipeapp.logging.LoggerConfigurator;
import com.github.matthewdesouza.recipeapp.view.LoginController;
import com.github.matthewdesouza.recipeapp.view.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RecipeApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/login.fxml"));
        Parent root = loader.load();
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(stage);

        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle("Login");
        stage.show();
    }

    public static void main(String[] args) {
        LoggerConfigurator.configureLogging();
        launch(args);
    }
}