package com.pushknight.controllers;

import com.pushknight.entities.*;
import com.pushknight.systems.CollisionSystem;
import com.pushknight.systems.GameDirector;
import com.pushknight.utils.Constants;
import com.pushknight.utils.GameState;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game controller that manages game state, entities, and user input.
 */
public class GameController {
    private GameState currentState;
    private Player player;
    private List<Entity> entities;
    private List<Trap> traps;
    private CollisionSystem collisionSystem;
    private GameDirector gameDirector;
    private GraphicsContext graphicsContext;
    private Scene scene;
    private AnimationTimer gameLoop;
    private long lastUpdateTime;
    private boolean[] keysPressed; // Array to track key states for continuous input

    /**
     * Creates a new GameController.
     */
    public GameController() {
        this.currentState = GameState.MENU; // Default to menu state
        this.entities = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.collisionSystem = new CollisionSystem();
        this.gameDirector = GameDirector.getInstance();
        this.keysPressed = new boolean[256]; // Support for standard keyboard keys
        
        initializeGame();
    }

    /**
     * Initializes the game components.
     */
    private void initializeGame() {
        // Create player
        this.player = new Player();
        
        // Add player to entities list
        this.entities.add(player);
        
        // Initialize GameDirector with player reference
        gameDirector.initialize(player);
    }

    /**
     * Sets the graphics context for rendering.
     *
     * @param gc The GraphicsContext to use for rendering
     */
    public void setGraphicsContext(GraphicsContext gc) {
        this.graphicsContext = gc;
    }

    /**
     * Sets the scene for input handling.
     *
     * @param scene The Scene to attach input handlers to
     */
    public void setScene(Scene scene) {
        this.scene = scene;
        setupInputHandlers();
    }

