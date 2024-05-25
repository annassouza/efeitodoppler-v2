package com.example.efeitodopplerv2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Pane root = FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/hello-view.fxml"));
        Scene scene = new Scene(root, 320, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

        stage.setTitle("DOPPLERMASTER");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
