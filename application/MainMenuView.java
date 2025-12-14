package application;

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

public class MainMenuView {
    private Scene scene;
    private VBox layout;

    private Runnable onPlaySelected;
    private Runnable onSettingsSelected;
    private Runnable onQuitSelected;

    public MainMenuView(int width, int height) {
        layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #0f3460;");

        // Center the content but allow it to expand
        layout.setMaxWidth(width);
        layout.setMaxHeight(height);

        // Title
        Text title = new Text("PUSH KNIGHT PERIL");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        title.setFill(Color.WHITE);

        // Subtitle
        Text subtitle = new Text("Defend the Realm");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        subtitle.setFill(Color.LIGHTGRAY);

        // Menu buttons
        Button playBtn = createMenuButton("Play");
        Button settingsBtn = createMenuButton("Settings");
        Button quitBtn = createMenuButton("Quit");

        // Button actions
        playBtn.setOnAction(e -> {
            // Play accept sound then run the callback
            AudioManager.getInstance().playUiSound("ACCEPT");
            if (onPlaySelected != null) onPlaySelected.run();
        });

        settingsBtn.setOnAction(e -> {
            AudioManager.getInstance().playUiSound("CONFIRM");
            if (onSettingsSelected != null) onSettingsSelected.run();
        });

        quitBtn.setOnAction(e -> {
            AudioManager.getInstance().playUiSound("CONFIRM");
            if (onQuitSelected != null) onQuitSelected.run();
        });

        layout.getChildren().addAll(
            title,
            subtitle,
            playBtn,
            settingsBtn,
            quitBtn
        );

        scene = new Scene(layout, width, height);

        // Make the scene responsive to window size changes
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            layout.setMaxWidth((Double) newVal);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            layout.setMaxHeight((Double) newVal);
        });
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        btn.setPrefWidth(300);
        btn.setPrefHeight(60);
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
            // Play navigation sound on hover
            AudioManager.getInstance().playUiSound("NAV1");
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

        // Click handling is set by the view constructor to allow combining
        // sound playback with navigation callbacks.

        return btn;
    }

    public Scene getScene() { return scene; }

    public void setOnPlaySelected(Runnable callback) {
        this.onPlaySelected = callback;
    }

    public void setOnSettingsSelected(Runnable callback) {
        this.onSettingsSelected = callback;
    }

    public void setOnQuitSelected(Runnable callback) {
        this.onQuitSelected = callback;
    }
}