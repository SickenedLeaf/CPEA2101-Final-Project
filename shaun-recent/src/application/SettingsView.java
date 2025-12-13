package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SettingsView {
    private Scene scene;
    private VBox layout;

    private Runnable onBackSelected;

    // Volume sliders
    private Slider musicVolumeSlider;
    private Slider sfxVolumeSlider;

    public SettingsView(int width, int height) {
        layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #0f3460;");

        // Title
        Text title = new Text("SETTINGS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.WHITE);

        // Music volume control
        Label musicLabel = new Label("Music Volume");
        musicLabel.setTextFill(Color.LIGHTGRAY);
        musicLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        musicVolumeSlider = new Slider(0, 100, AudioManager.getInstance().getMusicVolume() * 100); // Initialize from AudioManager
        musicVolumeSlider.setPrefWidth(300);
        musicVolumeSlider.setShowTickLabels(true);
        musicVolumeSlider.setShowTickMarks(true);
        musicVolumeSlider.setMajorTickUnit(25);
        musicVolumeSlider.setMinorTickCount(5);

        // Add listener to update music volume in real-time
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            AudioManager.getInstance().setMusicVolume(newVal.doubleValue() / 100.0);
        });

        // SFX volume control
        Label sfxLabel = new Label("Sound Effects Volume");
        sfxLabel.setTextFill(Color.LIGHTGRAY);
        sfxLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        sfxVolumeSlider = new Slider(0, 100, AudioManager.getInstance().getSfxVolume() * 100); // Initialize from AudioManager
        sfxVolumeSlider.setPrefWidth(300);
        sfxVolumeSlider.setShowTickLabels(true);
        sfxVolumeSlider.setShowTickMarks(true);
        sfxVolumeSlider.setMajorTickUnit(25);
        sfxVolumeSlider.setMinorTickCount(5);

        // Add listener to update SFX volume in real-time
        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            AudioManager.getInstance().setSfxVolume(newVal.doubleValue() / 100.0);
        });

        // Back button
        Button backBtn = new Button("Back");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        backBtn.setPrefWidth(200);
        backBtn.setPrefHeight(50);
        backBtn.setStyle(
            "-fx-background-color: #16213e; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #0f3460; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );

        backBtn.setOnMouseEntered(e -> {
            backBtn.setStyle(
                "-fx-background-color: #1a1a2e; " +
                "-fx-text-fill: cyan; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: cyan; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;"
            );
            // Play submenu navigation sound
            AudioManager.getInstance().playUiSound("NAV2");
        });

        backBtn.setOnMouseExited(e -> {
            backBtn.setStyle(
                "-fx-background-color: #16213e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #0f3460; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;"
            );
        });

        backBtn.setOnAction(e -> {
            if (onBackSelected != null) onBackSelected.run();
            // Play navigation confirmation sound
            AudioManager.getInstance().playUiSound("CONFIRM");
        });

        layout.getChildren().addAll(
            title,
            musicLabel,
            musicVolumeSlider,
            sfxLabel,
            sfxVolumeSlider,
            backBtn
        );

        scene = new Scene(layout, width, height);
    }

    public Scene getScene() { return scene; }

    public void setOnBackSelected(Runnable callback) {
        this.onBackSelected = callback;
    }

    // Getters for volume values - delegate to AudioManager
    public double getMusicVolume() {
        return AudioManager.getInstance().getMusicVolume();
    }

    public double getSfxVolume() {
        return AudioManager.getInstance().getSfxVolume();
    }

    // Setters for volume values (for loading saved settings)
    public void setMusicVolume(double volume) {
        musicVolumeSlider.setValue(volume * 100);  // This will trigger the listener
    }

    public void setSfxVolume(double volume) {
        sfxVolumeSlider.setValue(volume * 100);  // This will trigger the listener
    }
}