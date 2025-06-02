package biblioteca;

import biblioteca.service.SistemaBiblioteca;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent; // Adicionado para lidar com eventos de botão
import javafx.stage.Stage; // Adicionado para fechar a janela

import java.io.IOException;

public class PrimaryController {

    @FXML
    private Button buttonMenu;

    @FXML
    private CheckBox checkBoxMenu;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private AnchorPane paneMenu;

    @FXML
    private AnchorPane paneMenuLogin;

    @FXML
    private PasswordField passwordField;

    @FXML
    private StackPane stackPaneMenu;

    @FXML
    private TextField usernameField;

    private SistemaBiblioteca sistemaBiblioteca;

    public PrimaryController() {
        sistemaBiblioteca = new SistemaBiblioteca();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) { // Este é o método que deve ser chamado pelo FXML
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (sistemaBiblioteca.login(username, password)) { // Chamando o método 'login'
            try {
                // Fechar a janela de login

                // Abrir a SecondaryView (menu principal)
                App.setRoot("secondary");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Erro de Carregamento", "Não foi possível carregar a tela principal.");
            }
        } else {
            showAlert(AlertType.ERROR, "Erro de Login", "Credenciais inválidas. Tente novamente.");
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para fechar a conexão do banco de dados quando o aplicativo for
    // fechado
    // Isso pode ser chamado na App.java ou em um método handleSair da
    // SecondaryController
    public void closeDatabaseConnection() {
        if (sistemaBiblioteca != null) {
            sistemaBiblioteca.closeConnection();
        }
    }
}