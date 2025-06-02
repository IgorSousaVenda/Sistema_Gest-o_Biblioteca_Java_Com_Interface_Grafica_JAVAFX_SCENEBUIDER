package biblioteca.model;

import java.time.LocalDate;

public class Emprestimo {
    private int idEmprestimo;
    private int idExemplar;
    private int idLeitor;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal; // Pode ser null se não foi devolvido

    // Campos para exibição na UI (não estão no DB diretamente na tabela Emprestimo)
    private String nomeLeitor;
    private String telefoneLeitor;
    private String emailLeitor;
    private String tituloLivro; // Título do livro emprestado

    public Emprestimo(int idEmprestimo, int idExemplar, int idLeitor, LocalDate dataEmprestimo,
            LocalDate dataDevolucaoPrevista, LocalDate dataDevolucaoReal) {
        this.idEmprestimo = idEmprestimo;
        this.idExemplar = idExemplar;
        this.idLeitor = idLeitor;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    // Construtor adicional para facilitar a criação com dados da UI
    public Emprestimo(int idExemplar, int idLeitor, LocalDate dataEmprestimo, LocalDate dataDevolucaoPrevista) {
        this(0, idExemplar, idLeitor, dataEmprestimo, dataDevolucaoPrevista, null);
    }

    // Getters
    public int getIdEmprestimo() {
        return idEmprestimo;
    }

    public int getIdExemplar() {
        return idExemplar;
    }

    public int getIdLeitor() {
        return idLeitor;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public String getNomeLeitor() {
        return nomeLeitor;
    }

    public String getTelefoneLeitor() {
        return telefoneLeitor;
    }

    public String getEmailLeitor() {
        return emailLeitor;
    }

    public String getTituloLivro() {
        return tituloLivro;
    }

    // Setters
    public void setIdEmprestimo(int idEmprestimo) {
        this.idEmprestimo = idEmprestimo;
    }

    public void setIdExemplar(int idExemplar) {
        this.idExemplar = idExemplar;
    }

    public void setIdLeitor(int idLeitor) {
        this.idLeitor = idLeitor;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public void setNomeLeitor(String nomeLeitor) {
        this.nomeLeitor = nomeLeitor;
    }

    public void setTelefoneLeitor(String telefoneLeitor) {
        this.telefoneLeitor = telefoneLeitor;
    }

    public void setEmailLeitor(String emailLeitor) {
        this.emailLeitor = emailLeitor;
    }

    public void setTituloLivro(String tituloLivro) {
        this.tituloLivro = tituloLivro;
    }
}
