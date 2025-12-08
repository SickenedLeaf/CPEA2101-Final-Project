package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Level selection screen where players choose which level to play.
 */
public class LevelSelectView {
    private VBox root;
    private Scene scene;
    private Button level1Button;
    private Button level2Button;
    private Button level3Button;
    private Button backButton;
    
    private Runnable onLevel1Selected;
    private Runnable onLevel2Selected;
    private Runnable onLevel3Selected;
    private Runnable onBack;
    
    public LevelSelectView(int windowWidth, int windowHeight) {
        createLevelSelectScreen(windowWidth, windowHeight);
    }
    
    private void createLevelSelectScreen(int windowWidth, int windowHeight) {
        root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setBackground(new Background(new BackgroundFill(
            Color.web("#1a1a2e"),
            CornerRadii.EMPTY,
            Insets.EMPTY
        )));
        
        // Title
        Text title = new Text("SELECT LEVEL");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.web("#ffd700"));
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(2);
        
        // Level buttons container
        HBox levelContainer = new HBox(30);
        levelContainer.setAlignment(Pos.CENTER);
        
        // Level 1
        level1Button = createLevelCard(
            "LEVEL 1",
            "5 Waves\n15-20 Enemies/Wave\nGoblins, Skeletons, Boomers",
            Color.web("#2ecc71")
        );
        
        // Level 2
        level2Button = createLevelCard(
            "LEVEL 2",
            "7 Waves\n25-30 Enemies/Wave\nAll Enemy Types\n1 Brute per Wave (5-7)",
            Color.web("#f39c12")
        );
        
        // Level 3
        level3Button = createLevelCard(
            "LEVEL 3",
            "10 Waves\n35-45 Enemies/Wave\nIntense Spawns\nMax 5 Brutes",
            Color.web("#e74c3c")
        );
        
        levelContainer.getChildren().addAll(level1Button, level2Button, level3Button);
        
        // Back button
        backButton = createMenuButton("Back to Main Menu", 250, 45);
        
        root.getChildren().addAll(title, levelContainer, backButton);
        scene = new Scene(root, windowWidth, windowHeight);
        
        setupEventHandlers();
    }
    
    private Button createLevelCard(String levelName, String description, Color accentColor) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(250, 300);
        card.setStyle(
            "-fx-background-color: #2c3e50; " +
            "-fx-border-color: " + toRgbString(accentColor) + "; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px;"
        );
        
        // Level name
        Label nameLabel = new Label(levelName);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLabel.setTextFill(accentColor);
        
        // Divider
        Region divider = new Region();
        divider.setPrefHeight(2);
        divider.setMaxWidth(200);
        divider.setStyle("-fx-background-color: " + toRgbString(accentColor) + ";");
        
        // Description
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setTextFill(Color.WHITE);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(220);
        descLabel.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(nameLabel, divider, descLabel);
        
        Button button = new Button();
        button.setGraphic(card);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: #34495e; " +
                "-fx-border-color: " + toRgbString(accentColor) + "; " +
                "-fx-border-width: 4px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, " + toRgbString(accentColor) + ", 15, 0.5, 0, 0);"
            );
        });
        
        button.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: #2c3e50; " +
                "-fx-border-color: " + toRgbString(accentColor) + "; " +
                "-fx-border-width: 3px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px;"
            );
        });
        
        return button;
    }
    
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
    
    private void setupEventHandlers() {
        level1Button.setOnAction(e -> {
            if (onLevel1Selected != null) onLevel1Selected.run();
        });
        
        level2Button.setOnAction(e -> {
            if (onLevel2Selected != null) onLevel2Selected.run();
        });
        
        level3Button.setOnAction(e -> {
            if (onLevel3Selected != null) onLevel3Selected.run();
        });
        
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });
    }
    
    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255)
        );
    }
    
    // Setters for callbacks
    public void setOnLevel1Selected(Runnable callback) { this.onLevel1Selected = callback; }
    public void setOnLevel2Selected(Runnable callback) { this.onLevel2Selected = callback; }
    public void setOnLevel3Selected(Runnable callback) { this.onLevel3Selected = callback; }
    public void setOnBack(Runnable callback) { this.onBack = callback; }
    
    public Scene getScene() { return scene; }
}