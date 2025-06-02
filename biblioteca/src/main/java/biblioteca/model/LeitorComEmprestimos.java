package biblioteca.model;

public class LeitorComEmprestimos {
    private int idLeitor;
    private String nomeLeitor;
    private String endereco;
    private String telefoneLeitor;
    private String email;
    private int numeroEmprestimosAtivos;

    public LeitorComEmprestimos(int idLeitor, String nomeLeitor, String endereco, String telefoneLeitor, String email,
            int numeroEmprestimosAtivos) {
        this.idLeitor = idLeitor;
        this.nomeLeitor = nomeLeitor;
        this.endereco = endereco;
        this.telefoneLeitor = telefoneLeitor;
        this.email = email;
        this.numeroEmprestimosAtivos = numeroEmprestimosAtivos;
    }

    // --- Getters ---
    public int getIdLeitor() {
        return idLeitor;
    }

    public String getNomeLeitor() {
        return nomeLeitor;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefoneLeitor() {
        return telefoneLeitor;
    }

    public String getEmail() {
        return email;
    }

    public int getNumeroEmprestimosAtivos() {
        return numeroEmprestimosAtivos;
    }

    // --- Setters (apenas se precisar, como para atualizar a contagem de
    // empréstimos ativos) ---
    public void setIdLeitor(int idLeitor) {
        this.idLeitor = idLeitor;
    }

    public void setNomeLeitor(String nomeLeitor) {
        this.nomeLeitor = nomeLeitor;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setTelefoneLeitor(String telefoneLeitor) {
        this.telefoneLeitor = telefoneLeitor;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumeroEmprestimosAtivos(int numeroEmprestimosAtivos) {
        this.numeroEmprestimosAtivos = numeroEmprestimosAtivos;
    }
}