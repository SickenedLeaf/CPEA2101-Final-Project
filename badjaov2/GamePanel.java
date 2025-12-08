package application;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import application.GameLogic.GameUpdateEvent;

/**
 * GamePanel with health bars above characters and enemy counter.
 */
public class GamePanel {

    private static final int TILE_SIZE = 48;
    private static final int HEALTH_BAR_WIDTH = 40;
    private static final int HEALTH_BAR_HEIGHT = 6;
    private static final int HEALTH_BAR_OFFSET = 8;
    
    private GameLogic logic;
    private GridPane gridView = new GridPane();
    private StackPane[][] tilePanes = new StackPane[GameLogic.GRID_WIDTH][GameLogic.GRID_HEIGHT];
    
    private Rectangle playerNode;
    private StackPane playerHealthBar;
    private Map<String, StackPane> enemyHealthBars = new HashMap<>();
    
    // UI elements
    private VBox uiContainer;
    private Label waveLabel;
    private Label enemyCountLabel;
    private Label levelLabel;

    public GamePanel(GameLogic logic) {
        this.logic = logic;
        initializeGameView();
        initializeUI();
    }

    /**
     * Initializes the game grid view.
     */
    private void initializeGameView() {
        gridView.setAlignment(Pos.CENTER);
        gridView.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");

        for (int x = 0; x < GameLogic.GRID_WIDTH; x++) {
            for (int y = 0; y < GameLogic.GRID_HEIGHT; y++) {
                StackPane tilePane = new StackPane();
                tilePane.setPrefSize(TILE_SIZE, TILE_SIZE);
                tilePanes[x][y] = tilePane;
                
                // Floor
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE, Color.web("#7f8c8d"));
                tile.setStroke(Color.web("#34495e"));
                tilePane.getChildren().add(tile);
                
                // Entity
                int type = logic.getEntityAt(x, y);
                Node entityNode = null;

                if (type == 1) {
                    entityNode = createWallNode();
                } else if (type == 2) {
                    entityNode = createEnemyNode(x, y, logic.getEnemyHealthAt(x, y));
                } else if (type == 3) {
                    entityNode = createWallSpikesNode();
                } else if (type == 4) {
                    entityNode = createCampfireNode();
                }

                if (entityNode != null) {
                    tilePane.getChildren().add(entityNode);
                }

                GridPane.setConstraints(tilePane, x, y);
                gridView.getChildren().add(tilePane);
            }
        }

        // Player node with health bar
        playerNode = new Rectangle(TILE_SIZE - 10, TILE_SIZE - 10, Color.web("#27ae60"));
        playerNode.setArcWidth(15);
        playerNode.setArcHeight(15);
        
        playerHealthBar = createHealthBar(logic.getPlayer().getHealth(), logic.getPlayer().getMaxHealth());
        
        StackPane playerStack = new StackPane();
        playerStack.setAlignment(Pos.TOP_CENTER);
        playerStack.getChildren().addAll(playerNode, playerHealthBar);
        StackPane.setAlignment(playerHealthBar, Pos.TOP_CENTER);
        
