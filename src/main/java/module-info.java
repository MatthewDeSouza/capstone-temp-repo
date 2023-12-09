module com.github.matthewdesouza.recipeapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mariadb.java.client;
    requires jbcrypt;

    requires org.slf4j;
    requires logback.classic;
    requires atlantafx.base;
                        
    opens com.github.matthewdesouza.recipeapp to javafx.fxml;
    opens com.github.matthewdesouza.recipeapp.view to javafx.fxml;
    exports com.github.matthewdesouza.recipeapp;
    exports com.github.matthewdesouza.recipeapp.model;
    exports com.github.matthewdesouza.recipeapp.view;
    exports com.github.matthewdesouza.recipeapp.database;
    exports com.github.matthewdesouza.recipeapp.database.exception;
}