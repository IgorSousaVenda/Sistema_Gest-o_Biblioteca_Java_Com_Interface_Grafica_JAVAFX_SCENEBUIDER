package biblioteca;

import biblioteca.App;
import biblioteca.model.Emprestimo;
import biblioteca.model.Leitor;
import biblioteca.model.Livro;
import biblioteca.model.Exemplar;
import biblioteca.service.SistemaBiblioteca;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox; // Importar ComboBox
import javafx.util.StringConverter; // Importar StringConverter

public class emprestimosController {

    @FXML
    private Button btnAtualizar; // Será usado para "Devolver"

    @FXML
    private Button btnEliminar; // Será usado para "Excluir"

    @FXML
    private Button btnInserir; // Será usado para "Emprestar"

    @FXML
    private Button btnVoltar;

    @FXML
    private TextField dataDevolucaoField; // Data de Devolução Prevista

    @FXML
    private TableColumn<Emprestimo, LocalDate> dataDevolucaoTableCollumn;

    @FXML
    private TextField dataEmprestimoField;

    @FXML
    private TableColumn<Emprestimo, LocalDate> dataEmprestimoTableCollumn;

    @FXML
    private TableColumn<Emprestimo, String> emailTableCollumn;

    @FXML
    private TableColumn<Emprestimo, String> livroEmprestadoTableCollumn;

    @FXML
    private TableColumn<Emprestimo, String> nomeLeitorTableCollumn;

    @FXML
    private TableView<Emprestimo> tableGerenciarEmprestimos;

    @FXML
    private TableColumn<Emprestimo, String> telefoneTableColumn;

    @FXML
    private TableColumn<Emprestimo, LocalDate> dataDevolucaoRealTableCollumn;

    // NOVOS: ComboBoxes
    @FXML
    private ComboBox<Leitor> leitorComboBox;
    @FXML
    private ComboBox<Livro> livroComboBox;

    private SistemaBiblioteca sistemaBiblioteca;
    private ObservableList<Emprestimo> emprestimosData;
    private ObservableList<Leitor> leitoresList;
    private ObservableList<Livro> livrosDisponiveisList;

    private Exemplar selectedExemplarForEmprestimo; // Ainda necessário para o ID do exemplar

    public emprestimosController() {
        sistemaBiblioteca = new SistemaBiblioteca();
    }

