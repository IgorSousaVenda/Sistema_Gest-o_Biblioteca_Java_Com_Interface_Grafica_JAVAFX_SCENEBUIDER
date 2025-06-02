package biblioteca;

import biblioteca.App;
import biblioteca.model.Leitor;
import biblioteca.service.SistemaBiblioteca;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.io.IOException; // Importar IOException
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class leitorController {

    @FXML
    private Button btnAtualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnInserir;

    @FXML
    private TextField emailField;

    @FXML
    private TableColumn<Leitor, String> emailLeitorTableCollumn;

    @FXML
    private TextField endercoField;

    @FXML
    private TableColumn<Leitor, String> endercoLeitorTableCollumn;

    @FXML
    private TextField nomeField;

    @FXML
    private TableColumn<Leitor, String> nomeLeitorTableCollumn;

    @FXML
    private TableView<Leitor> tableGerenciarLeitor;

    @FXML
    private TextField telefoneField;

    @FXML
    private TableColumn<Leitor, String> telefoneLeitorTableColumn;

    @FXML
    private Label totalLivrosLeitorLabel;

    @FXML
    private Button btnVoltar; // NOVO: Botão Voltar

    private SistemaBiblioteca sistemaBiblioteca;
    private ObservableList<Leitor> leitoresData;

    public leitorController() {
        sistemaBiblioteca = new SistemaBiblioteca();
    }

    @FXML
    public void initialize() {
        App.debugLog("leitorController inicializado.");

        if (sistemaBiblioteca.getConnection() == null) {
            showAlert(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível conectar ao banco de dados.");
            totalLivrosLeitorLabel.setText("Erro: Conexão com BD falhou.");
            return;
        }

        nomeLeitorTableCollumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        emailLeitorTableCollumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefoneLeitorTableColumn.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        endercoLeitorTableCollumn.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        leitoresData = FXCollections.observableArrayList();
        tableGerenciarLeitor.setItems(leitoresData);

        loadLeitoresData();

        tableGerenciarLeitor.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showLeitorDetails(newValue));

        btnInserir.setOnAction(this::handleInserirLeitor);
        btnAtualizar.setOnAction(this::handleAtualizarLeitor);
        btnEliminar.setOnAction(this::handleEliminarLeitor);
        btnVoltar.setOnAction(this::handleVoltar); // NOVO: Configurar ação do botão Voltar
    }

    private void loadLeitoresData() {
        App.debugLog("leitorController: Carregando dados dos leitores.");
        List<Leitor> leitores = sistemaBiblioteca.getAllLeitores();
        if (leitores != null) {
            leitoresData.setAll(leitores);
            totalLivrosLeitorLabel.setText("Total Leitores: " + leitores.size());
            App.debugLog("leitorController: " + leitores.size() + " leitores carregados.");
        } else {
            leitoresData.clear();
            totalLivrosLeitorLabel.setText("Total Leitores: 0");
            App.debugLog("leitorController: Nenhum leitor encontrado ou erro ao carregar.");
        }
        clearFields();
    }

    private void showLeitorDetails(Leitor leitor) {
        if (leitor != null) {
            nomeField.setText(leitor.getNome());
            emailField.setText(leitor.getEmail());
            telefoneField.setText(leitor.getTelefone());
            endercoField.setText(leitor.getEndereco());
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        nomeField.setText("");
        emailField.setText("");
        telefoneField.setText("");
        endercoField.setText("");
        tableGerenciarLeitor.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleInserirLeitor(ActionEvent event) {
        App.debugLog("Botão Inserir Leitor clicado.");
        String nome = nomeField.getText();
        String endereco = endercoField.getText();
        String telefone = telefoneField.getText();
        String email = emailField.getText();

        if (nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

        Leitor novoLeitor = new Leitor(0, nome, endereco, telefone, email);
        if (sistemaBiblioteca.addLeitor(novoLeitor)) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Leitor adicionado com sucesso!");
            loadLeitoresData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao adicionar leitor. Verifique o log de depuração.");
        }
    }

    @FXML
    private void handleAtualizarLeitor(ActionEvent event) {
        App.debugLog("Botão Atualizar Leitor clicado.");
        Leitor selectedLeitor = tableGerenciarLeitor.getSelectionModel().getSelectedItem();
        if (selectedLeitor == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção",
                    "Por favor, selecione um leitor na tabela para atualizar.");
            return;
        }

        String nome = nomeField.getText();
        String endereco = endercoField.getText();
        String telefone = telefoneField.getText();
        String email = emailField.getText();

        if (nome.isEmpty() || endereco.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

        selectedLeitor.setNome(nome);
        selectedLeitor.setEndereco(endereco);
        selectedLeitor.setTelefone(telefone);
        selectedLeitor.setEmail(email);

        if (sistemaBiblioteca.updateLeitor(selectedLeitor)) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Leitor atualizado com sucesso!");
            loadLeitoresData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao atualizar leitor. Verifique o log de depuração.");
        }
    }

    @FXML
    private void handleEliminarLeitor(ActionEvent event) {
        App.debugLog("Botão Eliminar Leitor clicado.");
        Leitor selectedLeitor = tableGerenciarLeitor.getSelectionModel().getSelectedItem();
        if (selectedLeitor == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção",
                    "Por favor, selecione um leitor na tabela para eliminar.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirmação de Exclusão",
                "Tem certeza que deseja eliminar o leitor '" + selectedLeitor.getNome() + "'?",
                "Esta ação não pode ser desfeita.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (sistemaBiblioteca.deleteLeitor(selectedLeitor.getIdLeitor())) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Leitor eliminado com sucesso!");
                loadLeitoresData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao eliminar leitor. Verifique o log de depuração.");
            }
        }
    }

    // NOVO MÉTODO: Lida com a ação do botão "Voltar"
    @FXML
    private void handleVoltar(ActionEvent event) {
        App.debugLog("Botão 'Voltar' clicado! Retornando à tela principal.");
        try {
            App.setRoot("secondary"); // Carrega o FXML da tela secundária
        } catch (IOException e) {
            App.debugLog("Erro ao retornar à tela principal: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Carregamento", "Não foi possível retornar à tela principal.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}
