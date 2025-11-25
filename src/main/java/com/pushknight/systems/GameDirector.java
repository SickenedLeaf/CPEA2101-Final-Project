package com.pushknight.systems;

import com.pushknight.entities.*;
import com.pushknight.utils.Constants;
import com.pushknight.utils.EnemyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages enemy spawning, wave progression, and difficulty scaling.
 * Implements the Singleton pattern.
 */
public class GameDirector {
    private static GameDirector instance;

    private int currentWave;
    private int enemiesAlive;
    private int enemiesToSpawn;
    private double spawnRate;
    private boolean waveActive;
    private long timeSinceLastSpawn;
    private long waveStartTime;
    private long lastUpdateTime;
    private long waveCompleteTime;
    private boolean waveComplete;

    private List<Enemy> activeEnemies;
    private Player player;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private GameDirector() {
        this.currentWave = 0;
        this.enemiesAlive = 0;
        this.spawnRate = Constants.BASE_SPAWN_RATE;
        this.waveActive = false;
        this.timeSinceLastSpawn = 0;
        this.waveStartTime = 0;
        this.lastUpdateTime = 0;
        this.waveCompleteTime = 0;
        this.waveComplete = false;
        this.activeEnemies = new ArrayList<>();
    }

    /**
     * Gets the singleton instance of GameDirector.
     *
     * @return The GameDirector instance
     */
    public static GameDirector getInstance() {
        if (instance == null) {
            instance = new GameDirector();
        }
        return instance;
    }

    /**
     * Initializes the GameDirector with the player reference.
     *
     * @param player The player entity
     */
    public void initialize(Player player) {
        this.player = player;
        this.currentWave = 0;
        startNextWave();
    }

    /**
     * Starts the next wave of enemies.
     */
    public void startNextWave() {
        currentWave++;
        enemiesToSpawn = Constants.ENEMIES_PER_WAVE_BASE + (currentWave - 1) * Constants.ENEMIES_PER_WAVE_INCREASE;
        enemiesAlive = 0;
        spawnRate = Constants.BASE_SPAWN_RATE + (currentWave - 1) * Constants.SPAWN_RATE_INCREASE_PER_WAVE;
        waveActive = true;
        timeSinceLastSpawn = System.nanoTime();
        waveStartTime = System.nanoTime();
        waveComplete = false;
    }

    /**
     * Updates the GameDirector state based on elapsed time.
     *
     * @param now The current timestamp in nanoseconds
     */
    public void update(long now) {
        lastUpdateTime = now;

        if (waveActive && !waveComplete) {
            // Calculate time elapsed since last spawn
            long timeElapsed = now - timeSinceLastSpawn;
            double spawnInterval = 1.0 / spawnRate; // Convert spawn rate to interval in seconds
            long spawnIntervalNanos = (long) (spawnInterval * 1_000_000_000); // Convert to nanoseconds

            // Check if it's time to spawn a new enemy
            if (timeElapsed >= Math.max(spawnIntervalNanos, Constants.MIN_SPAWN_INTERVAL) && enemiesToSpawn > 0) {
                spawnEnemy();
                timeSinceLastSpawn = now;
                enemiesToSpawn--;
            }

            // Check if wave is complete (no more enemies to spawn and no enemies alive)
            if (enemiesToSpawn <= 0 && enemiesAlive <= 0) {
                completeWave(now);
            }
        }
    }

    /**
     * Spawns a new enemy with a random type.
     */
    private void spawnEnemy() {
        // Determine enemy type based on current wave
        EnemyType enemyType = getEnemyTypeForWave();

        // Calculate random spawn position at the edges of the screen
        double spawnX, spawnY;
        int side = (int) (Math.random() * 4); // 0: top, 1: right, 2: bottom, 3: left

        double margin = 50; // Keep enemies away from edges

        switch (side) {
            case 0: // Top
                spawnX = Math.random() * (Constants.WINDOW_WIDTH - 2 * margin) + margin;
                spawnY = -margin;
                break;
            case 1: // Right
                spawnX = Constants.WINDOW_WIDTH + margin;
                spawnY = Math.random() * (Constants.WINDOW_HEIGHT - 2 * margin) + margin;
                break;
            case 2: // Bottom
                spawnX = Math.random() * (Constants.WINDOW_WIDTH - 2 * margin) + margin;
                spawnY = Constants.WINDOW_HEIGHT + margin;
                break;
            default: // Left
                spawnX = -margin;
                spawnY = Math.random() * (Constants.WINDOW_HEIGHT - 2 * margin) + margin;
                break;
        }

        // Create enemy based on type
        Enemy newEnemy = createEnemyByType(enemyType, spawnX, spawnY);

        if (newEnemy != null) {
            // Set the player as the target for this enemy
            newEnemy.setTarget(player);
            
            // Add to active enemies list (this should be managed by the game controller)
            activeEnemies.add(newEnemy);
            enemiesAlive++;
        }
    }

