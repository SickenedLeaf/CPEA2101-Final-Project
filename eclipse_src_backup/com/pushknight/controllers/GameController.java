package com.pushknight.controllers;

import com.pushknight.entities.Character;
import com.pushknight.entities.Entity;
import com.pushknight.objects.Trap;
import com.pushknight.systems.CollisionSystem;
import com.pushknight.systems.GameEventManager;
import com.pushknight.utils.Constants;
import com.pushknight.utils.GameEventListener;
import com.pushknight.utils.GameState;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Main game controller that coordinates all game systems and entities.
 * Manages the game loop, entity updates, collision detection, and rendering.
 * Implements GameEventListener to respond to game events.
 */
public class GameController implements GameEventListener {
    // Game state
    private GameState gameState;
    private boolean paused;
    
    // Entity management
    private Character player;
    private List<Character> enemies;
    private List<Trap> traps;
    
    // Game statistics
    private int currentWave;
    private int score;
    private long gameStartTime;
    private int enemiesDefeated;
    
    // Systems
    private GameEventManager eventManager;
    
    // Rendering context
    private GraphicsContext gc;
    
    /**
     * Creates a new GameController.
     * 
     * @param gc The GraphicsContext for rendering
     */
    public GameController(GraphicsContext gc) {
        this.gc = gc;
        this.gameState = GameState.PLAYING;
        this.paused = false;
        
        // Initialize entity lists
        this.enemies = new ArrayList<>();
        this.traps = new ArrayList<>();
        
        // Initialize game statistics
        this.currentWave = 1;
        this.score = 0;
        this.gameStartTime = System.nanoTime();
        this.enemiesDefeated = 0;
        
        // Initialize event manager
        this.eventManager = GameEventManager.getInstance();
        this.eventManager.registerListener(this);
    }
    
    /**
     * Sets the player character.
     * 
     * @param player The player character
     */
    public void setPlayer(Character player) {
        this.player = player;
    }
    
    /**
     * Gets the player character.
     * 
     * @return The player character
     */
    public Character getPlayer() {
        return player;
    }
    
    /**
     * Adds an enemy to the game.
     * 
     * @param enemy The enemy to add
     */
    public void addEnemy(Character enemy) {
        if (enemy != null) {
            enemies.add(enemy);
        }
    }
    
    /**
     * Removes an enemy from the game.
     * 
     * @param enemy The enemy to remove
     */
    public void removeEnemy(Character enemy) {
        enemies.remove(enemy);
    }
    
    /**
     * Gets the list of enemies.
     * 
     * @return A copy of the enemies list
     */
    public List<Character> getEnemies() {
        return new ArrayList<>(enemies);
    }
    
    /**
     * Adds a trap to the game.
     * 
     * @param trap The trap to add
     */
    public void addTrap(Trap trap) {
        if (trap != null && traps.size() < Constants.MAX_TRAPS) {
            traps.add(trap);
        }
    }
    
    /**
     * Removes a trap from the game.
     * 
     * @param trap The trap to remove
     */
    public void removeTrap(Trap trap) {
        traps.remove(trap);
    }
    
    /**
     * Gets the list of traps.
     * 
     * @return A copy of the traps list
     */
    public List<Trap> getTraps() {
        return new ArrayList<>(traps);
    }
    
    /**
     * Gets the current wave number.
     * 
     * @return The current wave number
     */
    public int getCurrentWave() {
        return currentWave;
    }
    
    /**
     * Sets the current wave number.
     * 
     * @param wave The wave number
     */
    public void setCurrentWave(int wave) {
        this.currentWave = wave;
    }
    
    /**
     * Gets the current score.
     * 
     * @return The current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Adds to the score.
     * 
     * @param points The points to add
     */
    public void addScore(int points) {
        this.score += points;
        eventManager.fireScoreChanged(score);
    }
    
