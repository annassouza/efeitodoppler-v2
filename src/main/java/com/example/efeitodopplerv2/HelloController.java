package com.example.efeitodopplerv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private TextField txtFieldUsuario;

    @FXML
    private TextField txtFieldSenha;

    @FXML
    private Label errorLabel;

    @FXML
    protected void onEntrarButtonClick(ActionEvent event) {
        String usuario = txtFieldUsuario.getText();
        String senha = txtFieldSenha.getText();

        // Log para verificar os valores de usuario e senha
        System.out.println("Tentando fazer login com usuario: " + usuario + " e senha: " + senha);

        if (CadastroController.validarLogin(usuario, senha)) {
            errorLabel.setText("");
            abrirMenu();
        } else {
            errorLabel.setText("Usuário ou senha incorreta");
        }
    }

    private void abrirMenu() {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/menu.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Menu Principal");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de login atual
            Stage loginStage = (Stage) txtFieldUsuario.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void OnAindaNaoSouCadastradoClick(ActionEvent event) {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/cadastro-view.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Cadastro");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de login atual
            Stage loginStage = (Stage) txtFieldUsuario.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onEsqueciMinhaSenhaClick(ActionEvent event) {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/efeitodopplerv2/alterar-senha.fxml")));
            scene.getStylesheets().add(getClass().getResource("/com/example/efeitodopplerv2/Estilos/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Alterar Senha");
            stage.setScene(scene);
            stage.show();

            // Fechar a janela de login atual
            Stage loginStage = (Stage) txtFieldUsuario.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
