package biblioteca.model;

public class Exemplar {
    private int idExemplar;
    private int idLivro;
    private String status; // Ex: "disponivel", "emprestado", "danificado"

    public Exemplar(int idExemplar, int idLivro, String status) {
        this.idExemplar = idExemplar;
        this.idLivro = idLivro;
        this.status = status;
    }

    // Getters
    public int getIdExemplar() {
        return idExemplar;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public String getStatus() {
        return status;
    }

    // Setters (se necessário)
    public void setIdExemplar(int idExemplar) {
        this.idExemplar = idExemplar;
    }

    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}