package com.example.efeitodopplerv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CadastroController {

    @FXML
    private TextField txtFieldUsuario;

    @FXML
    private TextField txtFieldSenha;

    private static final Map<String, String> usuarios = new HashMap<>();

    @FXML
    protected void onFinalizarCadastroClick(ActionEvent event) {
        String usuario = txtFieldUsuario.getText();
        String senha = txtFieldSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            showAlert("Erro no Cadastro", "Por favor, preencha todos os campos.");
        } else if (usuarios.containsKey(usuario)) {
            showAlert("Erro no Cadastro", "Usuário já cadastrado.");
        } else {
            usuarios.put(usuario, senha);
            exibirConfirmacaoCadastro();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void exibirConfirmacaoCadastro() {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/confirmacao-cadastro.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Cadastro Finalizado");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onVoltarButtonClick(ActionEvent event) {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/hello-view.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de cadastro atual
            Stage cadastroStage = (Stage) txtFieldUsuario.getScene().getWindow();
            cadastroStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validarLogin(String usuario, String senha) {
        return usuarios.containsKey(usuario) && usuarios.get(usuario).equals(senha);
    }

    public static void alterarSenha(String usuario, String novaSenha) {
        if (usuarios.containsKey(usuario)) {
            usuarios.put(usuario, novaSenha);
        }
    }
}
