package com.example.efeitodopplerv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SimuladorController {

    @FXML
    private TextField txtFieldDistancia;

    @FXML
    private TextField txtFieldFrequencia;

    @FXML
    private TextField txtFieldPotencia;

    @FXML
    private TextField txtFieldVelocidadeRelativa;

    @FXML
    protected void onEnviarButtonClick(ActionEvent event) {
        try {
            double distancia = Double.parseDouble(txtFieldDistancia.getText());
            double frequencia = Double.parseDouble(txtFieldFrequencia.getText());
            double potencia = Double.parseDouble(txtFieldPotencia.getText());
            double velocidadeRelativa = Double.parseDouble(txtFieldVelocidadeRelativa.getText());

            SimuladorCalculos simulador = new SimuladorCalculos();
            simulador.Simulador(distancia, frequencia, potencia, velocidadeRelativa);
            simulador.simular();

            abrirResultados();
        } catch (NumberFormatException e) {
            showAlert("Erro de Entrada", "Por favor, insira valores numéricos válidos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void abrirResultados() {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/resultados.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Resultados");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de simulação atual
            Stage simulacaoStage = (Stage) txtFieldDistancia.getScene().getWindow();
            simulacaoStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLimparButtonClick() {
        txtFieldDistancia.clear();
        txtFieldFrequencia.clear();
        txtFieldPotencia.clear();
        txtFieldVelocidadeRelativa.clear();
    }
}
