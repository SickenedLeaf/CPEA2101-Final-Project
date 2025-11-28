package com.pushknight.controllers;

import com.pushknight.entities.*;
import com.pushknight.systems.Camera;
import com.pushknight.utils.Constants;
import com.pushknight.utils.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main game controller that manages the game loop and coordinates all systems.
 */
public class GameController {
    // Core systems
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private Camera camera;
    private SpawnSystem spawnSystem;
    
    // Game entities
    private Player player;
    private List<Enemy> enemies;
    private List<Obstacle> obstacles;
    private List<Trap> traps;
    private List<Powerup> powerups;
    
    // Input tracking
    private Set<KeyCode> pressedKeys;
    
    // Timing
    private long lastFrameTime;
    private double deltaTime;
    
    // Game state
    private boolean isRunning;
    private boolean isPaused;
    
    /**
     * Creates a new GameController.
     * @param canvas Canvas to render on
     */
    public GameController(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.pressedKeys = new HashSet<>();
        this.isRunning = false;
        this.isPaused = false;
        
        // Initialize systems
        initializeGame();
        setupGameLoop();
        setupInputHandlers();
    }
    
    /**
     * Initializes all game systems and entities.
     */
    private void initializeGame() {
        // Create player at center of world
        player = new Player(
            Constants.WORLD_WIDTH / 2.0,
            Constants.WORLD_HEIGHT / 2.0
        );
        
        // Initialize camera
        camera = new Camera(
            Constants.WINDOW_WIDTH,
            Constants.WINDOW_HEIGHT,
            Constants.WORLD_WIDTH,
            Constants.WORLD_HEIGHT
        );
        camera.centerOn(player.getPosition().x, player.getPosition().y);
        
        // Initialize entity lists
        enemies = new ArrayList<>();
        obstacles = new ArrayList<>();
        traps = new ArrayList<>();
        powerups = new ArrayList<>();
        
        // Create test obstacles (you should load these from a level file)
        createTestObstacles();
        
        // Create spawn points
        List<Vector2D> spawnPoints = createSpawnPoints();
        spawnSystem = new SpawnSystem(spawnPoints);
    }
    
    /**
     * Creates test obstacles for demonstration.
     */
    private void createTestObstacles() {
        // Create border walls
        obstacles.add(new Obstacle(0, 0, Constants.WORLD_WIDTH, 50)); // Top wall
        obstacles.add(new Obstacle(0, Constants.WORLD_HEIGHT - 50, Constants.WORLD_WIDTH, 50)); // Bottom wall
        obstacles.add(new Obstacle(0, 0, 50, Constants.WORLD_HEIGHT)); // Left wall
        obstacles.add(new Obstacle(Constants.WORLD_WIDTH - 50, 0, 50, Constants.WORLD_HEIGHT)); // Right wall
        
        // Create some interior obstacles
        obstacles.add(new Obstacle(400, 400, 200, 100));
        obstacles.add(new Obstacle(800, 300, 150, 150));
        obstacles.add(new Obstacle(600, 700, 100, 200));
        obstacles.add(new Obstacle(1200, 500, 250, 80));
        
        // Create some traps
        traps.add(new Trap(500, 500, 30, 1, true));
        traps.add(new Trap(900, 600, 30, 1, true));
        traps.add(new Trap(700, 800, 30, 1, true));
    }
    
    /**
     * Creates spawn points around the world.
     * @return List of spawn point positions
     */
    private List<Vector2D> createSpawnPoints() {
        List<Vector2D> spawnPoints = new ArrayList<>();
        
        // Create spawn points at edges of world
        spawnPoints.add(new Vector2D(200, 200));
        spawnPoints.add(new Vector2D(Constants.WORLD_WIDTH - 200, 200));
        spawnPoints.add(new Vector2D(200, Constants.WORLD_HEIGHT - 200));
        spawnPoints.add(new Vector2D(Constants.WORLD_WIDTH - 200, Constants.WORLD_HEIGHT - 200));
        spawnPoints.add(new Vector2D(Constants.WORLD_WIDTH / 2, 200));
        spawnPoints.add(new Vector2D(Constants.WORLD_WIDTH / 2, Constants.WORLD_HEIGHT - 200));
        spawnPoints.add(new Vector2D(200, Constants.WORLD_HEIGHT / 2));
        spawnPoints.add(new Vector2D(Constants.WORLD_WIDTH - 200, Constants.WORLD_HEIGHT / 2));
        
        return spawnPoints;
    }
    
    /**
     * Sets up the main game loop using JavaFX AnimationTimer.
     */
    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                if (isPaused) return;
                
                // Calculate delta time
                if (lastFrameTime == 0) {
                    lastFrameTime = currentTime;
                    return;
                }
                
                deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0;
                lastFrameTime = currentTime;
                
                // Cap delta time to prevent large jumps
                deltaTime = Math.min(deltaTime, 0.1);
                
