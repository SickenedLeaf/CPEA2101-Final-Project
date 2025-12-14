package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameOverView {
    private Pane root;

    // Callbacks for button actions
    private Runnable onRestartCallback;
    private Runnable onMenuCallback;
    private Runnable onLevelSelectCallback;

    public GameOverView() {
        initialize();
    }

    private void initialize() {
        root = new StackPane();
        root.setStyle("-fx-background-color: #0f3460;");

        VBox container = new VBox(30);
        container.setAlignment(Pos.CENTER);

        // Title
        Text title = new Text("GAME OVER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        title.setFill(Color.WHITE);

        // Subtitle
        Text subtitle = new Text("Your adventure has ended");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        subtitle.setFill(Color.LIGHTGRAY);

        // Buttons
        Button restartButton = new Button("Restart Level");
        Button levelSelectButton = new Button("Level Select");
        Button menuButton = new Button("Main Menu");

        // Style buttons to match other menus
        setupButtonStyle(restartButton);
        setupButtonStyle(levelSelectButton);
        setupButtonStyle(menuButton);

        restartButton.setOnAction(e -> {
            AudioManager.getInstance().playUiSound("CONFIRM");
            if (onRestartCallback != null) {
                onRestartCallback.run();
            }
        });

        levelSelectButton.setOnAction(e -> {
            AudioManager.getInstance().playUiSound("CONFIRM");
            if (onLevelSelectCallback != null) {
                onLevelSelectCallback.run();
            }
        });

        menuButton.setOnAction(e -> {
            AudioManager.getInstance().playUiSound("CONFIRM");
            if (onMenuCallback != null) {
                onMenuCallback.run();
            }
        });

        VBox buttonsContainer = new VBox(15);
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.getChildren().addAll(restartButton, levelSelectButton, menuButton);

        container.getChildren().addAll(title, subtitle, buttonsContainer);
        container.setPadding(new Insets(50));

        root.getChildren().add(container);

        // Add background pattern similar to other menus
        addBackgroundPattern();
    }

    private void setupButtonStyle(Button button) {
        button.setPrefSize(250, 50);
        button.setStyle(
            "-fx-background-color: #1a1a2e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-border-color: #e94560; " +
            "-fx-border-width: 2px; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10;"
        );

        button.setOnMouseEntered(e -> {
            AudioManager.getInstance().playUiSound("NAV1");
            button.setStyle(
                "-fx-background-color: #e94560; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #1a1a2e; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: #e94560; " +
                "-fx-border-width: 2px; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10;"
            );
        });
    }

    private void addBackgroundPattern() {
        // This would add decorative elements similar to other menu views
    }


    public void setOnRestartCallback(Runnable callback) {
        this.onRestartCallback = callback;
    }

    public void setOnMenuCallback(Runnable callback) {
        this.onMenuCallback = callback;
    }

    public void setOnLevelSelectCallback(Runnable callback) {
        this.onLevelSelectCallback = callback;
    }

    public Pane getRoot() {
        return root;
    }
}