    /**
     * Sets up input handlers for the scene.
     */
    private void setupInputHandlers() {
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPress);
            scene.setOnKeyReleased(this::handleKeyRelease);
        }
    }

    /**
     * Handles key press events.
     *
     * @param event The key event
     */
    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        
        // Map key codes to array indices
        int keyIndex = mapKeyCodeToIndex(code);
        if (keyIndex != -1) {
            keysPressed[keyIndex] = true;
        }
        
        // Handle specific key presses
        switch (currentState) {
            case PLAYING:
                handlePlayingStateKeyPress(code);
                break;
            case PAUSED:
                handlePausedStateKeyPress(code);
                break;
            case GAME_OVER:
                handleGameOverStateKeyPress(code);
                break;
            case UPGRADE_SELECTION:
                handleUpgradeSelectionKeyPress(code);
                break;
        }
    }

    /**
     * Handles key release events.
     *
     * @param event The key event
     */
    private void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        
        // Map key codes to array indices
        int keyIndex = mapKeyCodeToIndex(code);
        if (keyIndex != -1) {
            keysPressed[keyIndex] = false;
        }
    }

    /**
     * Maps a KeyCode to an array index.
     *
     * @param code The KeyCode to map
     * @return The array index, or -1 if not supported
     */
    private int mapKeyCodeToIndex(KeyCode code) {
        // This is a simplified mapping - in a real game you'd support more keys
        switch (code) {
            case W: return 0;
            case A: return 1;
            case S: return 2;
            case D: return 3;
            case UP: return 4;
            case LEFT: return 5;
            case DOWN: return 6;
            case RIGHT: return 7;
            case SPACE: return 8;
            case ESCAPE: return 9;
            default: return -1;
        }
    }

    /**
     * Handles key presses in the playing state.
     *
     * @param code The key code
     */
    private void handlePlayingStateKeyPress(KeyCode code) {
        switch (code) {
            case SPACE:
                // Use push ability
                player.usePush(System.nanoTime());
                break;
            case ESCAPE:
                // Pause the game
                setCurrentState(GameState.PAUSED);
                break;
        }
    }

    /**
     * Handles key presses in the paused state.
     *
     * @param code The key code
     */
    private void handlePausedStateKeyPress(KeyCode code) {
        if (code == KeyCode.ESCAPE || code == KeyCode.P) {
            // Resume the game
            setCurrentState(GameState.PLAYING);
        }
    }

    /**
     * Handles key presses in the game over state.
     *
     * @param code The key code
     */
    private void handleGameOverStateKeyPress(KeyCode code) {
        if (code == KeyCode.R || code == KeyCode.ENTER) {
            // Restart the game
            restartGame();
        }
    }

    /**
     * Handles key presses in the upgrade selection state.
     *
     * @param code The key code
     */
    private void handleUpgradeSelectionKeyPress(KeyCode code) {
        // Upgrade selection would be handled by UI elements
        // For now, we'll just go back to playing state
        if (code == KeyCode.ENTER) {
            setCurrentState(GameState.PLAYING);
        }
    }

    /**
     * Processes continuous input for player movement.
     */
    private void processInput() {
        if (currentState != GameState.PLAYING) {
            return;
        }

        // Check for movement keys and move player accordingly
        if (keysPressed[0] || keysPressed[4]) { // W or Up Arrow
            player.move(com.pushknight.utils.Direction.UP, System.nanoTime());
        }
        if (keysPressed[1] || keysPressed[5]) { // A or Left Arrow
            player.move(com.pushknight.utils.Direction.LEFT, System.nanoTime());
        }
        if (keysPressed[2] || keysPressed[6]) { // S or Down Arrow
            player.move(com.pushknight.utils.Direction.DOWN, System.nanoTime());
        }
        if (keysPressed[3] || keysPressed[7]) { // D or Right Arrow
            player.move(com.pushknight.utils.Direction.RIGHT, System.nanoTime());
        }
    }

    /**
     * Starts the game loop using AnimationTimer.
     */
    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate delta time
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; // Convert to seconds
                lastUpdateTime = now;

                // Update game logic
                update(deltaTime, now);

                // Render game
                render();
            }
        };
        lastUpdateTime = System.nanoTime();
        gameLoop.start();
    }

    /**
     * Updates game logic.
     *
     * @param deltaTime Time elapsed since last frame in seconds
     * @param now Current timestamp in nanoseconds
     */
    private void update(double deltaTime, long now) {
        if (currentState != GameState.PLAYING) {
            // For now, we only update gameplay when in PLAYING state
            // In a full implementation, other states might have updates too
            return;
        }

        // Process input
        processInput();

        // Update player
        player.update(now);

        // Update traps
        for (Trap trap : traps) {
            trap.update(now);
        }

        // Update GameDirector (handles enemy spawning)
        gameDirector.update(now);

        // Update other entities (enemies)
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            
            // Skip player as we updated it separately
            if (entity == player) {
                continue;
            }
            
            // Update entity
            entity.update(now);
            
            // Check if enemy is a BoomerGoblin that just died and is exploding
            if (entity instanceof BoomerGoblin) {
                BoomerGoblin boomer = (BoomerGoblin) entity;
                if (boomer.isExplosionComplete(now)) {
                    // Remove exploded boomer goblin
                    entities.remove(i);
                    gameDirector.onEnemyDefeated(boomer);
                }
                // Apply explosion damage to nearby entities
                else if (boomer.isExploding()) {
                    applyExplosionDamage(boomer);
                }
            }
            // For regular enemies that have died
            else if (entity instanceof Enemy && entity instanceof Damageable && !((Damageable) entity).isAlive()) {
                Enemy enemy = (Enemy) entity;
                enemy.onDeath();
                entities.remove(i);
                gameDirector.onEnemyDefeated(enemy);
            }
        }

        // Check collisions
        collisionSystem.checkCollisions(entities);

        // Check if player died
        if (!player.isAlive()) {
            setCurrentState(GameState.GAME_OVER);
        }

        // Check if wave is complete and ready for upgrade selection
        if (gameDirector.isWaveComplete() && gameDirector.isReadyForNextWave(now)) {
            setCurrentState(GameState.UPGRADE_SELECTION);
        }
    }

    /**
     * Applies explosion damage from a BoomerGoblin to nearby entities.
     *
     * @param boomer The BoomerGoblin that is exploding
     */
    private void applyExplosionDamage(BoomerGoblin boomer) {
        for (Entity entity : entities) {
            if (entity != boomer && boomer.isEntityInExplosionRadius(entity) && entity instanceof Damageable) {
                Damageable damageable = (Damageable) entity;
                damageable.takeDamage(boomer.getDamage());
                
                // If the entity that was damaged was an enemy and it died
                if (entity instanceof Enemy && entity instanceof Damageable && !((Damageable) entity).isAlive()) {
                    Enemy enemy = (Enemy) entity;
                    enemy.onDeath();
                    entities.remove(entity);
                    gameDirector.onEnemyDefeated(enemy);
                }
            }
        }
        
        // Check if player was in range of explosion
        if (boomer.isEntityInExplosionRadius(player)) {
            player.takeDamage(boomer.getDamage());
        }
    }

    /**
     * Renders the game to the canvas.
     */
    public void render() {
        if (graphicsContext == null) {
            return; // Can't render without graphics context
        }

        // Clear canvas
        graphicsContext.clearRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Set background color
        graphicsContext.setFill(javafx.scene.paint.Color.DARKGRAY);
        graphicsContext.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Render all entities
        for (Entity entity : entities) {
            entity.render(graphicsContext);
        }

        // Render all traps
        for (Trap trap : traps) {
            trap.render(graphicsContext);
        }

        // Render UI elements based on game state
        renderUI();
    }

    /**
     * Renders UI elements based on the current game state.
     */
    private void renderUI() {
        if (graphicsContext == null) {
            return;
        }

        // Set text color and font
        graphicsContext.setFill(javafx.scene.paint.Color.WHITE);
        graphicsContext.setFont(javafx.scene.text.Font.font(16));

        switch (currentState) {
            case PLAYING:
                // Display player health
                graphicsContext.fillText("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 10, 20);
                
                // Display score
                graphicsContext.fillText("Score: " + player.getScore(), 10, 40);
                
                // Display current wave
                graphicsContext.fillText("Wave: " + gameDirector.getCurrentWave(), 10, 60);
                
                // Display enemies remaining
                graphicsContext.fillText("Enemies: " + gameDirector.getEnemiesAlive(), 10, 80);
                break;
                
            case PAUSED:
                // Display pause message
                graphicsContext.fillText("PAUSED", Constants.WINDOW_WIDTH / 2 - 30, Constants.WINDOW_HEIGHT / 2);
                graphicsContext.fillText("Press ESC to resume", Constants.WINDOW_WIDTH / 2 - 70, Constants.WINDOW_HEIGHT / 2 + 20);
                break;
                
            case GAME_OVER:
                // Display game over message
                graphicsContext.fillText("GAME OVER", Constants.WINDOW_WIDTH / 2 - 50, Constants.WINDOW_HEIGHT / 2 - 20);
                graphicsContext.fillText("Final Score: " + player.getScore(), Constants.WINDOW_WIDTH / 2 - 60, Constants.WINDOW_HEIGHT / 2);
                graphicsContext.fillText("Wave Reached: " + gameDirector.getCurrentWave(), Constants.WINDOW_WIDTH / 2 - 70, Constants.WINDOW_HEIGHT / 2 + 20);
                graphicsContext.fillText("Press R to restart", Constants.WINDOW_WIDTH / 2 - 60, Constants.WINDOW_HEIGHT / 2 + 40);
                break;
                
            case UPGRADE_SELECTION:
                graphicsContext.fillText("UPGRADE SELECTION", Constants.WINDOW_WIDTH / 2 - 80, Constants.WINDOW_HEIGHT / 2 - 40);
                graphicsContext.fillText("Select your upgrade!", Constants.WINDOW_WIDTH / 2 - 70, Constants.WINDOW_HEIGHT / 2 - 20);
                graphicsContext.fillText("Press ENTER to continue", Constants.WINDOW_WIDTH / 2 - 80, Constants.WINDOW_HEIGHT / 2);
                break;
        }
    }

    /**
     * Gets the current game state.
     *
     * @return The current GameState
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current game state.
     *
     * @param state The new GameState
     */
    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    /**
     * Adds a trap to the game world.
     *
     * @param trap The trap to add
     */
    public void addTrap(Trap trap) {
        traps.add(trap);
        entities.add(trap);
    }

    /**
     * Gets the player character.
     *
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the list of all entities.
     *
     * @return The list of entities
     */
    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    /**
     * Gets the list of all traps.
     *
     * @return The list of traps
     */
    public List<Trap> getTraps() {
        return new ArrayList<>(traps);
    }

    /**
     * Removes all entities from the game world except the player.
     */
    public void clearEntities() {
        entities.removeIf(e -> e != player);
        traps.clear();
    }

    /**
     * Restarts the game.
     */
    public void restartGame() {
        // Clear all entities except player
        clearEntities();
        
        // Reset player
        player = new Player();
        entities.add(player);
        
        // Reset game director
        gameDirector.reset();
        gameDirector.initialize(player);
        
        // Reset game state
        setCurrentState(GameState.PLAYING);
    }
}