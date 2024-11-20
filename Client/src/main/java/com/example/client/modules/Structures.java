package com.example.client.modules;

import com.example.client.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Structures {
    //Classe di supporto per la definizione di costanti, import e funzioni

    public static final int SCENE_WIDTH = 500;
    public static final int SCENE_HEIGHT = 500;

    public static <T> T change_scene(String new_scene_fxml, Stage current_stage,  Class<?> current_class){
        //Metodo utilizzato per cambiare scena e ritorna l'istanza del nuovo controller
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(current_class.getResource(new_scene_fxml));
            Scene scene = new Scene(fxmlLoader.load(),Structures.SCENE_WIDTH , Structures.SCENE_HEIGHT);
            current_stage.setScene(scene);
            current_stage.show();
            // Ottieni il controller della scena appena caricata
            T controller = fxmlLoader.getController();

            return controller;

        } catch (IOException e){
            System.out.println("Exeption caught while changing scene");
            return null;
        }
    }


}
