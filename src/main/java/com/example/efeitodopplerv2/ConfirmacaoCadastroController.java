package com.example.efeitodopplerv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfirmacaoCadastroController {

    @FXML
    protected void onOkButtonClick(ActionEvent event) {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/hello-view.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de confirmação atual
            Stage confirmacaoStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            confirmacaoStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
