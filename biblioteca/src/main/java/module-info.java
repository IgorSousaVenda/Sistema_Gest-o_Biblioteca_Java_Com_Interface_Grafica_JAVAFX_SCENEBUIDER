module biblioteca { // O nome do seu módulo é 'biblioteca'
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Para a conexão com o banco de dados
    requires java.base; // Geralmente implícito, mas bom ter

    opens biblioteca.model to javafx.base;
    // Abre o pacote principal para o JavaFX FXML e reflection
    opens biblioteca to javafx.fxml;

    // Abre os pacotes de modelo e serviço para o JavaFX (necessário para
    // PropertyValueFactory)
    // Certifique-se de que estes caminhos correspondem à sua estrutura de pastas
    // real
    // Para PropertyValueFactory acessar os getters das classes de modelo
    opens biblioteca.service to javafx.base; // Se SystemaBiblioteca ou outras classes aqui precisam ser acessadas por
                                             // reflection

    exports biblioteca; // Exporta o pacote principal
    exports biblioteca.model; // Exporta o pacote de modelos
    exports biblioteca.service; // Exporta o pacote de serviço
}
