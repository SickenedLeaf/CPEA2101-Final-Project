package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Level selection menu view.
 */
public class LevelSelectView {
    private Scene scene;
    private VBox layout;
    
    private Runnable onLevel1Selected;
    private Runnable onLevel2Selected;
    private Runnable onLevel3Selected;
    private Runnable onEndlessModeSelected;
    private Runnable onBack;
    
    public LevelSelectView(int width, int height) {
        layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #0f3460;");
        
        // Title
        Text title = new Text("PUSH KNIGHT PERIL");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("Select Your Challenge");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        subtitle.setFill(Color.LIGHTGRAY);
        
        // Level buttons
        Button level1Btn = createLevelButton("Level 1: Beginner", 
            "5 Waves • Easy Enemies");
        Button level2Btn = createLevelButton("Level 2: Intermediate", 
            "7 Waves • Mixed Enemies");
        Button level3Btn = createLevelButton("Level 3: Advanced", 
            "10 Waves • Challenging");
        Button endlessBtn = createLevelButton("Endless Mode", 
            "Survive as long as you can!");
        
        // Set button actions
        level1Btn.setOnAction(e -> {
            if (onLevel1Selected != null) onLevel1Selected.run();
        });
        
        level2Btn.setOnAction(e -> {
            if (onLevel2Selected != null) onLevel2Selected.run();
        });
        
        level3Btn.setOnAction(e -> {
            if (onLevel3Selected != null) onLevel3Selected.run();
        });
        
        endlessBtn.setOnAction(e -> {
            if (onEndlessModeSelected != null) onEndlessModeSelected.run();
        });
        
        // Instructions
        Text instructions = new Text("Use Arrow Keys or WASD to move and push enemies\nPress SPACE for push ability");
        instructions.setFont(Font.font("Arial", 14));
        instructions.setFill(Color.LIGHTGRAY);
        instructions.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        layout.getChildren().addAll(
            title,
            subtitle,
            level1Btn,
            level2Btn,
            level3Btn,
            endlessBtn,
            instructions
        );
        
        scene = new Scene(layout, width, height);
    }
    
    /**
     * Creates a styled level button.
     */
    private Button createLevelButton(String mainText, String subText) {
        Button btn = new Button(mainText + "\n" + subText);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btn.setPrefWidth(400);
        btn.setPrefHeight(80);
        btn.setStyle(
            "-fx-background-color: #16213e; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #0f3460; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #1a1a2e; " +
                "-fx-text-fill: cyan; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: cyan; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;"
            );
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: #16213e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #0f3460; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;"
            );
        });
        
        return btn;
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public void setOnLevel1Selected(Runnable callback) {
        this.onLevel1Selected = callback;
    }
    
    public void setOnLevel2Selected(Runnable callback) {
        this.onLevel2Selected = callback;
    }
    
    public void setOnLevel3Selected(Runnable callback) {
        this.onLevel3Selected = callback;
    }
    
    public void setOnEndlessModeSelected(Runnable callback) {
        this.onEndlessModeSelected = callback;
    }
    
    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}
