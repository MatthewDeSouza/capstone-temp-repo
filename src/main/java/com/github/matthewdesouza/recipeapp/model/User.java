package com.github.matthewdesouza.recipeapp.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
    private Set<Integer> likedRecipes;
    private int id;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.likedRecipes = new HashSet<>();
    }

    public User() {
        this(null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Integer> getLikedRecipes() {
        return likedRecipes;
    }

    public void setLikedRecipes(Set<Integer> likedRecipes) {
        this.likedRecipes = likedRecipes;
    }

    public void addLikedRecipe(int recipeId) {
        this.likedRecipes.add(recipeId);
    }

    public void removeLikedRecipe(int recipeId) {
        this.likedRecipes.remove(recipeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
