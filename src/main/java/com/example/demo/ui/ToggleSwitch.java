package com.example.demo.ui;

import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToggleSwitch extends StackPane {
    private static final double TRACK_W = 44;
    private static final double TRACK_H = 24;
    private static final double THUMB  = 18;
    private static final double TRAVEL = (TRACK_W - THUMB) / 2.0; // 13 px

    private final Region track = new Region();
    private final Region thumb = new Region();
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public ToggleSwitch() {
        // tamaños
        setMinSize(TRACK_W, TRACK_H);
        setPrefSize(TRACK_W, TRACK_H);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        track.setMinSize(TRACK_W, TRACK_H);
        track.setPrefSize(TRACK_W, TRACK_H);
        track.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        thumb.setMinSize(THUMB, THUMB);
        thumb.setPrefSize(THUMB, THUMB);
        thumb.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        setPadding(new Insets(0));
        getChildren().addAll(track, thumb);

        // estilos iniciales (OFF)
        applyStyle(false, false);

        // toggle con click/teclado
        setOnMouseClicked(e -> setSelected(!isSelected()));
        setFocusTraversable(true);
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) setSelected(!isSelected());
        });

        // cuando cambia el estado, animá
        selected.addListener((obs, oldV, sel) -> applyStyle(sel, true));
    }

    private void applyStyle(boolean on, boolean animateThumb) {
        track.setStyle("-fx-background-color:" + (on ? "#7c3aed" : "#2b2b2b")
                + "; -fx-background-radius: 12;");
        thumb.setStyle("-fx-background-color: white; -fx-background-radius: 9;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 6, 0.3, 0, 1);");

        double toX = on ? TRAVEL : -TRAVEL;
        if (animateThumb) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(140), thumb);
            tt.setToX(toX);
            tt.play();
        } else {
            thumb.setTranslateX(toX);
        }
    }

    // API pública
    public BooleanProperty selectedProperty() { return selected; }
    public boolean isSelected()               { return selected.get(); }
    public void setSelected(boolean value)    { selected.set(value); }
}
