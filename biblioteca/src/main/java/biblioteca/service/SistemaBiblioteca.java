package biblioteca.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import biblioteca.App;
import biblioteca.model.Autor;
import biblioteca.model.Livro;
import biblioteca.model.Exemplar;
import biblioteca.model.Leitor;
import biblioteca.model.Emprestimo;

public class SistemaBiblioteca {
    private Connection connection;

    public SistemaBiblioteca() {
        try {
            String url = "jdbc:mysql://localhost:3306/biblioteca";
            String user = "root";
            String password = "Minharainha123$"; // <<<<< Sua senha REAL aqui!

            this.connection = DriverManager.getConnection(url, user, password);
            App.debugLog("✅ Conexão com o MySQL estabelecida para SistemaBiblioteca!");
        } catch (SQLException e) {
            App.debugLog("❌ Erro ao conectar ao MySQL no SistemaBiblioteca: " + e.getMessage());
            e.printStackTrace();
            this.connection = null;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                App.debugLog("Conexão com o MySQL fechada para SistemaBiblioteca.");
            } catch (SQLException e) {
                App.debugLog("Erro ao fechar conexão com o MySQL: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // --- Métodos de Login ---
    public boolean login(String username, String password) {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            App.debugLog("Erro durante o login: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // --- Métodos CRUD para Leitores ---

    public boolean addLeitor(Leitor leitor) {
        String sql = "INSERT INTO Leitor (nome, endereco, telefone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, leitor.getNome());
            pstmt.setString(2, leitor.getEndereco());
            pstmt.setString(3, leitor.getTelefone());
            pstmt.setString(4, leitor.getEmail());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        leitor.setIdLeitor(generatedKeys.getInt(1));
                    }
                }
                App.debugLog("addLeitor: Leitor '" + leitor.getNome() + "' adicionado com ID " + leitor.getIdLeitor()
                        + ". Linhas afetadas: " + affectedRows);
                return true;
            }
            return false;
        } catch (SQLException e) {
            App.debugLog("Erro ao adicionar leitor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLeitor(Leitor leitor) {
        String sql = "UPDATE Leitor SET nome = ?, endereco = ?, telefone = ?, email = ? WHERE id_leitor = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, leitor.getNome());
            pstmt.setString(2, leitor.getEndereco());
            pstmt.setString(3, leitor.getTelefone());
            pstmt.setString(4, leitor.getEmail());
            pstmt.setInt(5, leitor.getIdLeitor());
            int affectedRows = pstmt.executeUpdate();
            App.debugLog("updateLeitor: Leitor ID " + leitor.getIdLeitor() + " atualizado. Linhas afetadas: "
                    + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            App.debugLog("Erro ao atualizar leitor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLeitor(int idLeitor) {
        String sql = "DELETE FROM Leitor WHERE id_leitor = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idLeitor);
            int affectedRows = pstmt.executeUpdate();
            App.debugLog("deleteLeitor: Leitor ID " + idLeitor + " deletado. Linhas afetadas: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            App.debugLog("Erro ao deletar leitor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Leitor> getAllLeitores() {
        List<Leitor> leitores = new ArrayList<>();
        String sql = "SELECT id_leitor, nome, endereco, telefone, email FROM Leitor";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                leitores.add(new Leitor(
                        rs.getInt("id_leitor"),
                        rs.getString("nome"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email")));
            }
            App.debugLog("SistemaBiblioteca: Fetched " + leitores.size() + " leitores.");
        } catch (SQLException e) {
            App.debugLog("Erro ao obter todos os leitores: " + e.getMessage());
            e.printStackTrace();
        }
        return leitores;
    }

    public Leitor getLeitorById(int id) {
        String sql = "SELECT id_leitor, nome, endereco, telefone, email FROM Leitor WHERE id_leitor = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Leitor(
                            rs.getInt("id_leitor"),
                            rs.getString("nome"),
                            rs.getString("endereco"),
                            rs.getString("telefone"),
                            rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            App.debugLog("Erro ao obter leitor por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // NOVO: Método para buscar leitores por nome (para campo de procura)
    public List<Leitor> searchLeitoresByName(String nome) {
        List<Leitor> leitores = new ArrayList<>();
        String sql = "SELECT id_leitor, nome, endereco, telefone, email FROM Leitor WHERE nome LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    leitores.add(new Leitor(
                            rs.getInt("id_leitor"),
                            rs.getString("nome"),
                            rs.getString("endereco"),
                            rs.getString("telefone"),
                            rs.getString("email")));
                }
            }
        } catch (SQLException e) {
            App.debugLog("Erro ao buscar leitores por nome: " + e.getMessage());
            e.printStackTrace();
        }
        return leitores;
    }

    // --- Métodos CRUD para Livros (AGORA SEM LIGAÇÃO DIRETA COM TABELA
    // AUTOR/LIVRO_AUTOR) ---

    public boolean addLivro(Livro livro) {
        String sqlLivro = "INSERT INTO Livro (titulo, ISBN, editora, ano_publicacao, disponivel, nome_autor_livro) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmtLivro = connection.prepareStatement(sqlLivro, Statement.RETURN_GENERATED_KEYS)) {
            pstmtLivro.setString(1, livro.getTitulo());
            pstmtLivro.setString(2, livro.getISBN());
            pstmtLivro.setString(3, livro.getGenero());
            pstmtLivro.setInt(4, livro.getAnoPublicacao());
            pstmtLivro.setInt(5, livro.getDisponivel());
            pstmtLivro.setString(6, livro.getNomeAutorLivro());

            int affectedRows = pstmtLivro.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmtLivro.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        livro.setIdLivro(generatedKeys.getInt(1));
                    }
                }
                App.debugLog("addLivro: Livro '" + livro.getTitulo() + "' adicionado com ID " + livro.getIdLivro()
                        + " e autor '" + livro.getNomeAutorLivro() + "'. Linhas afetadas: " + affectedRows);
                return true;
            }
            return false;
        } catch (SQLException e) {
            App.debugLog("Erro ao adicionar livro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLivro(Livro livro) {
        String sqlLivro = "UPDATE Livro SET titulo = ?, ISBN = ?, editora = ?, ano_publicacao = ?, disponivel = ?, nome_autor_livro = ? WHERE id_livro = ?";
        try (PreparedStatement pstmtLivro = connection.prepareStatement(sqlLivro)) {
            pstmtLivro.setString(1, livro.getTitulo());
            pstmtLivro.setString(2, livro.getISBN());
            pstmtLivro.setString(3, livro.getGenero());
            pstmtLivro.setInt(4, livro.getAnoPublicacao());
            pstmtLivro.setInt(5, livro.getDisponivel());
            pstmtLivro.setString(6, livro.getNomeAutorLivro());
            pstmtLivro.setInt(7, livro.getIdLivro());

            int affectedRows = pstmtLivro.executeUpdate();
            App.debugLog(
                    "updateLivro: Livro ID " + livro.getIdLivro() + " atualizado. Linhas afetadas: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            App.debugLog("Erro ao atualizar livro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLivro(int idLivro) {
        String sqlDeleteLivro = "DELETE FROM Livro WHERE id_livro = ?";
        try (PreparedStatement pstmtLivro = connection.prepareStatement(sqlDeleteLivro)) {
            pstmtLivro.setInt(1, idLivro);
            int affectedRows = pstmtLivro.executeUpdate();
            App.debugLog("deleteLivro: Livro ID " + idLivro + " deletado. Linhas afetadas: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            App.debugLog("Erro ao deletar livro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Livro> getAllLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id_livro, titulo, ISBN, editora AS genero, ano_publicacao, disponivel, nome_autor_livro " +
                "FROM Livro";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Livro livro = new Livro(
                        rs.getInt("id_livro"),
                        rs.getString("titulo"),
                        rs.getString("ISBN"),
                        rs.getString("genero"),
                        rs.getInt("ano_publicacao"),
                        rs.getInt("disponivel"),
                        rs.getString("nome_autor_livro"));
                livros.add(livro);
            }
            App.debugLog("SistemaBiblioteca: Fetched " + livros.size() + " livros.");
        } catch (SQLException e) {
            App.debugLog("Erro ao obter todos os livros: " + e.getMessage());
            e.printStackTrace();
        }
        return livros;
    }

    public Livro getLivroById(int id) {
        String sql = "SELECT id_livro, titulo, ISBN, editora, ano_publicacao, disponivel, nome_autor_livro FROM Livro WHERE id_livro = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Livro(
                            rs.getInt("id_livro"),
                            rs.getString("titulo"),
                            rs.getString("ISBN"),
                            rs.getString("editora"),
                            rs.getInt("ano_publicacao"),
                            rs.getInt("disponivel"),
                            rs.getString("nome_autor_livro"));
                }
            }
        } catch (SQLException e) {
            App.debugLog("Erro ao obter livro por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // NOVO: Método para buscar livros por título (para campo de procura)
    public List<Livro> searchLivrosByTitulo(String titulo) {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id_livro, titulo, ISBN, editora AS genero, ano_publicacao, disponivel, nome_autor_livro FROM Livro WHERE titulo LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + titulo + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Livro livro = new Livro(
                            rs.getInt("id_livro"),
                            rs.getString("titulo"),
                            rs.getString("ISBN"),
                            rs.getString("genero"),
                            rs.getInt("ano_publicacao"),
                            rs.getInt("disponivel"),
                            rs.getString("nome_autor_livro"));
                    livros.add(livro);
                }
            }
        } catch (SQLException e) {
            App.debugLog("Erro ao buscar livros por título: " + e.getMessage());
            e.printStackTrace();
        }
        return livros;
    }

    // --- Métodos CRUD para Empréstimos ---

    // Método para adicionar um novo empréstimo
    public boolean addEmprestimo(Emprestimo emprestimo) {
        String sql = "INSERT INTO Emprestimo (id_exemplar, id_leitor, data_emprestimo, data_devolucao) VALUES (?, ?, ?, ?)";
        String sqlUpdateExemplarStatus = "UPDATE Exemplar SET status = 'emprestado' WHERE id_exemplar = ?"; // Atualiza
                                                                                                            // status do
                                                                                                            // exemplar
        try {
            connection.setAutoCommit(false); // Inicia transação

            // 1. Inserir o empréstimo
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, emprestimo.getIdExemplar());
                pstmt.setInt(2, emprestimo.getIdLeitor());
                pstmt.setDate(3, java.sql.Date.valueOf(emprestimo.getDataEmprestimo()));
                pstmt.setDate(4, java.sql.Date.valueOf(emprestimo.getDataDevolucaoPrevista()));
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        emprestimo.setIdEmprestimo(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // 2. Atualizar o status do exemplar para 'emprestado'
            try (PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdateExemplarStatus)) {
                pstmtUpdate.setInt(1, emprestimo.getIdExemplar());
                pstmtUpdate.executeUpdate();
            }

            connection.commit(); // Confirma a transação
            App.debugLog("addEmprestimo: Empréstimo adicionado com sucesso para exemplar " + emprestimo.getIdExemplar()
                    + " e leitor " + emprestimo.getIdLeitor());
            return true;
        } catch (SQLException e) {
            App.debugLog("Erro ao adicionar empréstimo: " + e.getMessage());
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                App.debugLog("Erro ao fazer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.debugLog("Erro ao resetar auto-commit: " + e.getMessage());
            }
        }
    }

    // Método para registrar a devolução de um empréstimo
    public boolean devolverEmprestimo(int idEmprestimo, int idExemplar) {
        String sql = "UPDATE Emprestimo SET data_devolvida = ? WHERE id_emprestimo = ?";
        String sqlUpdateExemplarStatus = "UPDATE Exemplar SET status = 'disponivel' WHERE id_exemplar = ?"; // Atualiza
                                                                                                            // status do
                                                                                                            // exemplar
        try {
            connection.setAutoCommit(false); // Inicia transação

            // 1. Atualizar a data de devolução real no empréstimo
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                pstmt.setInt(2, idEmprestimo);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // 2. Atualizar o status do exemplar para 'disponivel'
            try (PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdateExemplarStatus)) {
                pstmtUpdate.setInt(1, idExemplar);
                pstmtUpdate.executeUpdate();
            }

            connection.commit(); // Confirma a transação
            App.debugLog("devolverEmprestimo: Empréstimo ID " + idEmprestimo + " devolvido com sucesso.");
            return true;
        } catch (SQLException e) {
            App.debugLog("Erro ao devolver empréstimo: " + e.getMessage());
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                App.debugLog("Erro ao fazer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                App.debugLog("Erro ao resetar auto-commit: " + e.getMessage());
            }
        }
    }

