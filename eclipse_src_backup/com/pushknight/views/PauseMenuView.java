package com.pushknight.views;

import com.pushknight.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
 * View class for the pause menu screen.
 * Displays pause menu overlay with resume, restart, and main menu options.
 */
public class PauseMenuView {
    private VBox root;
    private Scene scene;
    private Button resumeButton;
    private Button restartButton;
    private Button mainMenuButton;
    private Text titleText;
    private Label statsLabel;
    
    /**
     * Creates a new PauseMenuView.
     */
    public PauseMenuView() {
        createPauseMenu();
    }
    
    /**
     * Creates the pause menu UI.
     */
    private void createPauseMenu() {
        // Create root VBox
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setBackground(new Background(new BackgroundFill(
            Color.rgb(0, 0, 0, 0.8), // Semi-transparent black overlay
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));
        
        // Create title
        titleText = new Text("PAUSED");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleText.setFill(Color.GOLD);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);
        
        // Create stats label (will be updated by controller)
        statsLabel = new Label();
        statsLabel.setFont(Font.font("Arial", 14));
        statsLabel.setTextFill(Color.WHITE);
        statsLabel.setAlignment(Pos.CENTER);
        updateStats(0, 0, 0.0);
        
        // Create buttons
        resumeButton = createMenuButton("Resume", 200, 40);
        restartButton = createMenuButton("Restart", 200, 40);
        mainMenuButton = createMenuButton("Main Menu", 200, 40);
        
        // Add all elements to VBox
        root.getChildren().addAll(
            titleText,
            statsLabel,
            resumeButton,
            restartButton,
            mainMenuButton
        );
        
        // Create scene
        scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
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
    
    /**
     * Updates the stats display.
     * 
     * @param wave The current wave number
     * @param score The current score
     * @param timeSurvived The time survived in seconds
     */
    public void updateStats(int wave, int score, double timeSurvived) {
        int minutes = (int) (timeSurvived / 60);
        int seconds = (int) (timeSurvived % 60);
        statsLabel.setText(String.format(
            "Wave: %d | Score: %d | Time: %02d:%02d",
            wave, score, minutes, seconds
        ));
    }
    
    /**
     * Gets the resume button.
     * 
     * @return The resume button
     */
    public Button getResumeButton() {
        return resumeButton;
    }
    
    /**
     * Gets the restart button.
     * 
     * @return The restart button
     */
    public Button getRestartButton() {
        return restartButton;
    }
    
    /**
     * Gets the main menu button.
     * 
     * @return The main menu button
     */
    public Button getMainMenuButton() {
        return mainMenuButton;
    }
    
    /**
     * Gets the pause menu scene.
     * 
     * @return The pause menu scene
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * Gets the root VBox.
     * 
     * @return The root VBox
     */
    public VBox getRoot() {
        return root;
    }
}

