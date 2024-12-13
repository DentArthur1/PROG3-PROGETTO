package com.example.server;

import com.example.shared.Structures;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Main class for server */

/**
 * Metodo di avvio dell'applicazione.
 * @throws IOException Se si verifica un errore durante il caricamento della scena.
 */

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ServerDashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Structures.SCENE_WIDTH, Structures.SCENE_HEIGHT);
        stage.setTitle("Mail Server");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}