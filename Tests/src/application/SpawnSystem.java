package application;

import java.util.*;

/**
 * Manages wave-based enemy spawning and book drops after waves.
 */
public class SpawnSystem {
    private int currentWave;
    private int totalWaves;
    private boolean endlessMode;
    private double waveDelay;
    private double waveDelayTimer;
    private boolean waveActive;
    
    private int enemiesPerWave;
    private int enemiesSpawned;
    private int enemiesDefeated;
    private double spawnTimer;
    private double spawnRate;
    private List<SpawnPoint> spawnPoints;
    private Random random;
    
    private int levelNumber;
    
    private double goblinRate;
    private double skeletonRate;
    private double boomerRate;
    private double bruteRate;
    
    private boolean shouldSpawnBook;
    private boolean bookSpawned;
    
    public static class SpawnPoint {
        public final int x;
        public final int y;
        
        public SpawnPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public SpawnSystem(List<SpawnPoint> spawnPoints, int levelNumber) {
        this.spawnPoints = spawnPoints;
        this.random = new Random();
        this.levelNumber = levelNumber;
        this.endlessMode = false;
        
        configureLevel(levelNumber);
        
        this.currentWave = 1;
        this.enemiesSpawned = 0;
        this.enemiesDefeated = 0;
        this.spawnTimer = 0;
        this.waveActive = true;
        this.waveDelay = 3.0;
        this.waveDelayTimer = 0;
        this.shouldSpawnBook = false;
        this.bookSpawned = false;
        
        updateSpawnRates();
        
        System.out.println("[SPAWN] Spawn system initialized for Level " + levelNumber);
        System.out.println("[SPAWN] Total waves: " + totalWaves + ", Starting wave 1");
    }
    
    public SpawnSystem(List<SpawnPoint> spawnPoints) {
        this(spawnPoints, 0);
        this.endlessMode = true;
        this.totalWaves = -1;
        System.out.println("[SPAWN] Endless mode initialized");
    }
    
    private void configureLevel(int level) {
        switch (level) {
            case 1:
                totalWaves = 5;
                enemiesPerWave = 5;
                spawnRate = 2.0;
                break;
            case 2:
                totalWaves = 7;
                enemiesPerWave = 8;
                spawnRate = 1.5;
                break;
            case 3:
                totalWaves = 10;
                enemiesPerWave = 12;
                spawnRate = 1.2;
                break;
            default:
                totalWaves = 5;
                enemiesPerWave = 5;
                spawnRate = 2.0;
        }
    }
    
    private void updateSpawnRates() {
        if (currentWave <= 2) {
            goblinRate = 0.3;
            skeletonRate = 0.5;
            boomerRate = 0.1;
            bruteRate = 0.1;
        } else if (currentWave <= 5) {
            goblinRate = 0.4;
            skeletonRate = 0.35;
            boomerRate = 0.15;
            bruteRate = 0.1;
        } else if (currentWave <= 10) {
            goblinRate = 0.3;
            skeletonRate = 0.3;
            boomerRate = 0.2;
            bruteRate = 0.2;
        } else {
            goblinRate = 0.2;
            skeletonRate = 0.25;
            boomerRate = 0.25;
            bruteRate = 0.3;
        }
    }
    
