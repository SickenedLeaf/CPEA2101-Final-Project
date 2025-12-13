package application;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GamePanel {
    private static final int TILE_SIZE = 72;

    private GridPane gridView;
    private BorderPane mainLayout;
    private VBox topBar;
    private HBox bottomBar;

    private StackPane[][] tilePanes = new StackPane[GameLogic.GRID_WIDTH][GameLogic.GRID_HEIGHT];
    private Image floorTile1 = new Image(getClass().getResource("/assets/tiles/FloorTile1.png").toExternalForm());
    private Image floorTile2 = new Image(getClass().getResource("/assets/tiles/FloorTile2.png").toExternalForm());
    private Image floorTile3 = new Image(getClass().getResource("/assets/tiles/FloorTile3.png").toExternalForm());

    private Image wallTile = new Image(getClass().getResource("/assets/obstacles/Wall.png").toExternalForm());
    private Image spikeTile = new Image(getClass().getResource("/assets/obstacles/Spikes.png").toExternalForm());
    private Image campfireTile = new Image(getClass().getResource("/assets/obstacles/Campfire.png").toExternalForm());

    private Random random = new Random();

    private GameLogic logic;
    private Map<String, VisualEntity> entities;

    private Text waveText;
    private Text enemiesText;
    private HBox healthBar;
    private Text cooldownText;

    public GamePanel(GameLogic logic) {
        this.logic = logic;
        this.entities = new HashMap<>();

        initializeUI();
        initializeGrid();
        updateUI();
    }

    private void initializeUI() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #1a1a2e;");

        topBar = new VBox(10);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-background-color: #16213e; -fx-padding: 10;");

        HBox topInfo = new HBox(30);
        topInfo.setAlignment(Pos.CENTER);

        VBox waveInfo = new VBox(5);
        waveInfo.setAlignment(Pos.CENTER);
        waveText = new Text("Wave: 1");
        waveText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        waveText.setFill(Color.WHITE);
        enemiesText = new Text("Enemies: 0/5");
        enemiesText.setFont(Font.font("Arial", 14));
        enemiesText.setFill(Color.LIGHTGRAY);
        waveInfo.getChildren().addAll(waveText, enemiesText);

        healthBar = new HBox(5);
        healthBar.setAlignment(Pos.CENTER);
        updateHealthBar();

        topInfo.getChildren().addAll(waveInfo, healthBar);
        topBar.getChildren().add(topInfo);

        bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-background-color: #16213e; -fx-padding: 10;");

        cooldownText = new Text("Push Ready (SPACE)");
        cooldownText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        cooldownText.setFill(Color.CYAN);
        bottomBar.getChildren().add(cooldownText);

        mainLayout.setTop(topBar);
        mainLayout.setBottom(bottomBar);
    }

    private void initializeGrid() {
        gridView = new GridPane();
        gridView.setAlignment(Pos.CENTER);
        gridView.setStyle("-fx-background-color: #0f3460;");

        for (int x = 0; x < GameLogic.GRID_WIDTH; x++) {
            for (int y = 0; y < GameLogic.GRID_HEIGHT; y++) {
                StackPane tilePane = new StackPane();
                tilePane.setPrefSize(TILE_SIZE, TILE_SIZE);
                tilePanes[x][y] = tilePane;

                // Floor Layer
                int floorType = random.nextInt(100);
                Image floorImage = (floorType >= 90) ? floorTile3 : (floorType >= 60 ? floorTile2 : floorTile1);
                ImageView tileView = new ImageView(floorImage);
                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                tilePane.getChildren().add(tileView);

                // Static Entities
                Node entityNode = null;
                int type = logic.getEntityAt(x, y);

                if (type == 1) {
                    entityNode = createWallNode();
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

        // Place player
        tilePanes[logic.getPlayer().getX()][logic.getPlayer().getY()]
            .getChildren().add(logic.getPlayer().getImageView());
        mainLayout.setCenter(gridView);
        logic.getPlayer().playIdleAnimation();

        // Initial enemy sprites (if any already exist in logic)
        for (Enemy enemy : logic.getEnemies()) {
            if (!enemy.isDead()) {
                tilePanes[enemy.getX()][enemy.getY()].getChildren().add(enemy.getImageView());
                enemy.playIdleAnimation();
            }
        }
    }

    private ImageView createWallNode() { return new ImageView(wallTile); }
    private ImageView createWallSpikesNode() { return new ImageView(spikeTile); }
    private ImageView createCampfireNode() { return new ImageView(campfireTile); }
    
    private Node createBookNode() {
        // Create a visual book icon
        Text bookIcon = new Text("📖");
        bookIcon.setFont(Font.font(48));
        bookIcon.setId("book-icon");
        return bookIcon;
    }
    
    private Node createTrapNode() {
        // Create a visual bear trap
        Circle trap = new Circle(20);
        trap.setFill(Color.rgb(80, 80, 80));
        trap.setStroke(Color.DARKGRAY);
        trap.setStrokeWidth(3);
        trap.setId("trap-icon");
        return trap;
    }

    private void updateHealthBar() {
        healthBar.getChildren().clear();

        Text label = new Text("Health: ");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setFill(Color.WHITE);
        healthBar.getChildren().add(label);

        int health = logic.getPlayer().getHealth();
        int maxHealth = logic.getPlayer().getMaxHealth();

        for (int i = 0; i < maxHealth; i++) {
            Rectangle heart = new Rectangle(25, 25);
            heart.setArcWidth(5);
            heart.setArcHeight(5);

            if (i < health) {
                heart.setFill(Color.RED);
                heart.setStroke(Color.DARKRED);
            } else {
                heart.setFill(Color.rgb(50, 50, 50));
                heart.setStroke(Color.rgb(30, 30, 30));
            }
            heart.setStrokeWidth(2);

            healthBar.getChildren().add(heart);
        }

        if (logic.getPlayer().isInvulnerable()) {
            Text iframesText = new Text(" [INVULNERABLE]");
            iframesText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            iframesText.setFill(Color.YELLOW);
            healthBar.getChildren().add(iframesText);
        }
    }

    private void updateUI() {
        SpawnSystem spawn = logic.getSpawnSystem();
        waveText.setText("Wave: " + spawn.getCurrentWave());

        int aliveEnemies = 0;
        for (Enemy enemy : logic.getEnemies()) {
            if (!enemy.isDead()) aliveEnemies++;
        }

        enemiesText.setText("Enemies: " + spawn.getEnemiesSpawned() + "/" +
                           spawn.getEnemiesPerWave() + " (Alive: " + aliveEnemies + ")");

        updateHealthBar();

        Player player = logic.getPlayer();
        if (player.canPush()) {
            cooldownText.setText("Push Ready (SPACE)");
            cooldownText.setFill(Color.CYAN);
        } else {
            double cooldown = player.getPushCooldown();
            cooldownText.setText(String.format("Push Cooldown: %.1fs", cooldown));
            cooldownText.setFill(Color.GRAY);
        }
    }

    public void handleEvent(GameUpdateEvent event) {
        if (event == null) return;

        switch (event.type) {
            case PLAYER_MOVE:
                handlePlayerMove(event);
                break;
            case ENEMY_MOVE:
                handleEnemyMove(event);
                break;
            case ENEMY_SPAWN:
                handleEnemySpawn(event);
                break;
            case DAMAGE:
                handleDamage(event);
                break;
            case REMOVE_ENTITY:
                handleRemoveEntity(event);
                break;
            case IMPACT:
                handleImpact(event);
                break;
            case PLAYER_DAMAGE:
                handlePlayerDamage(event);
                break;
            case BOOK_SPAWN:
                handleBookSpawn(event);
                break;
            case BOOK_COLLECTED:
                handleBookCollected(event);
                break;
            case TRAP_SPAWN:
                handleTrapSpawn(event);
                break;
            case TRAP_TRIGGER:
                handleTrapTrigger(event);
                break;
        }

        updateUI();
    }

    // --- Player ---

    private void handlePlayerMove(GameUpdateEvent event) {
        StackPane oldTile = tilePanes[event.oldX][event.oldY];
        oldTile.getChildren().remove(logic.getPlayer().getImageView());

        StackPane newTile = tilePanes[event.newX][event.newY];
        newTile.getChildren().add(logic.getPlayer().getImageView());

        // Optional flicker overlay
        newTile.getChildren().removeIf(node -> node instanceof Rectangle && "flicker".equals(node.getId()));
        if (logic.getPlayer().shouldFlicker()) {
            Rectangle flickerOverlay = new Rectangle(TILE_SIZE, TILE_SIZE);
            flickerOverlay.setId("flicker");
            flickerOverlay.setFill(Color.TRANSPARENT);
            flickerOverlay.setOpacity(0.5);
            newTile.getChildren().add(flickerOverlay);
        }
    }

    // --- Enemies ---

    private void handleEnemySpawn(GameUpdateEvent event) {
        Enemy enemy = logic.findEnemyAt(event.newX, event.newY);
        if (enemy != null && !enemy.isDead()) {
            StackPane tile = tilePanes[event.newX][event.newY];
            if (!tile.getChildren().contains(enemy.getImageView())) {
                tile.getChildren().add(enemy.getImageView());
            }
            enemy.playIdleAnimation();
        }
    }

    private void handleEnemyMove(GameUpdateEvent event) {
        Enemy enemy = logic.findEnemyAt(event.newX, event.newY);
        if (enemy != null && !enemy.isDead()) {
            ImageView enemyView = enemy.getImageView();

            // Remove from old tile if present
            StackPane oldPane = tilePanes[event.oldX][event.oldY];
            oldPane.getChildren().remove(enemyView);

            // Add to new tile
            StackPane newPane = tilePanes[event.newX][event.newY];
            if (!newPane.getChildren().contains(enemyView)) {
                newPane.getChildren().add(enemyView);
            }
        }
    }

    // --- Book ---
    
    private void handleBookSpawn(GameUpdateEvent event) {
        StackPane tile = tilePanes[event.newX][event.newY];
        Node bookNode = createBookNode();
        tile.getChildren().add(bookNode);
        System.out.println("[VISUAL] Book rendered at (" + event.newX + "," + event.newY + ")");
    }
    
    private void handleBookCollected(GameUpdateEvent event) {
        StackPane tile = tilePanes[event.newX][event.newY];
        tile.getChildren().removeIf(node -> "book-icon".equals(node.getId()));
        System.out.println("[VISUAL] Book removed from (" + event.newX + "," + event.newY + ")");
    }
    
    // --- Traps ---
    
    private void handleTrapSpawn(GameUpdateEvent event) {
        StackPane tile = tilePanes[event.newX][event.newY];
        Node trapNode = createTrapNode();
        tile.getChildren().add(trapNode);
        System.out.println("[VISUAL] Trap rendered at (" + event.newX + "," + event.newY + ")");
    }
    
    private void handleTrapTrigger(GameUpdateEvent event) {
        StackPane tile = tilePanes[event.newX][event.newY];
        tile.getChildren().removeIf(node -> "trap-icon".equals(node.getId()));
        
        // Flash effect
        Rectangle flash = new Rectangle(TILE_SIZE, TILE_SIZE);
        flash.setFill(Color.ORANGE);
        flash.setOpacity(0.7);
        flash.setId("trapTriggerFlash");
        tile.getChildren().add(flash);
        
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override public void run() {
                javafx.application.Platform.runLater(() ->
                    tile.getChildren().removeIf(node -> node instanceof Rectangle && "trapTriggerFlash".equals(node.getId()))
                );
            }
        }, 150);
        
        System.out.println("[VISUAL] Trap triggered at (" + event.newX + "," + event.newY + ")");
    }

    // --- Effects & Damage ---

    private void handleDamage(GameUpdateEvent event) {
        // Visual flash on the impacted tile (enemy or player tile)
        StackPane pane = tilePanes[event.newX][event.newY];
        Rectangle flash = new Rectangle(TILE_SIZE, TILE_SIZE);
        flash.setFill(Color.WHITE);
        flash.setOpacity(0.7);
        flash.setId("damageFlash");

        pane.getChildren().add(flash);

        // Remove flash after short time
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override public void run() {
                javafx.application.Platform.runLater(() ->
                    pane.getChildren().removeIf(node -> node instanceof Rectangle && "damageFlash".equals(node.getId()))
                );
            }
        }, 100);
    }

    private void handleImpact(GameUpdateEvent event) {
        // Yellow impact flash on tile
        StackPane pane = tilePanes[event.newX][event.newY];
        Rectangle flash = new Rectangle(TILE_SIZE, TILE_SIZE);
        flash.setFill(Color.YELLOW);
        flash.setOpacity(0.6);
        flash.setId("impactFlash");

        pane.getChildren().add(flash);

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override public void run() {
                javafx.application.Platform.runLater(() ->
                    pane.getChildren().removeIf(node -> node instanceof Rectangle && "impactFlash".equals(node.getId()))
                );
            }
        }, 150);
    }

    private void handleRemoveEntity(GameUpdateEvent event) {
        // Remove enemy sprite from tile when entity is removed
        Enemy enemy = logic.findEnemyAt(event.newX, event.newY);
        StackPane pane = tilePanes[event.newX][event.newY];

        if (enemy != null) {
            pane.getChildren().remove(enemy.getImageView());
        } else {
            // If no enemy, you could remove any temp overlay
            pane.getChildren().removeIf(node -> node instanceof Rectangle);
        }
    }

    private void handlePlayerDamage(GameUpdateEvent event) {
        updateHealthBar();

        // Player damage visual on current tile
        StackPane pane = tilePanes[event.newX][event.newY];

        // Flicker transparent overlay if invulnerable
        pane.getChildren().removeIf(node -> node instanceof Rectangle && "playerDamageFlash".equals(node.getId()));
        Rectangle flash = new Rectangle(TILE_SIZE, TILE_SIZE);
        flash.setId("playerDamageFlash");

        if (logic.getPlayer().isInvulnerable() && logic.getPlayer().shouldFlicker()) {
            flash.setFill(Color.TRANSPARENT);
            flash.setOpacity(0.5);
        } else if (!logic.getPlayer().isInvulnerable()) {
            flash.setFill(Color.WHITE);
            flash.setOpacity(0.7);
            // Remove after short delay
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override public void run() {
                    javafx.application.Platform.runLater(() ->
                        pane.getChildren().removeIf(node -> node instanceof Rectangle && "playerDamageFlash".equals(node.getId()))
                    );
                }
            }, 200);
        } else {
            flash.setFill(Color.TRANSPARENT);
            flash.setOpacity(0.0);
        }

        pane.getChildren().add(flash);
    }

    // --- Misc ---

    public Pane getGridView() {
        return mainLayout;
    }

    private static class VisualEntity {
        Rectangle rectangle;
        Text healthText;

        VisualEntity(Rectangle rect) {
            this.rectangle = rect;
        }
    }
}