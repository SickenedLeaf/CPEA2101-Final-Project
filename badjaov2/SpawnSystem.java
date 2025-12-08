package application;

import java.util.*;

/**
 * Manages enemy spawning with wave-based difficulty progression.
 * Spawns enemies at designated spawn points with varying rates.
 */
public class SpawnSystem {
    private int currentWave;                    // Current wave number
    private double spawnTimer;                  // Timer for next spawn
    private double spawnInterval;               // Time between spawns
    private int enemiesPerWave;                 // Total enemies in current wave
    private int enemiesSpawned;                 // Enemies spawned so far
    private boolean waveActive;                 // Is a wave currently active
    private double waveDelay;                   // Delay before next wave starts
    private double waveDelayTimer;              // Timer for wave delay
    
    private List<SpawnPoint> spawnPoints;       // Available spawn locations
    private Random random;
    
    // Spawn rates for each enemy type (changes with waves)
    private double goblinRate;
    private double skeletonRate;
    private double boomerRate;
    private double bruteRate;
    
    /**
     * Inner class representing a spawn point location.
     */
    public static class SpawnPoint {
        public final int x;
        public final int y;
        
        public SpawnPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * Creates a spawn system with designated spawn points.
     * @param spawnPoints List of spawn point coordinates
     */
    public SpawnSystem(List<SpawnPoint> spawnPoints) {
        this.spawnPoints = spawnPoints;
        this.random = new Random();
        this.currentWave = 1;
        this.spawnTimer = 0;
        this.spawnInterval = 2.0;       // 2 seconds between spawns
        this.enemiesPerWave = 5;
        this.enemiesSpawned = 0;
        this.waveActive = true;
        this.waveDelay = 3.0;           // 3 seconds between waves
        this.waveDelayTimer = 0;
        
        // Wave 1 spawn rates
        updateSpawnRates();
    }
    
    /**
     * Updates spawn rates based on current wave.
     */
    private void updateSpawnRates() {
        if (currentWave <= 2) {
            // Early waves: mostly goblins
            goblinRate = 0.6;
            skeletonRate = 0.3;
            boomerRate = 0.1;
            bruteRate = 0.0;
        } else if (currentWave <= 4) {
            // Mid waves: introduce brutes
            goblinRate = 0.4;
            skeletonRate = 0.35;
            boomerRate = 0.15;
            bruteRate = 0.1;
        } else if (currentWave <= 9) {
            // Later waves: balanced mix
            goblinRate = 0.3;
            skeletonRate = 0.3;
            boomerRate = 0.2;
            bruteRate = 0.2;
        } else {
            // Late game: more dangerous enemies
            goblinRate = 0.2;
            skeletonRate = 0.25;
            boomerRate = 0.25;
            bruteRate = 0.3;
        }
    }
    
    /**
     * Updates the spawn system.
     * @param deltaTime Time elapsed since last update in seconds
     * @param enemies Current enemy list
     * @param grid Game grid for checking spawn validity
     * @return Newly spawned enemy, or null if none spawned
     */
    public Enemy update(double deltaTime, List<Enemy> enemies, int[][] grid) {
        // Check if wave is complete
        if (waveActive && enemiesSpawned >= enemiesPerWave) {
            // Check if all enemies are dead
            boolean allDead = true;
            for (Enemy enemy : enemies) {
                if (!enemy.isDead()) {
                    allDead = false;
                    break;
                }
            }
            
            if (allDead) {
                // Wave complete, start delay
                waveActive = false;
                waveDelayTimer = waveDelay;
                System.out.println("[WAVE] Wave " + currentWave + " complete! Next wave in " + waveDelay + " seconds...");
            }
        }
        
        // Handle wave delay
        if (!waveActive) {
            waveDelayTimer -= deltaTime;
            if (waveDelayTimer <= 0) {
                startNextWave();
            }
            return null;
        }
        
        // Spawn enemies during active wave
        if (enemiesSpawned < enemiesPerWave) {
            spawnTimer += deltaTime;
            
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0;
                return spawnEnemy(grid);
            }
        }
        
        return null;
    }
    
    /**
     * Spawns a random enemy at a valid spawn point.
     * @param grid Game grid for checking spawn validity
     * @return Newly spawned enemy, or null if no valid spawn point
     */
    private Enemy spawnEnemy(int[][] grid) {
        if (spawnPoints.isEmpty()) {
            System.err.println("[ERROR] No spawn points available!");
            return null;
        }
        
        // Find valid spawn point (not blocked)
        SpawnPoint spawnPoint = null;
        int attempts = 0;
        int maxAttempts = 20;
        
        while (attempts < maxAttempts) {
            SpawnPoint candidate = spawnPoints.get(random.nextInt(spawnPoints.size()));
            
            // Check if spawn point is empty
            if (grid[candidate.x][candidate.y] == 0) {
                spawnPoint = candidate;
                break;
            }
            attempts++;
        }
        
        if (spawnPoint == null) {
            System.out.println("[WARN] Could not find valid spawn point, trying again next interval");
            return null;
        }
        
        // Determine enemy type based on spawn rates
        Enemy.EnemyType type = rollEnemyType();
        Enemy enemy = new Enemy(spawnPoint.x, spawnPoint.y, type);
        
        enemiesSpawned++;
        System.out.println("[SPAWN] Spawned " + type + " at (" + spawnPoint.x + ", " + spawnPoint.y + 
                          ") [" + enemiesSpawned + "/" + enemiesPerWave + "]");
        
        return enemy;
    }
    
    /**
     * Randomly selects an enemy type based on spawn rates.
     * @return Enemy type
     */
    private Enemy.EnemyType rollEnemyType() {
        double roll = random.nextDouble();
        
        if (roll < goblinRate) {
            return Enemy.EnemyType.GOBLIN;
        } else if (roll < goblinRate + skeletonRate) {
            return Enemy.EnemyType.SKELETON;
        } else if (roll < goblinRate + skeletonRate + boomerRate) {
            return Enemy.EnemyType.BOOMER;
        } else {
            return Enemy.EnemyType.BRUTE;
        }
    }
    
    /**
     * Starts the next wave with increased difficulty.
     */
    private void startNextWave() {
        currentWave++;
        enemiesSpawned = 0;
        waveActive = true;
        
        // Increase difficulty
        enemiesPerWave = (int)(5 + currentWave * 2.5);
        spawnInterval = Math.max(0.5, 2.0 - currentWave * 0.1);
        
        updateSpawnRates();
        
        System.out.println("[WAVE] Starting Wave " + currentWave + " - " + enemiesPerWave + 
                          " enemies, spawn interval: " + String.format("%.1f", spawnInterval) + "s");
    }
    
    // Getters
    public int getCurrentWave() { return currentWave; }
    public int getEnemiesSpawned() { return enemiesSpawned; }
    public int getEnemiesPerWave() { return enemiesPerWave; }
    public boolean isWaveActive() { return waveActive; }
    public double getWaveDelayTimer() { return waveDelayTimer; }
}