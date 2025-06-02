package biblioteca.model;

public class Autor {
    private int idAutor;
    private String nome;
    private String nacionalidade; // Adicionado para consistência, se você usa

    // Construtor que o SistemaBiblioteca espera para getAllAutores
    public Autor(int idAutor, String nome) {
        this.idAutor = idAutor;
        this.nome = nome;
        this.nacionalidade = null; // Ou um valor padrão, se não for passado
    }

    // Construtor completo (se você precisar adicionar autores com nacionalidade)
    public Autor(int idAutor, String nome, String nacionalidade) {
        this.idAutor = idAutor;
        this.nome = nome;
        this.nacionalidade = nacionalidade;
    }

    // Getters
    public int getIdAutor() {
        return idAutor;
    }

    public String getNome() {
        return nome;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    // Setters (se necessário)
    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }
}