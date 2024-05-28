package com.example.efeitodopplerv2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    private static Connection connection;

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/hello-view.fxml"));
        Scene scene = new Scene(root, 320, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

        stage.setTitle("DOPPLERMASTER");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        // Estabelece a conexão com o banco de dados
        connection = ConexaoBanco.getConnection();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Fecha a conexão com o banco de dados
        ConexaoBanco.closeConnection(connection);
    }

    public static void main(String[] args) {
        launch();
    }
}
