package com.pushknight.views;

import com.pushknight.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * View class for the settings screen.
 * Displays settings options for audio, difficulty, and other game preferences.
 */
public class SettingsView {
    private VBox root;
    private Scene scene;
    private Button backButton;
    private Button saveButton;
    private Text titleText;
    
    // Audio controls
    private Slider soundEffectVolumeSlider;
    private Slider musicVolumeSlider;
    private CheckBox soundEffectsEnabledCheckBox;
    private CheckBox musicEnabledCheckBox;
    
    // Game settings
    private CheckBox fullscreenCheckBox;
    
    /**
     * Creates a new SettingsView.
     */
    public SettingsView() {
        createSettingsScreen();
    }
    
    /**
     * Creates the settings screen UI.
     */
    private void createSettingsScreen() {
        // Create root VBox
        root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setBackground(new Background(new BackgroundFill(
            Color.DARKSLATEGRAY,
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));
        
        // Create title
        titleText = new Text("SETTINGS");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleText.setFill(Color.GOLD);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);
        
        // Create audio section
        VBox audioSection = createAudioSection();
        
        // Create game settings section
        VBox gameSection = createGameSection();
        
        // Create buttons
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        saveButton = createMenuButton("Save Settings", 200, 40);
        backButton = createMenuButton("Back", 200, 40);
        buttonBox.getChildren().addAll(saveButton, backButton);
        
        // Add all elements to root
        root.getChildren().addAll(
            titleText,
            audioSection,
            gameSection,
            buttonBox
        );
        
        // Create scene
        scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }
    
    /**
     * Creates the audio settings section.
     * 
     * @return VBox containing audio controls
     */
    private VBox createAudioSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));
        
        Text sectionTitle = new Text("AUDIO SETTINGS");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        sectionTitle.setFill(Color.WHITE);
        
        // Sound effect volume
        Label soundEffectLabel = new Label("Sound Effects Volume:");
        soundEffectLabel.setFont(Font.font("Arial", 14));
        soundEffectLabel.setTextFill(Color.WHITE);
        
        soundEffectVolumeSlider = new Slider(0.0, 1.0, 0.7);
        soundEffectVolumeSlider.setShowTickLabels(true);
        soundEffectVolumeSlider.setShowTickMarks(true);
        soundEffectVolumeSlider.setMajorTickUnit(0.25);
        soundEffectVolumeSlider.setPrefWidth(300);
        
        soundEffectsEnabledCheckBox = new CheckBox("Enable Sound Effects");
        soundEffectsEnabledCheckBox.setSelected(true);
        soundEffectsEnabledCheckBox.setTextFill(Color.WHITE);
        soundEffectsEnabledCheckBox.setFont(Font.font("Arial", 14));
        
        // Music volume
        Label musicLabel = new Label("Music Volume:");
        musicLabel.setFont(Font.font("Arial", 14));
        musicLabel.setTextFill(Color.WHITE);
        
        musicVolumeSlider = new Slider(0.0, 1.0, 0.5);
        musicVolumeSlider.setShowTickLabels(true);
        musicVolumeSlider.setShowTickMarks(true);
        musicVolumeSlider.setMajorTickUnit(0.25);
        musicVolumeSlider.setPrefWidth(300);
        
        musicEnabledCheckBox = new CheckBox("Enable Music");
        musicEnabledCheckBox.setSelected(true);
        musicEnabledCheckBox.setTextFill(Color.WHITE);
        musicEnabledCheckBox.setFont(Font.font("Arial", 14));
        
        section.getChildren().addAll(
            sectionTitle,
            soundEffectLabel,
            soundEffectVolumeSlider,
            soundEffectsEnabledCheckBox,
            musicLabel,
            musicVolumeSlider,
            musicEnabledCheckBox
        );
        
        return section;
    }
    
    /**
     * Creates the game settings section.
     * 
     * @return VBox containing game controls
     */
    private VBox createGameSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));
        
        Text sectionTitle = new Text("GAME SETTINGS");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        sectionTitle.setFill(Color.WHITE);
        
        fullscreenCheckBox = new CheckBox("Fullscreen");
        fullscreenCheckBox.setSelected(false);
        fullscreenCheckBox.setTextFill(Color.WHITE);
        fullscreenCheckBox.setFont(Font.font("Arial", 14));
        
        section.getChildren().addAll(sectionTitle, fullscreenCheckBox);
        
        return section;
    }
    
    /**
     * Creates a styled menu button.
     * 
     * @param text The button text
     * @param width The button width
     * @param height The button height
     * @return The created button
     */
    private Button createMenuButton(String text, double width, double height) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle(
            "-fx-background-color: #4a4a4a; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: #666666; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #5a5a5a; " +
                "-fx-text-fill: #ffd700; " +
                "-fx-border-color: #888888; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #4a4a4a; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #666666; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;"
            );
        });
        
        return button;
    }
    
    // Getters for settings values
    
    public double getSoundEffectVolume() {
        return soundEffectVolumeSlider.getValue();
    }
    
    public void setSoundEffectVolume(double volume) {
        soundEffectVolumeSlider.setValue(volume);
    }
    
    public double getMusicVolume() {
        return musicVolumeSlider.getValue();
    }
    
    public void setMusicVolume(double volume) {
        musicVolumeSlider.setValue(volume);
    }
    
    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabledCheckBox.isSelected();
    }
    
    public void setSoundEffectsEnabled(boolean enabled) {
        soundEffectsEnabledCheckBox.setSelected(enabled);
    }
    
    public boolean isMusicEnabled() {
        return musicEnabledCheckBox.isSelected();
    }
    
    public void setMusicEnabled(boolean enabled) {
        musicEnabledCheckBox.setSelected(enabled);
    }
    
    public boolean isFullscreen() {
        return fullscreenCheckBox.isSelected();
    }
    
    public void setFullscreen(boolean fullscreen) {
        fullscreenCheckBox.setSelected(fullscreen);
    }
    
    public Button getBackButton() {
        return backButton;
    }
    
    public Button getSaveButton() {
        return saveButton;
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public VBox getRoot() {
        return root;
    }
}