    /**
     * Gets the appropriate enemy type for the current wave.
     * As waves progress, more difficult enemies are introduced.
     *
     * @return The enemy type to spawn
     */
    private EnemyType getEnemyTypeForWave() {
        // Probability of each enemy type
        double random = Math.random();

        if (currentWave < 3) {
            // Early waves: mostly Skeletons
            if (random < 0.8) {
                return EnemyType.SKELETON;
            } else {
                return EnemyType.GOBLIN;
            }
        } else if (currentWave < 6) {
            // Mid waves: mix of Skeletons and Goblins, some SkeletonBrutes
            if (random < 0.5) {
                return EnemyType.SKELETON;
            } else if (random < 0.85) {
                return EnemyType.GOBLIN;
            } else {
                return EnemyType.SKELETON_BRUTE;
            }
        } else {
            // Later waves: all enemy types including BoomerGoblins
            if (random < 0.3) {
                return EnemyType.SKELETON;
            } else if (random < 0.6) {
                return EnemyType.GOBLIN;
            } else if (random < 0.85) {
                return EnemyType.SKELETON_BRUTE;
            } else {
                return EnemyType.BOOMER_GOBLIN;
            }
        }
    }

    /**
     * Creates an enemy of the specified type at the given position.
     *
     * @param type The type of enemy to create
     * @param x    The x coordinate
     * @param y    The y coordinate
     * @return The created enemy, or null if the type is not recognized
     */
    private Enemy createEnemyByType(EnemyType type, double x, double y) {
        switch (type) {
            case SKELETON:
                return new Skeleton(x, y);
            case GOBLIN:
                return new Goblin(x, y);
            case SKELETON_BRUTE:
                return new SkeletonBrute(x, y);
            case BOOMER_GOBLIN:
                return new BoomerGoblin(x, y);
            default:
                return null;
        }
    }

    /**
     * Called when an enemy is defeated.
     * Updates the count of enemies alive.
     *
     * @param enemy The enemy that was defeated
     */
    public void onEnemyDefeated(Enemy enemy) {
        enemiesAlive--;
        activeEnemies.remove(enemy);
    }

    /**
     * Called when a wave is completed.
     *
     * @param now The current timestamp in nanoseconds
     */
    private void completeWave(long now) {
        waveActive = false;
        waveComplete = true;
        waveCompleteTime = now;
    }

    /**
     * Checks if the current wave is complete.
     *
     * @return true if the wave is complete, false otherwise
     */
    public boolean isWaveComplete() {
        return waveComplete;
    }

    /**
     * Gets the current wave number.
     *
     * @return The current wave
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Gets the number of enemies alive in the current wave.
     *
     * @return The number of enemies alive
     */
    public int getEnemiesAlive() {
        return enemiesAlive;
    }

    /**
     * Gets the number of enemies remaining to spawn in the current wave.
     *
     * @return The number of enemies to spawn
     */
    public int getEnemiesToSpawn() {
        return enemiesToSpawn;
    }

    /**
     * Gets the current spawn rate (enemies per second).
     *
     * @return The spawn rate
     */
    public double getSpawnRate() {
        return spawnRate;
    }

    /**
     * Checks if a wave is currently active.
     *
     * @return true if a wave is active, false otherwise
     */
    public boolean isWaveActive() {
        return waveActive;
    }

    /**
     * Gets the total number of enemies for the current wave.
     *
     * @return The total number of enemies in the current wave
     */
    public int getTotalEnemiesInWave() {
        return (Constants.ENEMIES_PER_WAVE_BASE + (currentWave - 1) * Constants.ENEMIES_PER_WAVE_INCREASE);
    }

    /**
     * Checks if enough time has passed since the wave completed to allow progression.
     *
     * @param now The current timestamp in nanoseconds
     * @return true if ready for next wave, false otherwise
     */
    public boolean isReadyForNextWave(long now) {
        return waveComplete && (now - waveCompleteTime) >= Constants.WAVE_COMPLETE_DELAY;
    }

    /**
     * Resets the game director to initial state.
     */
    public void reset() {
        currentWave = 0;
        enemiesAlive = 0;
        spawnRate = Constants.BASE_SPAWN_RATE;
        waveActive = false;
        waveComplete = false;
        activeEnemies.clear();
    }
}