package com.pushknight.views;

import com.pushknight.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
 * View class for the "How to Play" screen.
 * Displays game instructions, controls, and enemy information.
 */
public class HowToPlayView {
    private VBox root;
    private Scene scene;
    private Button backButton;
    private Text titleText;
    private ScrollPane scrollPane;
    private VBox contentBox;
    
    /**
     * Creates a new HowToPlayView.
     */
    public HowToPlayView() {
        createHowToPlayScreen();
    }
    
    /**
     * Creates the "How to Play" screen UI.
     */
    private void createHowToPlayScreen() {
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
        titleText = new Text("HOW TO PLAY");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleText.setFill(Color.GOLD);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);
        
        // Create content box with instructions
        contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_LEFT);
        
        addSection("MOVEMENT CONTROLS", 
            "• Use WASD or Arrow Keys to move your knight\n" +
            "• Movement has a cooldown - you can't move too quickly\n" +
            "• Stay within the playing field boundaries");
        
        addSection("PUSH ABILITY", 
            "• Press SPACE to push nearby enemies\n" +
            "• Push enemies into traps to deal damage\n" +
            "• Push ability has a cooldown - use it strategically");
        
        addSection("TRAPS", 
            "• Place traps strategically to damage enemies\n" +
            "• Traps have a cooldown between activations\n" +
            "• Spike Traps deal instant damage to enemies that step on them\n" +
            "• You have a limited number of traps available");
        
        addSection("UPGRADE SYSTEM", 
            "• After completing each wave, choose an upgrade\n" +
            "• Upgrades can increase health, speed, damage, and more\n" +
            "• Choose upgrades that match your playstyle");
        
        addSection("ENEMY TYPES", 
            "• Skeleton: Basic enemy with moderate health and speed\n" +
            "• Goblin: Faster but weaker than skeletons\n" +
            "• Skeleton Brute: Slow but tanky, acts as an obstacle\n" +
            "• Boomer Goblin: Explodes on death, dealing area damage");
        
        addSection("OBJECTIVE", 
            "• Survive as many waves as possible\n" +
            "• Defeat enemies to earn score\n" +
            "• Use traps and positioning to your advantage\n" +
            "• Don't let enemies touch you!");
        
        // Create scroll pane for content
        scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefHeight(Constants.WINDOW_HEIGHT - 200);
        
        // Create back button
        backButton = createMenuButton("Back", 200, 40);
        
        // Add all elements to root
        root.getChildren().addAll(
            titleText,
            scrollPane,
            backButton
        );
        
        // Create scene
        scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }
    
    /**
     * Adds a section with title and content to the content box.
     * 
     * @param title The section title
     * @param content The section content
     */
    private void addSection(String title, String content) {
        // Section title
        Text sectionTitle = new Text(title);
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sectionTitle.setFill(Color.GOLD);
        sectionTitle.setStroke(Color.BLACK);
        sectionTitle.setStrokeWidth(1);
        
        // Section content
        Label sectionContent = new Label(content);
        sectionContent.setFont(Font.font("Arial", 14));
        sectionContent.setTextFill(Color.WHITE);
        sectionContent.setWrapText(true);
        sectionContent.setPadding(new Insets(5, 0, 15, 0));
        
        contentBox.getChildren().addAll(sectionTitle, sectionContent);
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
     * Gets the back button.
     * 
     * @return The back button
     */
    public Button getBackButton() {
        return backButton;
    }
    
    /**
     * Gets the "How to Play" scene.
     * 
     * @return The "How to Play" scene
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

