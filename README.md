# Recipe App

## Description
RecipeApp is a JavaFX-based desktop application that allows users to view, create, edit, and delete recipes. It's designed with a modern and user-friendly interface, providing an intuitive experience for managing a collection of recipes. The application uses a local database for data persistence and supports user authentication for personalized access.

## Features
- **User Authentication**: Secure login and registration system.
- **Recipe Management**: Users can add new recipes, edit existing ones, and delete them.
- **Image Support**: Attach images to recipes for better visualization.
- **Search Functionality**: Easily find recipes with a built-in search feature.
- **Responsive UI**: Modern and intuitive user interface.

## Installation
To run the application, you'll need to have Java and JavaFX set up on your system.

### Prerequisites
- Java JDK 21 or higher
- JavaFX SDK

### Setup
1. Clone the repository:

```bash
git clone https://github.com/MatthewDeSouza/recipe-app.git
```

2. Navigate to the project directory:

```bash
cd recipe-app
```

3. Build the JAR

```bash
mvn clean compile assembly:single
```

4. Execute the JAR

```bash
java -jar target/[artifactId]-[version]-jar-with-dependencies.jar
```