    /**
     * Gets the number of enemies defeated.
     * 
     * @return The number of enemies defeated
     */
    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }
    
    /**
     * Gets the time survived in seconds.
     * 
     * @return The time survived in seconds
     */
    public double getTimeSurvived() {
        return (System.nanoTime() - gameStartTime) / 1_000_000_000.0;
    }
    
    /**
     * Gets the number of enemies currently alive.
     * 
     * @return The number of alive enemies
     */
    public int getEnemiesAlive() {
        return enemies.size();
    }
    
    /**
     * Gets the number of traps placed.
     * 
     * @return The number of traps
     */
    public int getTrapCount() {
        return traps.size();
    }
    
    /**
     * Gets the current game state.
     * 
     * @return The game state
     */
    public GameState getGameState() {
        return gameState;
    }
    
    /**
     * Sets the game state.
     * 
     * @param state The new game state
     */
    public void setGameState(GameState state) {
        this.gameState = state;
    }
    
    /**
     * Checks if the game is paused.
     * 
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Pauses the game.
     */
    public void pause() {
        this.paused = true;
    }
    
    /**
     * Resumes the game.
     */
    public void resume() {
        this.paused = false;
    }
    
    /**
     * Toggles the pause state.
     */
    public void togglePause() {
        this.paused = !this.paused;
    }
    
    /**
     * Resets the game to initial state.
     */
    public void reset() {
        enemies.clear();
        traps.clear();
        currentWave = 1;
        score = 0;
        gameStartTime = System.nanoTime();
        enemiesDefeated = 0;
        paused = false;
        gameState = GameState.PLAYING;
    }
    
    /**
     * Updates all game entities and systems.
     * 
     * @param deltaTime The time elapsed since last frame in seconds
     * @param now The current timestamp in nanoseconds
     */
    public void update(double deltaTime, long now) {
        if (paused || gameState != GameState.PLAYING) {
            return;
        }
        
        // Update player
        if (player != null && player.isAlive()) {
            player.update(now);
            CollisionSystem.clampToBounds(player, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        }
        
        // Update enemies
        Iterator<Character> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Character enemy = enemyIterator.next();
            if (enemy.isAlive()) {
                enemy.update(now);
                CollisionSystem.clampToBounds(enemy, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            } else {
                // Enemy died - remove it
                enemyIterator.remove();
                enemiesDefeated++;
                eventManager.fireEnemyDefeated(enemy);
            }
        }
        
        // Update traps
        for (Trap trap : traps) {
            trap.update(now);
        }
        
        // Check collisions
        checkCollisions(now);
    }
    
    /**
     * Checks for collisions between entities.
     * 
     * @param now The current timestamp in nanoseconds
     */
    private void checkCollisions(long now) {
        if (player == null || !player.isAlive()) {
            return;
        }
        
        // Check player-enemy collisions
        for (Character enemy : enemies) {
            if (enemy.isAlive() && CollisionSystem.checkCollision(player, enemy)) {
                // Player takes damage (damage cooldown handled by player/enemy logic)
                // This is a placeholder - actual damage logic will be in player/enemy classes
            }
        }
        
        // Check enemy-trap collisions
        for (Trap trap : traps) {
            for (Character enemy : enemies) {
                if (enemy.isAlive() && CollisionSystem.checkCollision(trap, enemy)) {
                    if (trap.trigger(enemy, now)) {
                        eventManager.fireTrapTriggered(trap.getTrapType().toString());
                    }
                }
            }
        }
        
        // Check enemy-enemy collisions (for obstacle behavior)
        // This will be handled by enemy AI logic
    }
    
    /**
     * Renders all game entities.
     */
    public void render() {
        if (gc == null) {
            return;
        }
        
        // Render traps first (background layer)
        for (Trap trap : traps) {
            trap.render(gc);
        }
        
        // Render enemies
        for (Character enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.render(gc);
            }
        }
        
        // Render player (foreground layer)
        if (player != null && player.isAlive()) {
            player.render(gc);
        }
    }
    
    // GameEventListener implementation
    
    @Override
    public void onEnemyDefeated(Character enemy) {
        // Score is handled by the system that defeats the enemy
    }
    
    @Override
    public void onWaveComplete(int waveNumber) {
        // Wave completion logic will be handled by GameDirector
    }
    
    @Override
    public void onPlayerDamaged(int currentHealth, int maxHealth) {
        // Player damage feedback
    }
    
    @Override
    public void onPlayerDeath() {
        gameState = GameState.GAME_OVER;
    }
    
    @Override
    public void onWaveStart(int waveNumber) {
        this.currentWave = waveNumber;
    }
    
    @Override
    public void onTrapTriggered(String trapType) {
        // Trap trigger feedback
    }
    
    @Override
    public void onScoreChanged(int newScore) {
        // Score change feedback
    }
}

