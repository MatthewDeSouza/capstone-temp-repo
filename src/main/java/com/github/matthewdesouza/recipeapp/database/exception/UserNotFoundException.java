package com.github.matthewdesouza.recipeapp.database.exception;

public class UserNotFoundException extends Exception {
    private String message;

    public UserNotFoundException(String messsage) {
        super(messsage);
    }
}
