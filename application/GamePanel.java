package application;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
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

    // Book and trap assets
    private Image book1Tile = new Image(getClass().getResource("/assets/Book1.png").toExternalForm());
    private Image book2Tile = new Image(getClass().getResource("/assets/Book2.png").toExternalForm());
    private Image trapTile = new Image(getClass().getResource("/assets/Trap.png").toExternalForm());

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
        topBar.setMinHeight(80);  // Ensure minimum height for top bar
        topBar.setPrefHeight(80); // Set preferred height
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
        bottomBar.setMinHeight(60);  // Ensure minimum height for bottom bar
        bottomBar.setPrefHeight(60); // Set preferred height
        bottomBar.setStyle("-fx-background-color: #16213e; -fx-padding: 10;");

        cooldownText = new Text("Push Ready (SPACE)");
        cooldownText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        cooldownText.setFill(Color.CYAN);
        bottomBar.getChildren().add(cooldownText);

        mainLayout.setTop(topBar);
        mainLayout.setBottom(bottomBar);
        // The gridView will be set after initialization in initializeGrid()

        // Set margins to ensure proper spacing
        BorderPane.setMargin(topBar, new Insets(10));
        BorderPane.setMargin(bottomBar, new Insets(10));
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
                } else if (type == 5) {
                    entityNode = createBookNode();
                } else if (type == 6) {
                    entityNode = createTrapNode();
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
        logic.getPlayer().playIdleAnimation();

        // Initial enemy sprites (if any already exist in logic)
        for (Enemy enemy : logic.getEnemies()) {
            if (!enemy.isDead()) {
                tilePanes[enemy.getX()][enemy.getY()].getChildren().add(enemy.getImageView());
                enemy.playIdleAnimation();
            }
        }

        // Add the gridView to the main layout at the center position and set margins
        mainLayout.setCenter(gridView);
        BorderPane.setMargin(gridView, new Insets(10));
        BorderPane.setAlignment(gridView, Pos.CENTER);

        // Ensure the layout properly respects the top and bottom bars by setting preferred sizes
        topBar.setMaxHeight(topBar.getMinHeight());
        bottomBar.setMaxHeight(bottomBar.getMinHeight());
    }

    private ImageView createWallNode() { return new ImageView(wallTile); }
    private ImageView createWallSpikesNode() { return new ImageView(spikeTile); }
    private ImageView createCampfireNode() { return new ImageView(campfireTile); }

    private Node createBookNode() {
        // Randomly choose between Book1 and Book2
        Image bookImage = random.nextBoolean() ? book1Tile : book2Tile;
        ImageView bookView = new ImageView(bookImage);
        bookView.setFitWidth(TILE_SIZE * 0.8);  // Make it slightly smaller than the tile
        bookView.setFitHeight(TILE_SIZE * 0.8);
        bookView.setId("book-icon");
        return bookView;
    }

    private Node createTrapNode() {
        // Create a visual trap using the Trap.png asset
        ImageView trapView = new ImageView(trapTile);
        trapView.setFitWidth(TILE_SIZE * 0.8);  // Make it slightly smaller than the tile
        trapView.setFitHeight(TILE_SIZE * 0.8);
        trapView.setId("trap-icon");
        return trapView;
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

            // Optionally play movement animation based on enemy AI direction (if available)
            // For now, you could pass logic.getDirection() or store direction inside Enemy.
            // enemy.playMoveAnimation(enemy.getDirection());
        }
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
    	Enemy enemy = logic.findEnemyAt(event.oldX, event.oldY);
        if (enemy != null) {
            ImageView enemyView = enemy.getImageView();
            StackPane pane = tilePanes[event.oldX][event.oldY];
            pane.getChildren().remove(enemyView);
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
        // Visual effect when trap is triggered - remove the trap icon
        StackPane tile = tilePanes[event.newX][event.newY];
        tile.getChildren().removeIf(node -> "trap-icon".equals(node.getId()));
        System.out.println("[VISUAL] Trap triggered and removed from (" + event.newX + "," + event.newY + ")");
    }

    // --- Misc ---

    public Pane getGridView() {
        return mainLayout;
    }

    // Method to handle window resize - adjusts scaling if needed
    public void handleResize(double newWidth, double newHeight) {
        // Ensure the layout properly responds to resize by setting preferred sizes
        if (mainLayout != null && gridView != null && topBar != null && bottomBar != null) {
            mainLayout.setPrefSize(newWidth, newHeight);

            // Calculate available space for the grid by accounting for top and bottom bars with their padding/margins
            double totalBarHeight = topBar.getMinHeight() + bottomBar.getMinHeight();
            double totalPadding = 20; // top margin
            double totalMargin = 20; // bottom margin

            // Set constraints for the bars to maintain their minimum sizes
            topBar.setMaxHeight(topBar.getMinHeight());
            bottomBar.setMaxHeight(bottomBar.getMinHeight());

            // Calculate available space for the grid
            double availableHeight = newHeight - totalBarHeight - totalPadding - totalMargin;

            // Set constraints for the grid to ensure it fits properly between the bars
            gridView.setMaxHeight(Math.max(availableHeight, 100)); // Ensure minimum grid height
        }
    }

    private static class VisualEntity {
        Rectangle rectangle;
        Text healthText;

        VisualEntity(Rectangle rect) {
            this.rectangle = rect;
        }
    }
}
