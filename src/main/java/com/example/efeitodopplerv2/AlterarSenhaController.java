package com.example.efeitodopplerv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AlterarSenhaController {

    @FXML
    private TextField txtFieldUsuario;

    @FXML
    private TextField txtFieldNovaSenha;

    @FXML
    protected void onAlterarSenhaClick(ActionEvent event) {
        String usuario = txtFieldUsuario.getText();
        String novaSenha = txtFieldNovaSenha.getText();

        if (usuario.isEmpty() || novaSenha.isEmpty()) {
            showAlert("Erro", "Por favor, preencha todos os campos.");
        } else if (!CadastroController.validarLogin(usuario, novaSenha)) {
            showAlert("Erro", "Usuário não encontrado.");
        } else {
            CadastroController.alterarSenha(usuario, novaSenha); // Altera a senha
            showAlert("Sucesso", "Senha alterada com sucesso!");
            voltarParaLogin();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void voltarParaLogin() {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/hello-view.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de alteração de senha atual
            Stage alterarSenhaStage = (Stage) txtFieldUsuario.getScene().getWindow();
            alterarSenhaStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onVoltarButtonClick(ActionEvent event) {
        voltarParaLogin();
    }
}
