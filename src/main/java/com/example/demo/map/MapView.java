package com.example.demo.map;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

public class MapView extends AnchorPane {

    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();

    private Runnable whenReady;

    // Callbacks hacia el Controller
    private Consumer<String> onMarkerClick;
    private Consumer<String> onSecurityCameraClick;

    /** Objeto expuesto como window.javaBridge en JS */
    public final class JavaBridge {
        /** Click genérico (radares, parking, semáforos, etc.) */
        public void onMarkerClick(String id) {
            Consumer<String> cb = onMarkerClick;
            if (cb != null) Platform.runLater(() -> cb.accept(id));
        }
        /** Click específico de cámaras de seguridad */
        public void onSecurityCameraClick(String id) {
            Consumer<String> cb = onSecurityCameraClick;
            if (cb != null) Platform.runLater(() -> cb.accept(id));
        }
    }

    public MapView() {
        getChildren().add(webView);
        AnchorPane.setTopAnchor(webView, 0.0);
        AnchorPane.setRightAnchor(webView, 0.0);
        AnchorPane.setBottomAnchor(webView, 0.0);
        AnchorPane.setLeftAnchor(webView, 0.0);

        // Cargar el HTML del mapa desde resources
        URL url = MapView.class.getResource("/com/example/demo/map/index.html");
        Objects.requireNonNull(url, "No se encontró /com/example/demo/map/index.html en resources");
        engine.load(url.toExternalForm());

        // Cuando carga el HTML, exponemos el puente JS y avisamos al controller
        engine.getLoadWorker().stateProperty().addListener((obs, old, st) -> {
            if (st == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("javaBridge", new JavaBridge());
                if (whenReady != null) Platform.runLater(whenReady);
            }
        });
    }

    // ---- API pública usada por el Controller ----
    public void whenReady(Runnable r) { this.whenReady = r; }

    public void setOnMarkerClick(Consumer<String> cb) { this.onMarkerClick = cb; }

    public void setOnSecurityCameraClick(Consumer<String> cb) { this.onSecurityCameraClick = cb; }

    /** Envía el JSON de dispositivos al script addDevices(jsonText) del index.html */
    public void addDevices(String jsonText) {
        String escaped = toJsTemplateLiteral(jsonText);
        runJs("if (window.addDevices) window.addDevices(`" + escaped + "`);");
    }

    /** Centra el mapa en (lat, lng) con zoom dado */
    public void setView(double lat, double lng, int zoom) {
        runJs("if (window.setView) window.setView(" + lat + "," + lng + "," + zoom + ");");
    }

    /** Muestra/oculta un grupo de marcadores (layerGroup) en el mapa */
    public void setGroupVisible(String group, boolean visible) {
        runJs("if (window.setGroupVisible) window.setGroupVisible('" + group + "', " + visible + ");");
    }

    /** Muestra/oculta TODOS los marcadores de dispositivos */
    public void setAllDevicesVisible(boolean visible) {
        runJs("if (window.setAllDevicesVisible) window.setAllDevicesVisible(" + visible + ");");
    }

    /** Carga un archivo JSON desde el classpath y lo envía al mapa */
    public void addDevicesFromResource(String resourcePath) {
        try (var is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("No existe resource: " + resourcePath);
            String raw = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            addDevices(raw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---- Helpers ----

    /** Ejecuta JS de forma segura en el hilo de JavaFX */
    private void runJs(String script) {
        if (Platform.isFxApplicationThread()) {
            try { engine.executeScript(script); } catch (Exception ignored) {}
        } else {
            Platform.runLater(() -> {
                try { engine.executeScript(script); } catch (Exception ignored) {}
            });
        }
    }

    /**
     * Escapa texto para usar dentro de un template literal JS:
     *  - backslash -> \\
     *  - backtick  -> \`
     *  - secuencia ${ -> \${
     */
    private static String toJsTemplateLiteral(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("`", "\\`")
                .replace("${", "\\${");
    }
}
