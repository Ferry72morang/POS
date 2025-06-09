package com.situmorang.id.situmorangapps.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppNavigator {
    private static Stage mainStage;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(AppNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();
            mainStage.setScene(new Scene(root));
            mainStage.setTitle(title);
            mainStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
