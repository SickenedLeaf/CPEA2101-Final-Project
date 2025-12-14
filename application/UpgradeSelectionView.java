package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.util.List;
import java.util.function.Consumer;

public class UpgradeSelectionView {
    private Stage stage;
    private Consumer<UpgradeManager.UpgradeType> onUpgradeSelected;

    public UpgradeSelectionView(List<UpgradeManager.Upgrade> upgrades, Consumer<UpgradeManager.UpgradeType> onUpgradeSelected) {
        this.onUpgradeSelected = onUpgradeSelected;

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Choose Your Upgrade");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 30;");

        Text title = new Text("📖 BOOK COLLECTED!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setFill(Color.GOLD);

        Text subtitle = new Text("Choose Your Upgrade");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitle.setFill(Color.LIGHTGRAY);

        HBox cardContainer = new HBox(20);
        cardContainer.setAlignment(Pos.CENTER);

        // Create 3 upgrade cards
        for (UpgradeManager.Upgrade upgrade : upgrades) {
            VBox card = createUpgradeCard(upgrade);
            cardContainer.getChildren().add(card);
        }

        layout.getChildren().addAll(title, subtitle, cardContainer);

        Scene scene = new Scene(layout, 900, 400);
        stage.setScene(scene);
    }

    private VBox createUpgradeCard(UpgradeManager.Upgrade upgrade) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(250, 280);
        card.setStyle(
            "-fx-background-color: #16213e; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #0f3460; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15; " +
            "-fx-padding: 20;"
        );

        // Icon based on upgrade type
        javafx.scene.Node iconNode;
        if (upgrade.type == UpgradeManager.UpgradeType.SPAWN_TRAPS) {
            // Use trap image for spawn traps upgrade
            try {
                Image trapImage = new Image(getClass().getResource("/assets/Trap.png").toExternalForm());
                ImageView trapImageView = new ImageView(trapImage);
                trapImageView.setFitWidth(60);
                trapImageView.setFitHeight(60);
                iconNode = trapImageView;
            } catch (Exception e) {
                // Fallback to text if image fails to load
                Text fallbackIcon = new Text("🪤");
                fallbackIcon.setFont(Font.font(48));
                iconNode = fallbackIcon;
            }
        } else {
            // Use text emoji for other upgrade types
            String icon = getIconForUpgrade(upgrade.type);
            Text iconText = new Text(icon);
            iconText.setFont(Font.font(48));
            iconNode = iconText;
        }

        Text nameText = new Text(upgrade.name);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameText.setFill(Color.CYAN);
        nameText.setWrappingWidth(210);
        nameText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Text descText = new Text(upgrade.description);
        descText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descText.setFill(Color.LIGHTGRAY);
        descText.setWrappingWidth(210);
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button selectBtn = new Button("SELECT");
        selectBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        selectBtn.setPrefWidth(180);
        selectBtn.setStyle(
            "-fx-background-color: #0f3460; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: cyan; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8;"
        );

        selectBtn.setOnMouseEntered(e -> {
            AudioManager.getInstance().playUiSound("NAV1");

            selectBtn.setStyle(
                "-fx-background-color: cyan; " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: cyan; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;"
            );
            card.setStyle(
                "-fx-background-color: #1a1a2e; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: cyan; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 15; " +
                "-fx-padding: 20;"
            );
        });

        selectBtn.setOnMouseExited(e -> {
            selectBtn.setStyle(
                "-fx-background-color: #0f3460; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: cyan; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;"
            );
            card.setStyle(
                "-fx-background-color: #16213e; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: #0f3460; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 15; " +
                "-fx-padding: 20;"
            );
        });

        selectBtn.setOnAction(e -> {
            AudioManager.getInstance().playUiSound("CONFIRM");
            if (onUpgradeSelected != null) {
                onUpgradeSelected.accept(upgrade.type);
            }
            stage.close();
        });

        card.getChildren().addAll(iconNode, nameText, descText, selectBtn);
        return card;
    }

    private String getIconForUpgrade(UpgradeManager.UpgradeType type) {
        switch (type) {
            case PUSH_RANGE:
            case PUSH_RANGE_PLUS:
                return "🎯";
            case PUSH_STRENGTH:
                return "💪";
            case PUSH_AREA:
                return "〰️";
            case SPAWN_TRAPS:
                return "🪤";
            case HEAL:
                return "❤️";
            default:
                return "⭐";
        }
    }

    public void show() {
        stage.showAndWait();
    }
}