    @FXML
    public void initialize() {
        App.debugLog("emprestimosController inicializado.");

        if (sistemaBiblioteca.getConnection() == null) {
            showAlert(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível conectar ao banco de dados.");
            return;
        }

        // Configurar as colunas da tabela
        nomeLeitorTableCollumn.setCellValueFactory(new PropertyValueFactory<>("nomeLeitor"));
        telefoneTableColumn.setCellValueFactory(new PropertyValueFactory<>("telefoneLeitor"));
        emailTableCollumn.setCellValueFactory(new PropertyValueFactory<>("emailLeitor"));
        livroEmprestadoTableCollumn.setCellValueFactory(new PropertyValueFactory<>("tituloLivro"));
        dataEmprestimoTableCollumn.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        dataDevolucaoTableCollumn.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoPrevista"));
        dataDevolucaoRealTableCollumn.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoReal"));

        emprestimosData = FXCollections.observableArrayList();
        tableGerenciarEmprestimos.setItems(emprestimosData);

        // Inicializar e configurar ComboBoxes
        leitoresList = FXCollections.observableArrayList();
        leitorComboBox.setItems(leitoresList);
        leitorComboBox.setConverter(new StringConverter<Leitor>() {
            @Override
            public String toString(Leitor leitor) {
                return leitor != null ? leitor.getNome() : "";
            }

            @Override
            public Leitor fromString(String string) {
                // Não precisamos de fromString para este caso de uso simples
                return null;
            }
        });

        livrosDisponiveisList = FXCollections.observableArrayList();
        livroComboBox.setItems(livrosDisponiveisList);
        livroComboBox.setConverter(new StringConverter<Livro>() {
            @Override
            public String toString(Livro livro) {
                return livro != null ? livro.getTitulo() + " (ISBN: " + livro.getISBN() + ")" : "";
            }

            @Override
            public Livro fromString(String string) {
                // Não precisamos de fromString para este caso de uso simples
                return null;
            }
        });

        // Listener para seleção de livro na ComboBox (para encontrar exemplar
        // disponível)
        livroComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedExemplarForEmprestimo = sistemaBiblioteca.getAvailableExemplarByLivroId(newVal.getIdLivro());
                if (selectedExemplarForEmprestimo == null) {
                    showAlert(Alert.AlertType.WARNING, "Exemplar Indisponível",
                            "Não há exemplares disponíveis para o livro '" + newVal.getTitulo() + "'.");
                    // Opcional: desabilitar o botão de empréstimo ou limpar seleção do livro
                } else {
                    App.debugLog("Exemplar disponível para '" + newVal.getTitulo() + "': ID "
                            + selectedExemplarForEmprestimo.getIdExemplar());
                }
            } else {
                selectedExemplarForEmprestimo = null;
            }
        });

        loadComboBoxData(); // Carrega dados para as ComboBoxes
        loadEmprestimosData(); // Carrega dados para a tabela de empréstimos

        // Listener para seleção de linha na tabela de empréstimos
        tableGerenciarEmprestimos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEmprestimoDetails(newValue));

        // Configurar ações dos botões
        btnInserir.setOnAction(this::handleEmprestarLivro);
        btnAtualizar.setOnAction(this::handleDevolverLivro);
        btnEliminar.setOnAction(this::handleExcluirEmprestimo);
        btnVoltar.setOnAction(this::handleVoltar);
    }

    private void loadComboBoxData() {
        App.debugLog("emprestimosController: Carregando dados para ComboBoxes.");
        // Carrega leitores
        List<Leitor> todosLeitores = sistemaBiblioteca.getAllLeitores();
        if (todosLeitores != null) {
            leitoresList.setAll(todosLeitores);
        } else {
            leitoresList.clear();
        }

        // Carrega livros (todos os livros, a verificação de disponibilidade será feita
        // ao selecionar)
        List<Livro> todosLivros = sistemaBiblioteca.getAllLivros();
        if (todosLivros != null) {
            livrosDisponiveisList.setAll(todosLivros);
        } else {
            livrosDisponiveisList.clear();
        }
    }

    private void loadEmprestimosData() {
        App.debugLog("emprestimosController: Carregando dados dos empréstimos.");
        List<Emprestimo> emprestimos = sistemaBiblioteca.getAllEmprestimosComDetalhes();
        if (emprestimos != null) {
            emprestimosData.setAll(emprestimos);
            App.debugLog("emprestimosController: " + emprestimos.size() + " empréstimos carregados.");
        } else {
            emprestimosData.clear();
            App.debugLog("emprestimosController: Nenhum empréstimo encontrado ou erro ao carregar.");
        }
        clearFields();
    }

    private void showEmprestimoDetails(Emprestimo emprestimo) {
        if (emprestimo != null) {
            // Seleciona o leitor na ComboBox
            leitorComboBox.getSelectionModel().select(
                    leitoresList.stream()
                            .filter(l -> l.getIdLeitor() == emprestimo.getIdLeitor())
                            .findFirst()
                            .orElse(null));

            // Seleciona o livro na ComboBox
            // Note: Aqui estamos selecionando o livro pelo ID do exemplar.
            // Precisamos do ID do livro associado ao exemplar para selecionar o livro na
            // ComboBox.
            Exemplar exemplarAssociado = sistemaBiblioteca.getExemplarById(emprestimo.getIdExemplar());
            if (exemplarAssociado != null) {
                livroComboBox.getSelectionModel().select(
                        livrosDisponiveisList.stream()
                                .filter(liv -> liv.getIdLivro() == exemplarAssociado.getIdLivro())
                                .findFirst()
                                .orElse(null));
            } else {
                livroComboBox.getSelectionModel().clearSelection();
            }

            dataEmprestimoField
                    .setText(emprestimo.getDataEmprestimo() != null ? emprestimo.getDataEmprestimo().toString() : "");
            dataDevolucaoField.setText(
                    emprestimo.getDataDevolucaoPrevista() != null ? emprestimo.getDataDevolucaoPrevista().toString()
                            : "");

            // Define o exemplar selecionado para operações futuras (devolução/exclusão)
            selectedExemplarForEmprestimo = sistemaBiblioteca.getExemplarById(emprestimo.getIdExemplar());

        } else {
            clearFields();
        }
    }

    private void clearFields() {
        leitorComboBox.getSelectionModel().clearSelection();
        livroComboBox.getSelectionModel().clearSelection();
        dataEmprestimoField.setText("");
        dataDevolucaoField.setText("");
        tableGerenciarEmprestimos.getSelectionModel().clearSelection();
        selectedExemplarForEmprestimo = null;
    }

    @FXML
    private void handleEmprestarLivro(ActionEvent event) {
        App.debugLog("Botão Emprestar Livro clicado.");

        Leitor leitorSelecionado = leitorComboBox.getSelectionModel().getSelectedItem();
        Livro livroSelecionado = livroComboBox.getSelectionModel().getSelectedItem();

        if (leitorSelecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Leitor Não Selecionado", "Por favor, selecione um leitor na lista.");
            return;
        }
        if (livroSelecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Livro Não Selecionado", "Por favor, selecione um livro na lista.");
            return;
        }
        if (selectedExemplarForEmprestimo == null
                || selectedExemplarForEmprestimo.getIdLivro() != livroSelecionado.getIdLivro()
                || !selectedExemplarForEmprestimo.getStatus().equalsIgnoreCase("disponivel")) {
            showAlert(Alert.AlertType.WARNING, "Exemplar Indisponível",
                    "Não há um exemplar disponível para o livro selecionado. Por favor, selecione outro livro ou verifique os exemplares.");
            return;
        }

        String dataEmprestimoStr = dataEmprestimoField.getText();
        String dataDevolucaoStr = dataDevolucaoField.getText();

        if (dataEmprestimoStr.isEmpty() || dataDevolucaoStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios",
                    "Por favor, preencha as datas de empréstimo e devolução prevista.");
            return;
        }

        try {
            LocalDate dataEmprestimo = LocalDate.parse(dataEmprestimoStr);
            LocalDate dataDevolucaoPrevista = LocalDate.parse(dataDevolucaoStr);

            Emprestimo novoEmprestimo = new Emprestimo(
                    selectedExemplarForEmprestimo.getIdExemplar(), // Usa o exemplar disponível encontrado
                    leitorSelecionado.getIdLeitor(), // Usa o leitor selecionado na ComboBox
                    dataEmprestimo,
                    dataDevolucaoPrevista);

            if (sistemaBiblioteca.addEmprestimo(novoEmprestimo)) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo registrado com sucesso!");
                loadEmprestimosData();
                loadComboBoxData(); // Recarrega ComboBoxes para refletir mudanças de disponibilidade
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro",
                        "Falha ao registrar empréstimo. Verifique o log de depuração.");
            }
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Formato de Data Inválido",
                    "Por favor, use o formato AAAA-MM-DD para as datas.");
        }
    }

    @FXML
    private void handleDevolverLivro(ActionEvent event) {
        App.debugLog("Botão Devolver Livro clicado.");
        Emprestimo selectedEmprestimo = tableGerenciarEmprestimos.getSelectionModel().getSelectedItem();
        if (selectedEmprestimo == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção",
                    "Por favor, selecione um empréstimo na tabela para devolver.");
            return;
        }

        if (selectedEmprestimo.getDataDevolucaoReal() != null) {
            showAlert(Alert.AlertType.INFORMATION, "Já Devolvido", "Este empréstimo já foi devolvido.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirmação de Devolução",
                "Tem certeza que deseja registrar a devolução do livro '" + selectedEmprestimo.getTituloLivro()
                        + "' por '" + selectedEmprestimo.getNomeLeitor() + "'?",
                "A data de devolução será definida para hoje.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (sistemaBiblioteca.devolverEmprestimo(selectedEmprestimo.getIdEmprestimo(),
                    selectedEmprestimo.getIdExemplar())) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Devolução registrada com sucesso!");
                loadEmprestimosData();
                loadComboBoxData(); // Recarrega ComboBoxes para refletir mudanças de disponibilidade
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao registrar devolução. Verifique o log de depuração.");
            }
        }
    }

    @FXML
    private void handleExcluirEmprestimo(ActionEvent event) {
        App.debugLog("Botão Excluir Empréstimo clicado.");
        Emprestimo selectedEmprestimo = tableGerenciarEmprestimos.getSelectionModel().getSelectedItem();
        if (selectedEmprestimo == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção",
                    "Por favor, selecione um empréstimo na tabela para excluir.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirmação de Exclusão",
                "Tem certeza que deseja EXCLUIR o empréstimo do livro '" + selectedEmprestimo.getTituloLivro()
                        + "' por '" + selectedEmprestimo.getNomeLeitor() + "'?",
                "Esta ação não pode ser desfeita e NÃO altera o status do exemplar. Use 'Devolver' para registrar a devolução.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (sistemaBiblioteca.deleteEmprestimo(selectedEmprestimo.getIdEmprestimo())) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo excluído com sucesso!");
                loadEmprestimosData();
                loadComboBoxData(); // Recarrega ComboBoxes para refletir mudanças de disponibilidade
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao excluir empréstimo. Verifique o log de depuração.");
            }
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        App.debugLog("Botão 'Voltar' clicado na tela de Empréstimos! Retornando à tela principal.");
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
