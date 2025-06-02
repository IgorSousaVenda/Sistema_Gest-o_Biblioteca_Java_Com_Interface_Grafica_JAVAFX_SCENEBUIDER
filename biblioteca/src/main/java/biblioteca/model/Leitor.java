package biblioteca.model;

public class Leitor {
    private int idLeitor;
    private String nome;
    private String endereco;
    private String telefone;
    private String email;

    // Construtor
    public Leitor(int idLeitor, String nome, String endereco, String telefone, String email) {
        this.idLeitor = idLeitor;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }

    // Getters
    public int getIdLeitor() {
        return idLeitor;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    // Setters (se necessário, mas para este caso, os getters são o principal)
    public void setIdLeitor(int idLeitor) {
        this.idLeitor = idLeitor;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}