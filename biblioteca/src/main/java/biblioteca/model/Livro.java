package biblioteca.model;

public class Livro {
    private int idLivro;
    private String titulo;
    private String ISBN;
    private String genero; // Mapeado para 'editora' do DB
    private int anoPublicacao;
    private int disponivel;
    private String nomeAutorLivro; // NOVO: Campo para o nome do autor armazenado diretamente no Livro

    // Construtor atualizado para incluir ISBN, disponivel e nomeAutorLivro
    public Livro(int idLivro, String titulo, String ISBN, String genero, int anoPublicacao, int disponivel,
            String nomeAutorLivro) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.ISBN = ISBN;
        this.genero = genero;
        this.anoPublicacao = anoPublicacao;
        this.disponivel = disponivel;
        this.nomeAutorLivro = nomeAutorLivro; // Inicializa com o nome do autor
    }

    // Getters
    public int getIdLivro() {
        return idLivro;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getGenero() { // Este getter corresponde à coluna 'editora' do seu DB
        return genero;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public int getDisponivel() {
        return disponivel;
    }

    public String getNomeAutorLivro() { // NOVO: Getter para o nome do autor do livro
        return nomeAutorLivro;
    }

    // Setters
    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setGenero(String genero) { // Este setter corresponde à coluna 'editora' do seu DB
        this.genero = genero;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public void setDisponivel(int disponivel) {
        this.disponivel = disponivel;
    }

    public void setNomeAutorLivro(String nomeAutorLivro) { // NOVO: Setter para o nome do autor do livro
        this.nomeAutorLivro = nomeAutorLivro;
    }
}
