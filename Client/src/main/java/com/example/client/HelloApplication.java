package com.example.client;

import javafx.application.Application;
import com.example.client.modules.Structures;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
      Structures.change_scene("Login.fxml", stage, getClass());
    }

    public static void main(String[] args) {
        launch();
    }
}