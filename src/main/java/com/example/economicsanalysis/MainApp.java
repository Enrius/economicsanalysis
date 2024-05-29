package com.example.economicsanalysis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root, 1080, 600); // Set the preferred window size here
            primaryStage.setScene(scene);
            primaryStage.setTitle("Анализ экономических показателей");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
