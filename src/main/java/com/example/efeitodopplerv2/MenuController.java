package com.example.efeitodopplerv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    protected void onHistoricoButtonClick(ActionEvent event) {
        // Implemente a lógica para abrir a tela de histórico
    }

    @FXML
    protected void onNovaSimulacaoButtonClick(ActionEvent event) {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/simulador-doppler.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Simulador Doppler");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de menu atual
            Stage menuStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            menuStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
