package biblioteca;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import biblioteca.model.Emprestimo;
import biblioteca.model.Livro; // Importar Livro
import biblioteca.service.SistemaBiblioteca;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory; // Adicionado para TableView

public class SecondaryController implements Initializable {

    @FXML
    private Label totalLivrosDisponiveisLabel;

    @FXML
    private TableView<Emprestimo> leitoresTable; // Provavelmente deveria ser emprestimosTable
    @FXML
    private TableColumn<Emprestimo, String> nomeLeitorColumn;
    @FXML
    private TableColumn<Emprestimo, String> emprestimosAtivosColumn; // Título do Livro
    @FXML
    private TableColumn<Emprestimo, String> telefoneLeitorColumn;

    private SistemaBiblioteca sistemaBiblioteca;

    public SecondaryController() {
        sistemaBiblioteca = new SistemaBiblioteca();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.debugLog("SecondaryController inicializado.");

        if (sistemaBiblioteca.getConnection() == null) {
            totalLivrosDisponiveisLabel.setText("Erro: Conexão com BD falhou.");
            return;
        }

        // Configurar colunas da tabela de empréstimos (se for para exibir empréstimos
        // ativos)
        // Se esta tabela for para exibir empréstimos ativos, as PropertyValueFactory
        // devem ser ajustadas
        // para as propriedades do modelo Emprestimo.
        // Assumindo que você quer exibir empréstimos ativos aqui:
        nomeLeitorColumn.setCellValueFactory(new PropertyValueFactory<>("nomeLeitor"));
        emprestimosAtivosColumn.setCellValueFactory(new PropertyValueFactory<>("tituloLivro")); // Exibe o título do
                                                                                                // livro
        telefoneLeitorColumn.setCellValueFactory(new PropertyValueFactory<>("telefoneLeitor"));

        loadDashboardData();
    }

    private void loadDashboardData() {
        // Atualizar o total de livros disponíveis
        int totalDisponiveis = 0;
        List<Livro> todosLivros = sistemaBiblioteca.getAllLivros();
        if (todosLivros != null) {
            for (Livro livro : todosLivros) {
                totalDisponiveis += livro.getDisponivel(); // Soma a quantidade disponível de cada livro
            }
        }
        totalLivrosDisponiveisLabel.setText("Total de Livros Disponíveis: " + totalDisponiveis);
        App.debugLog("Dashboard: Total de Livros Disponíveis: " + totalDisponiveis);

        // Carregar os empréstimos ativos para a tabela
        // O método getAllEmprestimosComDetalhes() retorna todos os empréstimos (ativos
        // e devolvidos)
        // Você pode filtrar aqui se quiser apenas os ativos, ou exibir todos.
        // Para a tela principal, vamos exibir apenas os que ainda não foram devolvidos.
        List<Emprestimo> todosEmprestimos = sistemaBiblioteca.getAllEmprestimosComDetalhes();
        if (todosEmprestimos != null) {
            // Filtra apenas os empréstimos ativos (dataDevolucaoReal é null)
            List<Emprestimo> emprestimosAtivos = todosEmprestimos.stream()
                    .filter(e -> e.getDataDevolucaoReal() == null)
                    .toList(); // Usa toList() para criar uma lista imutável
            leitoresTable.getItems().setAll(emprestimosAtivos); // Atualiza a tabela
            App.debugLog("Dashboard: " + emprestimosAtivos.size() + " empréstimos ativos carregados.");
        } else {
            leitoresTable.getItems().clear();
            App.debugLog("Dashboard: Nenhum empréstimo ativo encontrado ou erro ao carregar.");
        }
    }

    @FXML
    private void handleGerenciarAutores(ActionEvent event) {
        System.out.println("Gerenciar Autores clicado!");
        App.debugLog("Botão 'Gerenciar Autores' clicado!");
        // Se você não vai gerir autores, este botão pode ser desabilitado ou removido
        // no FXML
        // Ou você pode adicionar uma tela de "Autores" simples se decidir gerenciar.
        // Por enquanto, apenas loga.
    }

    @FXML
    private void handleGerenciarLivros(ActionEvent event) {
        App.debugLog("Botão 'Gerenciar Livros' clicado! Carregando tela de Livros.");
        try {
            App.setRoot("livros"); // Carrega o FXML da tela de gerenciamento de livros
        } catch (IOException e) {
            App.debugLog("Erro ao carregar tela de Livros: " + e.getMessage());
            e.printStackTrace();
            // Opcional: mostrar um alerta para o usuário
            // showAlert(Alert.AlertType.ERROR, "Erro de Carregamento", "Não foi possível
            // carregar a tela de gerenciamento de livros.");
        }
    }

    @FXML
    private void handleGerenciarExemplares(ActionEvent event) {
        System.out.println("Gerenciar Exemplares clicado!");
        App.debugLog("Botão 'Gerenciar Exemplares' clicado!");
        // Implementar navegação para tela de Exemplares
    }

    @FXML
    private void handleGerenciarLeitores(ActionEvent event) {
        App.debugLog("Botão 'Gerenciar Leitores' clicado! Carregando tela de Leitores.");
        try {
            App.setRoot("leitor"); // Carrega o FXML da tela de gerenciamento de leitores
        } catch (IOException e) {
            App.debugLog("Erro ao carregar tela de Leitores: " + e.getMessage());
            e.printStackTrace();
            // Opcional: mostrar um alerta para o usuário
            // showAlert(Alert.AlertType.ERROR, "Erro de Carregamento", "Não foi possível
            // carregar a tela de gerenciamento de leitores.");
        }
    }

    @FXML
    private void handleGerenciarEmprestimos(ActionEvent event) {
        System.out.println("Gerenciar Empréstimos clicado!");
        App.debugLog("Botão 'Gerenciar Empréstimos' clicado! Carregando tela de Empréstimos.");
        try {
            App.setRoot("emprestimos"); // Carrega o FXML da tela de gerenciamento de empréstimos
        } catch (IOException e) {
            App.debugLog("Erro ao carregar tela de Empréstimos: " + e.getMessage());
            e.printStackTrace();
            // Opcional: mostrar um alerta para o usuário
            // showAlert(Alert.AlertType.ERROR, "Erro de Carregamento", "Não foi possível
            // carregar a tela de gerenciamento de empréstimos.");
        }
    }

    @FXML
    private void handleRelatorios(ActionEvent event) {
        System.out.println("Relatórios clicado!");
        App.debugLog("Botão 'Relatórios' clicado!");
        // Implementar navegação para tela de Relatórios
    }

    @FXML
    private void handleSair(ActionEvent event) {
        if (sistemaBiblioteca != null) {
            sistemaBiblioteca.closeConnection();
            App.debugLog("Conexão com o BD fechada ao sair.");
        }
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
