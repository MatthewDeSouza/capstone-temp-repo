package com.github.matthewdesouza.recipeapp.model;

import java.util.Objects;

public class Recipe {
    private int id;
    private String title;
    private String content;
    private String imageUri;
    private int userId;

    public Recipe() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return id == recipe.id && userId == recipe.userId && Objects.equals(title, recipe.title) && Objects.equals(content, recipe.content) && Objects.equals(imageUri, recipe.imageUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, imageUri, userId);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", userId=" + userId +
                '}';
    }
}