    // Método para deletar um empréstimo (cuidado ao usar, geralmente é melhor
    // "devolver" do que deletar)
    public boolean deleteEmprestimo(int idEmprestimo) {
        String sql = "DELETE FROM Emprestimo WHERE id_emprestimo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idEmprestimo);
            int affectedRows = pstmt.executeUpdate();
            App.debugLog(
                    "deleteEmprestimo: Empréstimo ID " + idEmprestimo + " deletado. Linhas afetadas: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            App.debugLog("Erro ao deletar empréstimo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para obter todos os empréstimos (ativos e devolvidos) com detalhes de
    // leitor e livro
    public List<Emprestimo> getAllEmprestimosComDetalhes() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT e.id_emprestimo, e.id_exemplar, e.id_leitor, e.data_emprestimo, e.data_devolucao, e.data_devolvida, "
                +
                "l.nome AS nome_leitor, l.telefone AS telefone_leitor, l.email AS email_leitor, " +
                "liv.titulo AS titulo_livro " +
                "FROM Emprestimo e " +
                "JOIN Leitor l ON e.id_leitor = l.id_leitor " +
                "JOIN Exemplar ex ON e.id_exemplar = ex.id_exemplar " +
                "JOIN Livro liv ON ex.id_livro = liv.id_livro " +
                "ORDER BY e.data_emprestimo DESC"; // Ordena pelos mais recentes

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate dataEmprestimo = rs.getDate("data_emprestimo") != null
                        ? rs.getDate("data_emprestimo").toLocalDate()
                        : null;
                LocalDate dataDevolucaoPrevista = rs.getDate("data_devolucao") != null
                        ? rs.getDate("data_devolucao").toLocalDate()
                        : null;
                LocalDate dataDevolucaoReal = rs.getDate("data_devolvida") != null
                        ? rs.getDate("data_devolvida").toLocalDate()
                        : null;

                Emprestimo emprestimo = new Emprestimo(
                        rs.getInt("id_emprestimo"),
                        rs.getInt("id_exemplar"),
                        rs.getInt("id_leitor"),
                        dataEmprestimo,
                        dataDevolucaoPrevista,
                        dataDevolucaoReal);
                emprestimo.setNomeLeitor(rs.getString("nome_leitor"));
                emprestimo.setTelefoneLeitor(rs.getString("telefone_leitor"));
                emprestimo.setEmailLeitor(rs.getString("email_leitor"));
                emprestimo.setTituloLivro(rs.getString("titulo_livro"));
                emprestimos.add(emprestimo);
            }
            App.debugLog("SistemaBiblioteca: Fetched " + emprestimos.size() + " empréstimos com detalhes.");
        } catch (SQLException e) {
            App.debugLog("Erro ao obter todos os empréstimos com detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return emprestimos;
    }

    // Método para obter um exemplar por ID
    public Exemplar getExemplarById(int idExemplar) {
        String sql = "SELECT id_exemplar, id_livro, status FROM Exemplar WHERE id_exemplar = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idExemplar);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Exemplar(rs.getInt("id_exemplar"), rs.getInt("id_livro"), rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            App.debugLog("Erro ao obter exemplar por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Método para obter um exemplar disponível de um livro específico
    public Exemplar getAvailableExemplarByLivroId(int idLivro) {
        String sql = "SELECT id_exemplar, id_livro, status FROM Exemplar WHERE id_livro = ? AND status = 'disponivel' LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idLivro);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Exemplar(rs.getInt("id_exemplar"), rs.getInt("id_livro"), rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            App.debugLog("Erro ao obter exemplar disponível por ID de livro: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // --- Métodos para Autores (mantidos) ---
    public List<Autor> getAllAutores() {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT id_autor, nome FROM Autor";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                autores.add(new Autor(rs.getInt("id_autor"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            App.debugLog("Erro ao obter autores: " + e.getMessage());
        }
        return autores;
    }
}
