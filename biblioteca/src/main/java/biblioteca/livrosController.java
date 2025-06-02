package biblioteca;

import biblioteca.App;
import biblioteca.model.Autor; // Ainda necessário para getAutorById (se for usado em outro lugar)
import biblioteca.model.Livro;
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
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class livrosController {

    @FXML
    private TextField anodepublicacaoField;

    @FXML
    private TextField autorField; // Este campo agora é apenas um texto livre para o nome do autor

    @FXML
    private TableColumn<Livro, String> autorTableCollumn; // Coluna para exibir o nome do autor

    @FXML
    private Button btnAtualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnInserir;

    @FXML
    private Button btnVoltar;

    @FXML
    private TextField editoraField; // Este campo é para o 'genero' do Livro

    @FXML
    private TableColumn<Livro, String> editoraTableColumn; // Coluna para 'genero'

    @FXML
    private TableColumn<Livro, Integer> publicacaoTableCollumn;

    @FXML
    private TableView<Livro> tableGerenciarLivros;

    @FXML
    private TableColumn<Livro, String> tituloTableCollumn;

    @FXML
    private TextField tituloeField;

    @FXML
    private Label totalLivrosLabel;

    @FXML
    private TextField ISBNField;
    @FXML
    private TableColumn<Livro, String> ISBNTableColumn;

    @FXML
    private TextField disponivelField;
    @FXML
    private TableColumn<Livro, Integer> disponivelTableColumn;

    private SistemaBiblioteca sistemaBiblioteca;
    private ObservableList<Livro> livrosData;

    public livrosController() {
        sistemaBiblioteca = new SistemaBiblioteca();
    }

    @FXML
    public void initialize() {
        App.debugLog("livrosController inicializado.");

        if (sistemaBiblioteca.getConnection() == null) {
            showAlert(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível conectar ao banco de dados.");
            totalLivrosLabel.setText("Erro: Conexão com BD falhou.");
            return;
        }

        // Configurar as colunas da tabela
        tituloTableCollumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        autorTableCollumn.setCellValueFactory(new PropertyValueFactory<>("nomeAutorLivro")); // AGORA USA
                                                                                             // 'nomeAutorLivro'
        editoraTableColumn.setCellValueFactory(new PropertyValueFactory<>("genero"));
        publicacaoTableCollumn.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));
        ISBNTableColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        disponivelTableColumn.setCellValueFactory(new PropertyValueFactory<>("disponivel"));

        livrosData = FXCollections.observableArrayList();
        tableGerenciarLivros.setItems(livrosData);

        loadLivrosData();

        tableGerenciarLivros.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showLivroDetails(newValue));

        btnInserir.setOnAction(this::handleInserirLivro);
        btnAtualizar.setOnAction(this::handleAtualizarLivro);
        btnEliminar.setOnAction(this::handleEliminarLivro);
        btnVoltar.setOnAction(this::handleVoltar);
    }

    private void loadLivrosData() {
        App.debugLog("livrosController: Carregando dados dos livros.");
        List<Livro> livros = sistemaBiblioteca.getAllLivros();
        if (livros != null) {
            livrosData.setAll(livros);
            totalLivrosLabel.setText("Total Livros: " + livros.size());
            App.debugLog("livrosController: " + livros.size() + " livros carregados.");
        } else {
            livrosData.clear();
            totalLivrosLabel.setText("Total Livros: 0");
            App.debugLog("livrosController: Nenhum livro encontrado ou erro ao carregar.");
        }
        clearFields();
    }

    private void showLivroDetails(Livro livro) {
        if (livro != null) {
            tituloeField.setText(livro.getTitulo());
            ISBNField.setText(livro.getISBN());
            autorField.setText(livro.getNomeAutorLivro()); // AGORA USA 'nomeAutorLivro'
            editoraField.setText(livro.getGenero());
            anodepublicacaoField.setText(String.valueOf(livro.getAnoPublicacao()));
            disponivelField.setText(String.valueOf(livro.getDisponivel()));
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        tituloeField.setText("");
        autorField.setText("");
        editoraField.setText("");
        anodepublicacaoField.setText("");
        ISBNField.setText("");
        disponivelField.setText("");
        tableGerenciarLivros.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleInserirLivro(ActionEvent event) {
        App.debugLog("Botão Inserir Livro clicado.");
        String titulo = tituloeField.getText();
        String ISBN = ISBNField.getText();
        String nomeAutor = autorField.getText(); // Nome do autor como texto livre
        String genero = editoraField.getText();
        String anoPublicacaoStr = anodepublicacaoField.getText();
        String disponivelStr = disponivelField.getText();

        if (titulo.isEmpty() || ISBN.isEmpty() || nomeAutor.isEmpty() || genero.isEmpty() || anoPublicacaoStr.isEmpty()
                || disponivelStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            int anoPublicacao = Integer.parseInt(anoPublicacaoStr);
            int disponivel = Integer.parseInt(disponivelStr);

            // NÃO HÁ VALIDAÇÃO DE AUTOR AQUI, APENAS PEGA O TEXTO
            Livro novoLivro = new Livro(0, titulo, ISBN, genero, anoPublicacao, disponivel, nomeAutor); // Passa o nome
                                                                                                        // do autor
            if (sistemaBiblioteca.addLivro(novoLivro)) { // Método addLivro agora aceita apenas o objeto Livro
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro adicionado com sucesso!");
                loadLivrosData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao adicionar livro. Verifique o log de depuração.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Entrada Inválida",
                    "Ano de Publicação e Disponibilidade devem ser números válidos.");
        }
    }

    @FXML
    private void handleAtualizarLivro(ActionEvent event) {
        App.debugLog("Botão Atualizar Livro clicado.");
        Livro selectedLivro = tableGerenciarLivros.getSelectionModel().getSelectedItem();
        if (selectedLivro == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção",
                    "Por favor, selecione um livro na tabela para atualizar.");
            return;
        }

        String titulo = tituloeField.getText();
        String ISBN = ISBNField.getText();
        String nomeAutor = autorField.getText(); // Nome do autor como texto livre
        String genero = editoraField.getText();
        String anoPublicacaoStr = anodepublicacaoField.getText();
        String disponivelStr = disponivelField.getText();

        if (titulo.isEmpty() || ISBN.isEmpty() || nomeAutor.isEmpty() || genero.isEmpty() || anoPublicacaoStr.isEmpty()
                || disponivelStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            int anoPublicacao = Integer.parseInt(anoPublicacaoStr);
            int disponivel = Integer.parseInt(disponivelStr);

            // NÃO HÁ VALIDAÇÃO DE AUTOR AQUI, APENAS PEGA O TEXTO
            selectedLivro.setTitulo(titulo);
            selectedLivro.setISBN(ISBN);
            selectedLivro.setGenero(genero);
            selectedLivro.setAnoPublicacao(anoPublicacao);
            selectedLivro.setDisponivel(disponivel);
            selectedLivro.setNomeAutorLivro(nomeAutor); // Atualiza o nome do autor no objeto

            if (sistemaBiblioteca.updateLivro(selectedLivro)) { // Método updateLivro agora aceita apenas o objeto Livro
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro atualizado com sucesso!");
                loadLivrosData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao atualizar livro. Verifique o log de depuração.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Entrada Inválida",
                    "Ano de Publicação e Disponibilidade devem ser números válidos.");
        }
    }

    @FXML
    private void handleEliminarLivro(ActionEvent event) {
        App.debugLog("Botão Eliminar Livro clicado.");
        Livro selectedLivro = tableGerenciarLivros.getSelectionModel().getSelectedItem();
        if (selectedLivro == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção",
                    "Por favor, selecione um livro na tabela para eliminar.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirmação de Exclusão",
                "Tem certeza que deseja eliminar o livro '" + selectedLivro.getTitulo() + "'?",
                "Esta ação não pode ser desfeita."); // Não menciona associação de autor, pois não é mais gerenciada
                                                     // aqui

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (sistemaBiblioteca.deleteLivro(selectedLivro.getIdLivro())) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro eliminado com sucesso!");
                loadLivrosData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao eliminar livro. Verifique o log de depuração.");
            }
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        App.debugLog("Botão 'Voltar' clicado na tela de Livros! Retornando à tela principal.");
        try {
            App.setRoot("secondary");
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
