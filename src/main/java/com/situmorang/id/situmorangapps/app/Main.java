package com.situmorang.id.situmorangapps.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main.java
public class Main extends Application {
    private static Stage primaryStage;

    public static void setRoot(String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        setRoot("/view/login.fxml");
        stage.setTitle("Situmorang Kasir Apps");
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

