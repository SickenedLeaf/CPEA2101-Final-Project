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
 * View class for the game over screen.
 * Displays final statistics and options to retry or return to main menu.
 */
public class GameOverView {
    private VBox root;
    private Scene scene;
    private Button retryButton;
    private Button mainMenuButton;
    private Text titleText;
    private Label finalScoreLabel;
    private Label waveReachedLabel;
    private Label enemiesDefeatedLabel;
    private Label timeSurvivedLabel;
    
    /**
     * Creates a new GameOverView.
     */
    public GameOverView() {
        createGameOverScreen();
    }
    
    /**
     * Creates the game over screen UI.
     */
    private void createGameOverScreen() {
        // Create root VBox
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setBackground(new Background(new BackgroundFill(
            Color.DARKSLATEGRAY,
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));
        
        // Create title
        titleText = new Text("GAME OVER");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        titleText.setFill(Color.RED);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);
        
        // Create stats labels
        finalScoreLabel = createStatLabel("Final Score: 0");
        waveReachedLabel = createStatLabel("Wave Reached: 0");
        enemiesDefeatedLabel = createStatLabel("Enemies Defeated: 0");
        timeSurvivedLabel = createStatLabel("Time Survived: 0:00");
        
        // Create buttons
        retryButton = createMenuButton("Retry", 200, 40);
        mainMenuButton = createMenuButton("Main Menu", 200, 40);
        
        // Add all elements to VBox
        root.getChildren().addAll(
            titleText,
            finalScoreLabel,
            waveReachedLabel,
            enemiesDefeatedLabel,
            timeSurvivedLabel,
            retryButton,
            mainMenuButton
        );
        
        // Create scene
        scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }
    
    /**
     * Creates a styled stat label.
     * 
     * @param text The label text
     * @return The created label
     */
    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);
        return label;
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
     * Updates the game over screen with final statistics.
     * 
     * @param finalScore The final score
     * @param waveReached The wave reached
     * @param enemiesDefeated The number of enemies defeated
     * @param timeSurvived The time survived in seconds
     */
    public void updateStats(int finalScore, int waveReached, int enemiesDefeated, double timeSurvived) {
        finalScoreLabel.setText("Final Score: " + finalScore);
        waveReachedLabel.setText("Wave Reached: " + waveReached);
        enemiesDefeatedLabel.setText("Enemies Defeated: " + enemiesDefeated);
        
        int minutes = (int) (timeSurvived / 60);
        int seconds = (int) (timeSurvived % 60);
        timeSurvivedLabel.setText(String.format("Time Survived: %02d:%02d", minutes, seconds));
    }
    
    /**
     * Gets the retry button.
     * 
     * @return The retry button
     */
    public Button getRetryButton() {
        return retryButton;
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
     * Gets the game over scene.
     * 
     * @return The game over scene
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

