package com.example.demo;

import com.example.demo.core.AppContext;
import com.example.demo.map.MapView;
import com.example.demo.services.ViolationService;
import com.example.demo.ui.ToggleSwitch;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller {

    // ===== contexto/servicios =====
    private AppContext ctx;
    private ViolationService violationService;
    private ObservableList<ViolationService.Violation> violations;

    // ===== UI =====
    @FXML private StackPane stack;
    @FXML private AnchorPane mapLayer;
    @FXML private StackPane  cameraLayer;   // overlay de foto
    @FXML private ImageView  cameraImage;
    @FXML private Button     btnPolice, btnAmbulance, btnFireman, btnBack;
    @FXML private Label      toastMap;

    @FXML private MenuItem menuViolationsView, menuReports;

    @FXML private ToggleSwitch swMap;
    @FXML private ToggleSwitch swSemaphores;
    @FXML private ToggleSwitch swRadars;
    @FXML private ToggleSwitch swCameras;
    @FXML private ToggleSwitch swFails;


    // Mapa web (Leaflet)
    private MapView mapView;

    // Ventanas secundarias
    private Stage violationsStage, reportsStage;

    // Tablas (Reports)
    private TableView<DeviceRow> devicesTable;
    private TableView<TypeRow> typesTable;

    // Estado de overlay
    private String currentDeviceId = null;
    private final Random random = new Random();
    private final String[] images = {
            "/com/example/demo/Images/SecurityPhotoAnomaly1.jpg",
            "/com/example/demo/Images/SecurityPhotoAnomaly2.jpg",
            "/com/example/demo/Images/SecurityPhotoAnomaly3.jpg",
            "/com/example/demo/Images/SecurityPhotoAnomaly4.jpg",
            "/com/example/demo/Images/SecurityPhotoAnomaly5.jpg",
            "/com/example/demo/Images/SecurityPhotoAnomaly6.jpg",
            "/com/example/demo/Images/SecurityPhotoNormal1.jpg",
            "/com/example/demo/Images/SecurityPhotoNormal2.jpeg",
            "/com/example/demo/Images/SecurityPhotoNormal3.jpg",
            "/com/example/demo/Images/SecurityPhotoNormal4.jpg",
            "/com/example/demo/Images/SecurityPhotoNormal5.jpg",
            "/com/example/demo/Images/SecurityPhotoNormal6.jpg"
    };

    // ===== POJOs para tablas =====
    public static final class DeviceRow {
        private final String deviceId, category; private final int count;
        public DeviceRow(String deviceId, String category, int count) {
            this.deviceId = deviceId; this.category = category; this.count = count;
        }
        public String getDeviceId() { return deviceId; }
        public String getCategory() { return category; }
        public int getCount() { return count; }
    }
    public static final class TypeRow {
        private final String category; private final int count;
        public TypeRow(String category, int count) { this.category = category; this.count = count; }
        public String getCategory() { return category; }
        public int getCount() { return count; }
    }

    // ===== init desde runner =====
    public void init(AppContext ctx) {
        this.ctx = ctx;
        this.violationService = ctx.violationService;
        this.violations = violationService.items();

        // Cuando el WebView/Leaflet ya cargó, conectamos y dibujamos dispositivos
        mapView.whenReady(() -> {
            mapView.setOnMarkerClick(id -> {
                // Por ahora, para otros tipos de devices solo mostramos un toast
                showMapToast(id);
            });
            mapView.setOnSecurityCameraClick(id -> {
                currentDeviceId = id;
                showRandomCameraImageFor(id);
            });
            // CARGA DESDE CLASSPATH (resources)
            try (var is = getClass().getResourceAsStream("/static/devices.json")) {
                if (is == null) {
                    System.err.println("devices.json NO encontrado en resources/com/example/demo/");
                    return;
                }
                String raw = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                mapView.addDevices(raw); // 1. Cargar marcadores en las capas JS

                // 2. Sincronizar la visibilidad inicial del mapa con el estado de los toggles
                if (swMap != null) mapView.setAllDevicesVisible(swMap.isSelected());
                if (swSemaphores != null) mapView.setGroupVisible("semaphores", swSemaphores.isSelected());
                if (swRadars != null) mapView.setGroupVisible("radars", swRadars.isSelected());
                if (swCameras != null) mapView.setGroupVisible("cameras", swCameras.isSelected());
                if (swFails != null) mapView.setGroupVisible("fails", swFails.isSelected());

                // 3. Ahora que el estado está sincronizado, añadir los listeners para futuras acciones del usuario
                if (swMap != null) swMap.selectedProperty().addListener((obs, o, n) -> mapView.setAllDevicesVisible(n));
                if (swSemaphores != null) swSemaphores.selectedProperty().addListener((obs, o, n) -> mapView.setGroupVisible("semaphores", n));
                if (swRadars != null) swRadars.selectedProperty().addListener((obs, o, n) -> mapView.setGroupVisible("radars", n));
                if (swCameras != null) swCameras.selectedProperty().addListener((obs, o, n) -> mapView.setGroupVisible("cameras", n));
                if (swFails != null) swFails.selectedProperty().addListener((obs, o, n) -> mapView.setGroupVisible("fails", n));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ===== initialize del FXML =====
    @FXML
    public void initialize() {
        // colocar el MapView pegado al AnchorPane
        mapView = new MapView();
        AnchorPane.setTopAnchor(mapView, 0.0);
        AnchorPane.setRightAnchor(mapView, 0.0);
        AnchorPane.setBottomAnchor(mapView, 0.0);
        AnchorPane.setLeftAnchor(mapView, 0.0);
        mapLayer.getChildren().add(0, mapView);

        // Establecer el estado inicial de los toggles. NO añadir listeners aquí.
        if (swMap != null) swMap.setSelected(true);
        if (swSemaphores != null) swSemaphores.setSelected(true);
        if (swRadars != null) swRadars.setSelected(true);
        if (swCameras != null) swCameras.setSelected(true);
        if (swFails != null) swFails.setSelected(false);

        // botones overlay
        if (btnPolice != null)    btnPolice.setOnAction(e -> serviceClicked("POLICE"));
        if (btnAmbulance != null) btnAmbulance.setOnAction(e -> serviceClicked("AMBULANCE"));
        if (btnFireman != null)   btnFireman.setOnAction(e -> serviceClicked("FIREMAN"));
        if (btnBack != null)      btnBack.setOnAction(e -> showMap());

        // menú / atajos
        if (menuViolationsView != null) menuViolationsView.setOnAction(e -> openViolationsWindow());
        if (menuReports != null)        menuReports.setOnAction(e -> openReportsWindow());
        stack.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> showMap();
                case V      -> openViolationsWindow();
                case R      -> openReportsWindow();
            }
        });
        stack.setFocusTraversable(true);
        stack.requestFocus();

        // vista inicial: mapa
        setNodeVisibleManaged(mapLayer, true);
        setNodeVisibleManaged(cameraLayer, false);
    }

    // ===== Overlay de cámara =====
    private void showRandomCameraImageFor(String deviceId) {
        if (cameraImage != null) {
            int idx = random.nextInt(images.length);
            URL url = getClass().getResource(images[idx]);
            if (url != null) cameraImage.setImage(new Image(url.toExternalForm()));
        }
        setNodeVisibleManaged(mapLayer, false);
        setNodeVisibleManaged(cameraLayer, true);
        showMapToast(deviceId);
        stack.requestFocus();
    }

    private void showMap() {
        currentDeviceId = null;
        setNodeVisibleManaged(cameraLayer, false);
        setNodeVisibleManaged(mapLayer, true);
        stack.requestFocus();
    }

    private void serviceClicked(String serviceName) {
        if (currentDeviceId == null) {
            showMapToast("Select a camera first");
            return;
        }
        // Guarda en el servicio (log de acciones)
        violationService.recordServiceCall(currentDeviceId, serviceName);
        showMapToast(currentDeviceId + " • SERVICE " + serviceName);
    }

    // ===== Toast (arriba-derecha) =====
    private SequentialTransition toastMapAnimation;
    private void showMapToast(String msg) {
        if (toastMap == null) return;
        toastMap.setText(msg);
        toastMap.setVisible(true); toastMap.setManaged(true); toastMap.setOpacity(0);
        if (toastMapAnimation != null) toastMapAnimation.stop();
        var fadeIn = new FadeTransition(Duration.millis(140), toastMap); fadeIn.setToValue(1);
        var stay   = new PauseTransition(Duration.seconds(1.2));
        var fadeOut= new FadeTransition(Duration.millis(180), toastMap); fadeOut.setToValue(0);
        toastMapAnimation = new SequentialTransition(fadeIn, stay, fadeOut);
        toastMapAnimation.setOnFinished(e -> { toastMap.setVisible(false); toastMap.setManaged(false); });
        toastMapAnimation.play();
    }
    private void setNodeVisibleManaged(Node n, boolean v) { if (n != null) { n.setVisible(v); n.setManaged(v); } }

    // ===== Violations window =====
    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private void openViolationsWindow() {
        if (violationsStage == null) {
            var view = new javafx.collections.transformation.FilteredList<>(
                    violationService.items(),
                    v -> v.type != ViolationService.Type.SERVICE_CALL
            );

            TableView<ViolationService.Violation> table = new TableView<>(view);
            TableColumn<ViolationService.Violation, String> c1 = new TableColumn<>("Time");
            c1.setCellValueFactory(d -> new SimpleStringProperty(TS_FMT.format(d.getValue().ts)));
            TableColumn<ViolationService.Violation, String> c2 = new TableColumn<>("Device");
            c2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().deviceId));
            TableColumn<ViolationService.Violation, String> c3 = new TableColumn<>("Type");
            c3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().type.name()));
            TableColumn<ViolationService.Violation, String> c4 = new TableColumn<>("Plate");
            c4.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().plate));
            TableColumn<ViolationService.Violation, String> c5 = new TableColumn<>("Details");
            c5.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().details));

            c1.setPrefWidth(160); c2.setPrefWidth(120); c3.setPrefWidth(140);
            c4.setPrefWidth(120); c5.setPrefWidth(280);
            table.getColumns().addAll(c1, c2, c3, c4, c5);

            violationsStage = new Stage();
            violationsStage.setTitle("Traffic Violations");
            violationsStage.setScene(new Scene(table, 820, 380));
        }
        violationsStage.show();
        violationsStage.toFront();
    }

    // ===== Reports (By Device / By Type) =====
    private void openReportsWindow() {
        if (reportsStage == null) {
            var tp = new TabPane();
            tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            // Tab 1
            devicesTable = new TableView<>();
            var d1 = new TableColumn<DeviceRow, String>("Device");
            d1.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDeviceId()));
            var d2 = new TableColumn<DeviceRow, String>("Category");
            d2.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategory()));
            var d3 = new TableColumn<DeviceRow, String>("Count");
            d3.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getCount())));
            d1.setPrefWidth(240); d2.setPrefWidth(160); d3.setPrefWidth(80);
            devicesTable.getColumns().addAll(d1, d2, d3);
            var t1 = new Tab("By Device", devicesTable);

            // Tab 2
            typesTable = new TableView<>();
            var t1c1 = new TableColumn<TypeRow, String>("Device Type");
            t1c1.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategory()));
            var t1c2 = new TableColumn<TypeRow, String>("Count");
            t1c2.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getCount())));
            t1c1.setPrefWidth(260); t1c2.setPrefWidth(100);
            typesTable.getColumns().addAll(t1c1, t1c2);
            var t2 = new Tab("By Type", typesTable);

            tp.getTabs().addAll(t1, t2);

            reportsStage = new Stage();
            reportsStage.setTitle("Reports");
            reportsStage.setScene(new Scene(tp, 540, 420));

            // refresco automático
            violations.addListener((javafx.collections.ListChangeListener<? super ViolationService.Violation>) c -> refreshReportsTables());
        }

        refreshReportsTables();
        reportsStage.show();
        reportsStage.toFront();
    }

    private void refreshReportsTables() {
        // contar por device a partir del log real
        Map<String, Integer> byDevice = new LinkedHashMap<>();
        for (var v : violationService.items()) {
            byDevice.merge(v.deviceId, 1, Integer::sum);
        }
        // filas By Device
        var devRows = FXCollections.<DeviceRow>observableArrayList();
        for (var e : byDevice.entrySet()) {
            devRows.add(new DeviceRow(e.getKey(), deviceCategory(e.getKey()), e.getValue()));
        }
        devRows.sort((a,b) -> Integer.compare(b.getCount(), a.getCount()));
        devicesTable.setItems(devRows);

        // By Type
        Map<String, Integer> byCat = new LinkedHashMap<>();
        for (var r : devRows) byCat.merge(r.getCategory(), r.getCount(), Integer::sum);
        var typeRows = FXCollections.<TypeRow>observableArrayList();
        for (var e : byCat.entrySet()) typeRows.add(new TypeRow(e.getKey(), e.getValue()));
        typeRows.sort((a,b) -> Integer.compare(b.getCount(), a.getCount()));
        typesTable.setItems(typeRows);
    }

    private static String deviceCategory(String id) {
        if (id == null) return "Unknown";
        String s = id.toLowerCase(Locale.ROOT);
        if (s.contains("radar"))           return "Radar";
        if (s.contains("parking"))         return "Parking Camera";
        if (s.contains("camera"))          return "Security Camera";
        if (s.contains("semaphore") || s.startsWith("int-")) return "Traffic Light";
        return "Unknown";
    }
}
