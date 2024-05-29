package com.example.efeitodopplerv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CadastroController {

    @FXML
    private TextField txtFieldUsuario;

    @FXML
    private TextField txtFieldSenha;

    @FXML
    protected void onFinalizarCadastroClick(ActionEvent event) {
        String usuario = txtFieldUsuario.getText();
        String senha = txtFieldSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            showAlert("Erro no Cadastro", "Por favor, preencha todos os campos.");
        } else if (validarUsuarioExistente(usuario)) {
            showAlert("Erro no Cadastro", "Usuário já cadastrado.");
        } else {
            cadastrarUsuario(usuario, senha);
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
        Connection connection = ConexaoBanco.getConnection();
        boolean isValid = false;

        if (connection != null) {
            try {
                // Chama a procedure SP_VALIDACAO_LOGIN
                String sql = "{? = call sp_validar_login(?, ?)}";
                CallableStatement callableStatement = connection.prepareCall(sql);

                // Configura os parâmetros de entrada e saída
                callableStatement.registerOutParameter(1, java.sql.Types.INTEGER);
                callableStatement.setString(2, usuario);
                callableStatement.setString(3, senha);

                // Executa a procedure
                callableStatement.execute();

                // Obtém o valor de retorno
                int result = callableStatement.getInt(1);
                isValid = (result == 1);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        }
        return isValid;
    }

    private boolean validarUsuarioExistente(String usuario) {
        Connection connection = ConexaoBanco.getConnection();
        boolean exists = false;

        if (connection != null) {
            try {
                String sql = "SELECT COUNT(*) FROM usuarios WHERE ip_login = ?";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setString(1, usuario);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    exists = resultSet.getInt(1) > 0;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        }
        return exists;
    }

    private void cadastrarUsuario(String usuario, String senha) {
        Connection connection = ConexaoBanco.getConnection();

        if (connection != null) {
            try {
                String sql = "INSERT INTO usuarios (ip_login, senha) VALUES (?, ?)";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setString(1, usuario);
                statement.setString(2, senha);

                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        }
    }

    public static void alterarSenha(String usuario, String novaSenha) {
        Connection connection = null;
        CallableStatement procuraCodUsuarioStmt = null;
        PreparedStatement updateSenhaStmt = null;
        CallableStatement auditoriaStmt = null;

        try {
            // Estabelece a conexão com o banco de dados
            connection = ConexaoBanco.getConnection();
            if (connection == null) {
                System.out.println("Não foi possível estabelecer a conexão com o banco de dados.");
                return;
            }

            // Verifica se o usuário existe e obtém o código do usuário
            String procuraCodUsuarioSql = "{call SP_PROCURAR_COD_USUARIO(?, ?)}";
            procuraCodUsuarioStmt = connection.prepareCall(procuraCodUsuarioSql);
            procuraCodUsuarioStmt.setString(1, usuario);
            procuraCodUsuarioStmt.registerOutParameter(2, java.sql.Types.INTEGER);
            procuraCodUsuarioStmt.execute();
            int codUsuario = procuraCodUsuarioStmt.getInt(2);

            System.out.println("Código do usuário retornado: " + codUsuario);

            if (codUsuario == -1) {
                // Usuário não encontrado, exibir mensagem de erro
                System.out.println("Usuário não cadastrado");
                return;
            }

            // Obter a senha antiga
            String senhaAntiga = null;
            String getSenhaSql = "SELECT senha FROM usuarios WHERE ip_login = ?";
            PreparedStatement getSenhaStmt = connection.prepareStatement(getSenhaSql);
            getSenhaStmt.setString(1, usuario);
            ResultSet rs = getSenhaStmt.executeQuery();
            if (rs.next()) {
                senhaAntiga = rs.getString("senha");
                System.out.println("Senha antiga obtida: " + senhaAntiga);
            }

            // Verifica se a nova senha é diferente da antiga
            if (senhaAntiga != null && senhaAntiga.equals(novaSenha)) {
                System.out.println("Senha igual a antiga. Utilize uma senha diferente.");
                return;
            }

            // Atualizar a senha do usuário
            String updateSenhaSql = "UPDATE usuarios SET senha = ? WHERE ip_login = ?";
            updateSenhaStmt = connection.prepareStatement(updateSenhaSql);
            updateSenhaStmt.setString(1, novaSenha);
            updateSenhaStmt.setString(2, usuario);
            updateSenhaStmt.executeUpdate();
            System.out.println("Senha atualizada no banco de dados");

            // Registrar a alteração na tabela auditoria_senha
            if (senhaAntiga != null) {
                String auditoriaSql = "{call sp_inserir_na_tabela_auditoria(?, ?, ?)}";
                auditoriaStmt = connection.prepareCall(auditoriaSql);
                auditoriaStmt.setString(1, senhaAntiga);
                auditoriaStmt.setString(2, novaSenha);
                auditoriaStmt.setInt(3, codUsuario);
                auditoriaStmt.executeUpdate();
                System.out.println("Mudança registrada na auditoria_senha");
            }

            System.out.println("Senha atualizada com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fechar todos os recursos em ordem reversa de sua abertura
            if (auditoriaStmt != null) {
                try {
                    auditoriaStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (updateSenhaStmt != null) {
                try {
                    updateSenhaStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (procuraCodUsuarioStmt != null) {
                try {
                    procuraCodUsuarioStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Conexão fechada com sucesso");
        }
    }

    private static int getCodUsuario(String usuario) {
        Connection connection = ConexaoBanco.getConnection();
        int codUsuario = -1;

        if (connection != null) {
            try {
                String sql = "SELECT cod_usuario FROM usuarios WHERE ip_login = ?";
                CallableStatement statement = connection.prepareCall(sql);
                statement.setString(1, usuario);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    codUsuario = resultSet.getInt("cod_usuario");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConexaoBanco.closeConnection(connection);
            }
        }
        return codUsuario;
    }
}