        tilePanes[logic.getPlayerX()][logic.getPlayerY()].getChildren().add(playerStack);
    }
    
    /**
     * Creates a health bar display.
     */
    private StackPane createHealthBar(int currentHealth, int maxHealth) {
        StackPane container = new StackPane();
        container.setMaxSize(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        container.setTranslateY(-TILE_SIZE / 2 - HEALTH_BAR_OFFSET);
        
        // Background
        Rectangle bg = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Color.DARKGRAY);
        
        // Health fill
        double healthPercent = (double) currentHealth / maxHealth;
        Rectangle fill = new Rectangle(HEALTH_BAR_WIDTH * healthPercent, HEALTH_BAR_HEIGHT, getHealthColor(healthPercent));
        fill.setId("healthFill");
        
        // Border
        Rectangle border = new Rectangle(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(1);
        
        container.getChildren().addAll(bg, fill, border);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        
        return container;
    }
    
    /**
     * Updates an existing health bar.
     */
    private void updateHealthBar(StackPane healthBar, int currentHealth, int maxHealth) {
        if (healthBar == null) return;
        
        double healthPercent = (double) currentHealth / maxHealth;
        
        for (Node node : healthBar.getChildren()) {
            if (node instanceof Rectangle && node.getId() != null && node.getId().equals("healthFill")) {
                Rectangle fill = (Rectangle) node;
                fill.setWidth(HEALTH_BAR_WIDTH * healthPercent);
                fill.setFill(getHealthColor(healthPercent));
            }
        }
    }
    
    /**
     * Gets color based on health percentage.
     */
    private Color getHealthColor(double healthPercent) {
        if (healthPercent > 0.6) return Color.web("#2ecc71"); // Green
        if (healthPercent > 0.3) return Color.web("#f39c12"); // Orange
        return Color.web("#e74c3c"); // Red
    }
    
    /**
     * Initializes UI overlay.
     */
    private void initializeUI() {
        uiContainer = new VBox(10);
        uiContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                           "-fx-padding: 15; -fx-background-radius: 10;");
        uiContainer.setAlignment(Pos.TOP_LEFT);
        
        // Level display
        levelLabel = new Label();
        levelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        levelLabel.setTextFill(Color.web("#ffd700"));
        updateLevelDisplay();
        
        // Wave display
        waveLabel = new Label();
        waveLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        waveLabel.setTextFill(Color.web("#3498db"));
        updateWaveDisplay();
        
        // Enemy count
        enemyCountLabel = new Label();
        enemyCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        enemyCountLabel.setTextFill(Color.web("#e74c3c"));
        updateEnemyCount();
        
        uiContainer.getChildren().addAll(levelLabel, waveLabel, enemyCountLabel);
    }
    
    /**
     * Updates level display.
     */
    private void updateLevelDisplay() {
        SpawnSystem spawn = logic.getSpawnSystem();
        levelLabel.setText("▼ LEVEL " + spawn.getLevelNumber() + " ▼");
    }
    
    /**
     * Updates wave display.
     */
    private void updateWaveDisplay() {
        SpawnSystem spawn = logic.getSpawnSystem();
        waveLabel.setText("Wave " + spawn.getCurrentWave() + "/" + spawn.getTotalWaves());
    }
    
    /**
     * Updates enemy count display.
     */
    private void updateEnemyCount() {
        SpawnSystem spawn = logic.getSpawnSystem();
        int remaining = spawn.getEnemiesRemaining();
        enemyCountLabel.setText("Enemies Left: " + remaining);
    }
    
    private Rectangle createWallNode() {
        Rectangle wall = new Rectangle(TILE_SIZE, TILE_SIZE, Color.web("#34495e"));
        wall.setArcWidth(8);
        wall.setArcHeight(8);
        return wall;
    }

    private StackPane createWallSpikesNode() {
        Rectangle base = new Rectangle(TILE_SIZE, TILE_SIZE, Color.web("#34495e"));
        base.setArcWidth(8);
        base.setArcHeight(8);
        
        Polygon spike = new Polygon(
            TILE_SIZE / 2.0 - 8, TILE_SIZE / 2.0 + 8,
            TILE_SIZE / 2.0 + 8, TILE_SIZE / 2.0 + 8,
            TILE_SIZE / 2.0, TILE_SIZE / 2.0 - 8
        );
        spike.setFill(Color.web("#c0392b"));
        
        return new StackPane(base, spike);
    }

    private StackPane createCampfireNode() {
        Rectangle fireBase = new Rectangle(TILE_SIZE - 12, TILE_SIZE - 12, Color.web("#d35400"));
        fireBase.setArcWidth(20);
        fireBase.setArcHeight(20);
        
        Circle flame = new Circle(TILE_SIZE / 6, Color.web("#f1c40f"));
        flame.setTranslateY(-(TILE_SIZE / 6.0));
        
        return new StackPane(fireBase, flame);
    }
    
    private StackPane createEnemyNode(int x, int y, int health) {
        Rectangle enemy = new Rectangle(TILE_SIZE - 10, TILE_SIZE - 10, Color.web("#e74c3c"));
        enemy.setArcWidth(15);
        enemy.setArcHeight(15);
        
        // Get enemy to determine max health
        Enemy enemyObj = logic.findEnemyAt(x, y);
        int maxHp = (enemyObj != null) ? enemyObj.getMaxHp() : 3;
        
        StackPane healthBar = createHealthBar(health, maxHp);
        
        String key = x + "," + y;
        enemyHealthBars.put(key, healthBar);
        
        StackPane enemyStack = new StackPane();
        enemyStack.setAlignment(Pos.TOP_CENTER);
        enemyStack.getChildren().addAll(enemy, healthBar);
        enemyStack.setId("enemy_" + key);
        StackPane.setAlignment(healthBar, Pos.TOP_CENTER);
        
        return enemyStack;
    }
    
    /**
     * Handles game update events.
     */
    public void handleEvent(GameUpdateEvent event) {
        switch (event.type) {
            case PLAYER_MOVE:
                movePlayerWithHealthBar(event.oldX, event.oldY, event.newX, event.newY);
                
                if (logic.getPlayer().shouldFlicker()) {
                    playerNode.setVisible(false);
                } else {
                    playerNode.setVisible(true);
                }
                break;
                
            case PLAYER_DAMAGE:
                updateHealthBar(playerHealthBar, event.value, logic.getPlayer().getMaxHealth());
                showDamageEffect(event.newX, event.newY, Color.web("#e74c3c"));
                break;
                
            case ENEMY_MOVE:
                handleEnemyMoveWithHealthBar(event);
                updateEnemyCount();
                break;
                
            case ENEMY_SPAWN:
                StackPane spawnPane = tilePanes[event.newX][event.newY];
                Node enemyNode = createEnemyNode(event.newX, event.newY, event.value);
                spawnPane.getChildren().add(enemyNode);
                updateEnemyCount();
                updateWaveDisplay();
                break;
                
            case DAMAGE:
                String key = event.newX + "," + event.newY;
                StackPane healthBar = enemyHealthBars.get(key);
                Enemy enemy = logic.findEnemyAt(event.newX, event.newY);
                if (enemy != null) {
                    updateHealthBar(healthBar, event.value, enemy.getMaxHp());
                }
                showDamageEffect(event.newX, event.newY, Color.web("#f39c12"));
                break;
                
            case REMOVE_ENTITY:
                removeEntityNode(event.newX, event.newY);
                updateEnemyCount();
                updateWaveDisplay();
                break;
                
            case IMPACT:
                showAttackEffect(event.newX, event.newY);
                break;
        }
    }
    
    /**
     * Moves player with health bar attached.
     */
    private void movePlayerWithHealthBar(int oldX, int oldY, int newX, int newY) {
        StackPane oldPane = tilePanes[oldX][oldY];
        StackPane newPane = tilePanes[newX][newY];
        
        // Find player stack
        Node playerStack = oldPane.getChildren().stream()
            .filter(node -> node instanceof StackPane)
            .filter(node -> ((StackPane)node).getChildren().contains(playerNode))
            .findFirst().orElse(null);
        
        if (playerStack != null) {
            oldPane.getChildren().remove(playerStack);
            newPane.getChildren().add(playerStack);
        }
    }
    
    /**
     * Shows a damage number effect.
     */
    private void showDamageEffect(int x, int y, Color color) {
        StackPane targetPane = tilePanes[x][y];
        
        Label damageLabel = new Label("-1");
        damageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        damageLabel.setTextFill(color);
        damageLabel.setTranslateY(-15);
        
        targetPane.getChildren().add(damageLabel);
        
        FadeTransition ft = new FadeTransition(Duration.millis(800), damageLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> targetPane.getChildren().remove(damageLabel));
        ft.play();
    }
    
    private void handleEnemyMoveWithHealthBar(GameUpdateEvent event) {
        StackPane oldPane = tilePanes[event.oldX][event.oldY];
        StackPane newPane = tilePanes[event.newX][event.newY];
        
        Node enemyNode = oldPane.getChildren().stream()
            .filter(node -> node.getId() != null && node.getId().startsWith("enemy_"))
            .findFirst().orElse(null);

        if (enemyNode != null) {
            oldPane.getChildren().remove(enemyNode);
            newPane.getChildren().add(enemyNode);
            
            String oldKey = event.oldX + "," + event.oldY;
            String newKey = event.newX + "," + event.newY;
            
            StackPane healthBar = enemyHealthBars.remove(oldKey);
            if (healthBar != null) {
                enemyHealthBars.put(newKey, healthBar);
                enemyNode.setId("enemy_" + newKey);
            }
        }
    }

    private void removeEntityNode(int x, int y) {
        StackPane targetPane = tilePanes[x][y];
        Node enemyNode = targetPane.getChildren().stream()
                .filter(node -> node.getId() != null && node.getId().startsWith("enemy_"))
                .findFirst().orElse(null);

        if (enemyNode != null) {
            targetPane.getChildren().remove(enemyNode);
            String key = x + "," + y;
            enemyHealthBars.remove(key);
        }
    }

    private void showAttackEffect(int x, int y) {
        StackPane targetPane = tilePanes[x][y];

        Rectangle flash = new Rectangle(TILE_SIZE, TILE_SIZE, Color.web("#f1c40f"));
        flash.setOpacity(0.9);

        targetPane.getChildren().add(flash);

        FadeTransition ft = new FadeTransition(Duration.millis(300), flash);
        ft.setFromValue(0.9);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> targetPane.getChildren().remove(flash));
        ft.play();
    }

    public VBox getGridView() {
        VBox container = new VBox(10);
        container.getChildren().addAll(uiContainer, gridView);
        container.setAlignment(Pos.CENTER);
        return container;
    }
}