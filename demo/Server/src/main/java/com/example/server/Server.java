package com.example.server;

import com.example.shared.Structures;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Server extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("ServerDashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Structures.SCENE_WIDTH, Structures.SCENE_HEIGHT);
        stage.setTitle("Mail Server");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}