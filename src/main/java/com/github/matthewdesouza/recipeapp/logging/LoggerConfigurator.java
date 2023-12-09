package com.github.matthewdesouza.recipeapp.logging;

import java.io.File;

public class LoggerConfigurator {
    public static void configureLogging() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            // Windows
            System.setProperty("LOGS_HOME", userHome + "\\AppData\\Roaming\\recipe-app\\logs");
        } else if (os.contains("mac")) {
            // macOS
            System.setProperty("LOGS_HOME", userHome + "/Library/Application Support/recipe-app/logs");
        } else {
            // Assume Linux/Unix
            System.setProperty("LOGS_HOME", userHome + "/.config/recipe-app/logs");
        }
        File logDir = new File(System.getProperty("LOGS_HOME"));
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }
}