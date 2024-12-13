package com.example.client;

import javafx.application.Application;
import com.example.shared.Structures;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Structures.change_scene(stage, new FXMLLoader(HelloApplication.class.getResource("Login.fxml")));
        stage.setTitle("Mail Client");
    }

    public static void main(String[] args) {
        launch();
    }
}