                // Update and render
                update(deltaTime);
                render();
            }
        };
    }
    
    /**
     * Sets up keyboard input handlers.
     */
    private void setupInputHandlers() {
        canvas.getScene().setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            handleKeyPress(event.getCode());
        });
        
        canvas.getScene().setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            handleKeyRelease(event.getCode());
        });
        
        // Make canvas focusable
        canvas.setFocusTraversable(true);
    }
    
    /**
     * Handles key press events.
     * @param keyCode Key that was pressed
     */
    private void handleKeyPress(KeyCode keyCode) {
        switch (keyCode) {
            case W:
            case UP:
                player.setMovingUp(true);
                break;
            case S:
            case DOWN:
                player.setMovingDown(true);
                break;
            case A:
            case LEFT:
                player.setMovingLeft(true);
                break;
            case D:
            case RIGHT:
                player.setMovingRight(true);
                break;
            case SPACE:
                player.executePush(enemies, obstacles, traps);
                break;
            case ESCAPE:
                togglePause();
                break;
        }
    }
    
    /**
     * Handles key release events.
     * @param keyCode Key that was released
     */
    private void handleKeyRelease(KeyCode keyCode) {
        switch (keyCode) {
            case W:
            case UP:
                player.setMovingUp(false);
                break;
            case S:
            case DOWN:
                player.setMovingDown(false);
                break;
            case A:
            case LEFT:
                player.setMovingLeft(false);
                break;
            case D:
            case RIGHT:
                player.setMovingRight(false);
                break;
        }
    }
    
    /**
     * Updates all game systems and entities.
     * @param deltaTime Time elapsed since last update in seconds
     */
    private void update(double deltaTime) {
        // Update player
        player.update(deltaTime, obstacles, enemies, traps, powerups);
        
        // Update camera to follow player
        camera.update(deltaTime, player);
        
        // Update spawn system (spawns new enemies)
        spawnSystem.update(deltaTime, enemies);
        
        // Update all enemies
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, player, obstacles, enemies, traps);
            
            // Remove dead enemies after death animation
            if (!enemy.isAlive() && enemy instanceof BoomerGoblin) {
                BoomerGoblin boomer = (BoomerGoblin) enemy;
                if (boomer.hasExploded()) {
                    enemiesToRemove.add(enemy);
                }
            } else if (!enemy.isAlive()) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);
        
        // Update all traps
        for (Trap trap : traps) {
            trap.update(deltaTime);
        }
        
        // Remove collected powerups
        powerups.removeIf(Powerup::isCollected);
    }
    
    /**
     * Renders all game entities and UI.
     */
    private void render() {
        // Clear screen
        gc.setFill(Color.rgb(20, 25, 30)); // Dark background
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        double camX = camera.getX();
        double camY = camera.getY();
        
        // Render obstacles
        for (Obstacle obstacle : obstacles) {
            if (camera.isRectVisible(obstacle.getX(), obstacle.getY(), 
                                    obstacle.getWidth(), obstacle.getHeight())) {
                obstacle.render(gc, camX, camY);
            }
        }
        
        // Render traps
        for (Trap trap : traps) {
            if (camera.isVisible(trap.getPosition().x, trap.getPosition().y, 50)) {
                trap.render(gc, camX, camY);
            }
        }
        
        // Render powerups
        for (Powerup powerup : powerups) {
            if (camera.isVisible(powerup.getPosition().x, powerup.getPosition().y, 50)) {
                powerup.render(gc, camX, camY);
            }
        }
        
        // Render enemies
        for (Enemy enemy : enemies) {
            if (camera.isVisible(enemy.getPosition().x, enemy.getPosition().y, 100)) {
                enemy.render(gc, camX, camY);
            }
        }
        
        // Render player
        player.render(gc, camX, camY);
        
        // Render UI
        renderUI();
        
        // Render pause overlay if paused
        if (isPaused) {
            renderPauseOverlay();
        }
    }
    
    /**
     * Renders the game UI (health, wave info, etc.)
     */
    private void renderUI() {
        // Render player health
        player.renderHealth(gc, Constants.HEALTH_UI_X, Constants.HEALTH_UI_Y);
        
        // Render wave information
        gc.setFill(Color.WHITE);
        gc.fillText("Wave: " + spawnSystem.getCurrentWave(), 
                   Constants.WINDOW_WIDTH - 150, 30);
        gc.fillText("Enemies: " + spawnSystem.getEnemiesSpawned() + "/" + 
                   spawnSystem.getEnemiesPerWave(), 
                   Constants.WINDOW_WIDTH - 150, 50);
        gc.fillText("Alive: " + countAliveEnemies(), 
                   Constants.WINDOW_WIDTH - 150, 70);
        
        // Render push cooldown indicator
        if (!player.canPush()) {
            double cooldownPercent = player.getPushCooldown() / 1.0;
            gc.setFill(Color.rgb(255, 255, 255, 0.3));
            gc.fillRect(Constants.WINDOW_WIDTH / 2 - 50, Constants.WINDOW_HEIGHT - 30, 
                       100, 10);
            gc.setFill(Color.CYAN);
            gc.fillRect(Constants.WINDOW_WIDTH / 2 - 50, Constants.WINDOW_HEIGHT - 30, 
                       100 * (1 - cooldownPercent), 10);
        }
    }
    
    /**
     * Renders pause overlay.
     */
    private void renderPauseOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        gc.setFill(Color.WHITE);
        gc.fillText("PAUSED", Constants.WINDOW_WIDTH / 2 - 50, Constants.WINDOW_HEIGHT / 2);
        gc.fillText("Press ESC to resume", Constants.WINDOW_WIDTH / 2 - 80, 
                   Constants.WINDOW_HEIGHT / 2 + 30);
    }
    
    /**
     * Counts number of alive enemies.
     * @return Count of alive enemies
     */
    private int countAliveEnemies() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) count++;
        }
        return count;
    }
    
    /**
     * Starts the game loop.
     */
    public void start() {
        if (!isRunning) {
            isRunning = true;
            lastFrameTime = 0;
            gameLoop.start();
        }
    }
    
    /**
     * Stops the game loop.
     */
    public void stop() {
        if (isRunning) {
            isRunning = false;
            gameLoop.stop();
        }
    }
    
    /**
     * Toggles pause state.
     */
    public void togglePause() {
        isPaused = !isPaused;
    }
    
    // Getters
    public Player getPlayer() { return player; }
    public Camera getCamera() { return camera; }
    public boolean isPaused() { return isPaused; }
    public boolean isRunning() { return isRunning; }
}
