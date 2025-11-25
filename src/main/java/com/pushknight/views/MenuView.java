package com.pushknight.views;

import com.pushknight.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
 * View class for the main menu screen.
 * Creates and manages the menu UI elements.
 */
public class MenuView {
    private VBox root;
    private Scene scene;
    private Button startButton;
    private Button howToPlayButton;
    private Button settingsButton;
    private Button quitButton;
    private Text titleText;
    
    /**
     * Creates a new MenuView.
     */
    public MenuView() {
        createMenu();
    }
    
    /**
     * Creates the menu UI.
     */
    private void createMenu() {
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
        titleText = new Text("PUSH KNIGHT PERIL");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleText.setFill(Color.GOLD);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);
        
        // Create buttons
        startButton = createMenuButton("Start Game", 200, 40);
        howToPlayButton = createMenuButton("How to Play", 200, 40);
        settingsButton = createMenuButton("Settings", 200, 40);
        quitButton = createMenuButton("Quit", 200, 40);
        
        // Add all elements to VBox
        root.getChildren().addAll(
            titleText,
            startButton,
            howToPlayButton,
            settingsButton,
            quitButton
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
     * Gets the start game button.
     * 
     * @return The start button
     */
    public Button getStartButton() {
        return startButton;
    }
    
    /**
     * Gets the how to play button.
     * 
     * @return The how to play button
     */
    public Button getHowToPlayButton() {
        return howToPlayButton;
    }
    
    /**
     * Gets the settings button.
     * 
     * @return The settings button
     */
    public Button getSettingsButton() {
        return settingsButton;
    }
    
    /**
     * Gets the quit button.
     * 
     * @return The quit button
     */
    public Button getQuitButton() {
        return quitButton;
    }
    
    /**
     * Gets the menu scene.
     * 
     * @return The menu scene
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

