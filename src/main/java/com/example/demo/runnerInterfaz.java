package com.example.demo;

import com.example.demo.core.AppContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;   // <— IMPORTANTE
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class runnerInterfaz extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1) Crear el contexto (ya arranca FineEmissionService en el constructor)
        AppContext ctx = new AppContext();

        // 2) Resolver la ruta del FXML y validar que exista
        URL fxmlUrl = runnerInterfaz.class.getResource("/com/example/demo/Interfaz.fxml");
        // Si esto da null, la ruta del FXML no es correcta o el archivo no está en resources
        Objects.requireNonNull(fxmlUrl, "No encontré /com/example/demo/Interfaz.fxml en resources");

        // 3) Cargar el FXML
        FXMLLoader fxml = new FXMLLoader(fxmlUrl);
        Parent root = fxml.load();  // <— ahora root es un Parent explícito

        // 4) Inyectar el contexto en el controller
        Controller controller = fxml.getController();
        controller.init(ctx);

        // 5) Escena/ventana
        Scene scene = new Scene(root, 1240, 950);
        stage.setTitle("Monitoring center!");
        stage.setScene(scene);
        stage.setResizable(false); // ponelo en true si querés hacerlo responsive

        // 6) Persistencia al salir
        stage.setOnCloseRequest(e -> ctx.saveOnExit());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}