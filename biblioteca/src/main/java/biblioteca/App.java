package biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage; // Variável estática para acessar o Stage principal

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Atribui o Stage para a variável estática
        Parent root = loadFXML("primary"); // Carrega o FXML da tela inicial

        // Cria a cena com o tamanho preferencial do painel raiz do FXML
        // Se o FXML não tiver prefWidth/prefHeight, ele usará o tamanho padrão ou o
        // tamanho do conteúdo
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sistema de Gestão de Biblioteca"); // Defina o título da janela

        // Redimensiona a janela para o tamanho preferencial da cena (que se baseia no
        // root do FXML)
        stage.sizeToScene();
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent newRoot = loadFXML(fxml);
        scene.setRoot(newRoot); // Define o novo painel raiz para a cena

        // Redimensiona a janela para o tamanho preferencial do NOVO painel raiz
        if (primaryStage != null) {
            primaryStage.sizeToScene();
            // Opcional: Centralizar a janela na tela após o redimensionamento
            // primaryStage.centerOnScreen();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // Método de depuração, útil para ver mensagens no console
    public static void debugLog(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        launch();
    }
}