    public Enemy update(double deltaTime, List<Enemy> enemies, int[][] grid) {
        if (waveActive && enemiesSpawned >= enemiesPerWave) {
            int aliveCount = 0;
            for (Enemy enemy : enemies) {
                if (!enemy.isDead()) aliveCount++;
            }
            
            if (aliveCount == 0) {
                waveActive = false;
                waveDelayTimer = waveDelay;
                shouldSpawnBook = true;
                bookSpawned = false;
                System.out.println("[WAVE] Wave " + currentWave + " complete! " + 
                                 "Enemies defeated: " + enemiesDefeated);
                System.out.println("[WAVE] Book will spawn!");
                
                if (!endlessMode && currentWave >= totalWaves) {
                    System.out.println("[WAVE] Level " + levelNumber + " complete!");
                }
            }
        }
        
        if (!waveActive && !bookSpawned) {
            // Wait for book to be collected before starting next wave
            return null;
        }
        
        if (!waveActive && bookSpawned) {
            waveDelayTimer -= deltaTime;
            if (waveDelayTimer <= 0) {
                startNextWave();
            }
            return null;
        }
        
        if (enemiesSpawned < enemiesPerWave) {
            spawnTimer += deltaTime;
            
            if (spawnTimer >= spawnRate) {
                spawnTimer = 0;
                return spawnEnemy(grid);
            }
        }
        
        return null;
    }
    
    private Enemy spawnEnemy(int[][] grid) {
        if (spawnPoints.isEmpty()) {
            System.err.println("[ERROR] No spawn points available!");
            return null;
        }
        
        SpawnPoint spawnPoint = null;
        int attempts = 0;
        
        while (attempts < 20) {
            SpawnPoint candidate = spawnPoints.get(random.nextInt(spawnPoints.size()));
            
            if (grid[candidate.x][candidate.y] == 0) {
                spawnPoint = candidate;
                break;
            }
            attempts++;
        }
        
        if (spawnPoint == null) {
            System.out.println("[WARN] No valid spawn point found");
            return null;
        }
        
        Enemy.EnemyType type = rollEnemyType();
        Enemy enemy = createEnemy(spawnPoint.x, spawnPoint.y, type);
        
        enemiesSpawned++;
        System.out.println("[SPAWN] Spawned " + type + " at (" + spawnPoint.x + 
                          "," + spawnPoint.y + ") [Wave " + currentWave + ": " + 
                          enemiesSpawned + "/" + enemiesPerWave + "]");
        
        return enemy;
    }
    
    private Enemy createEnemy(int x, int y, Enemy.EnemyType type) {
        switch (type) {
            case GOBLIN:
                return new Goblin(x, y);
            case SKELETON:
                return new Skeleton(x, y);
            case BRUTE:
                return new SkeletonBrute(x, y);
            case BOOMER:
                return new BoomerGoblin(x, y);
            default:
                return new Goblin(x, y);
        }
    }
    
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
    
    private void startNextWave() {
        currentWave++;
        enemiesSpawned = 0;
        waveActive = true;
        shouldSpawnBook = false;
        bookSpawned = false;
        
        if (endlessMode) {
            enemiesPerWave = (int)(5 + currentWave * 2.5);
            spawnRate = Math.max(0.3, 2.0 - currentWave * 0.08);
        } else {
            enemiesPerWave = (int)(enemiesPerWave * 1.3);
            spawnRate = Math.max(0.5, spawnRate * 0.9);
        }
        
        updateSpawnRates();
        
        System.out.println("[WAVE] Starting Wave " + currentWave + 
                          " - Enemies: " + enemiesPerWave + 
                          ", Spawn rate: " + String.format("%.2f", spawnRate) + "s");
    }
    
    public void onEnemyDefeated() {
        enemiesDefeated++;
    }
    
    public void onBookCollected() {
        bookSpawned = true;
        shouldSpawnBook = false;
    }
    
    public boolean shouldSpawnBook() {
        return shouldSpawnBook && !bookSpawned;
    }
    
    public boolean isLevelComplete() {
        return !endlessMode && currentWave > totalWaves && !waveActive;
    }
    
    public int getCurrentWave() { return currentWave; }
    public int getTotalWaves() { return totalWaves; }
    public int getEnemiesSpawned() { return enemiesSpawned; }
    public int getEnemiesPerWave() { return enemiesPerWave; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public boolean isWaveActive() { return waveActive; }
    public boolean isEndlessMode() { return endlessMode; }
    public int getLevelNumber() { return levelNumber; }
}