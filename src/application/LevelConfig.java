package application;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for game levels.
 * Defines enemy spawn rates, wave counts, and difficulty progression.
 */
public class LevelConfig {
    
    /**
     * Configuration for a single level.
     */
    public static class Level {
        public final int levelNumber;
        public final int totalWaves;
        public final int minEnemiesPerWave;
        public final int maxEnemiesPerWave;
        
        // Spawn configurations: wave number -> spawn rate in seconds
        public final Map<Integer, Double> goblinSpawnRates;
        public final Map<Integer, Double> skeletonSpawnRates;
        public final Map<Integer, Double> boomerSpawnRates;
        public final Map<Integer, Double> bruteSpawnRates;
        
        // Wave when each enemy type starts spawning
        public final int goblinStartWave;
        public final int skeletonStartWave;
        public final int boomerStartWave;
        public final int bruteStartWave;
        
        // Special rules
        public final int maxBrutesTotal; // -1 means unlimited
        
        public Level(int levelNumber, int totalWaves, int minEnemies, int maxEnemies) {
            this.levelNumber = levelNumber;
            this.totalWaves = totalWaves;
            this.minEnemiesPerWave = minEnemies;
            this.maxEnemiesPerWave = maxEnemies;
            
            this.goblinSpawnRates = new HashMap<>();
            this.skeletonSpawnRates = new HashMap<>();
            this.boomerSpawnRates = new HashMap<>();
            this.bruteSpawnRates = new HashMap<>();
            
            // Default start waves (will be overridden)
            this.goblinStartWave = 1;
            this.skeletonStartWave = 1;
            this.boomerStartWave = 1;
            this.bruteStartWave = 1;
            this.maxBrutesTotal = -1;
        }
        
        public Level(int levelNumber, int totalWaves, int minEnemies, int maxEnemies,
                    int goblinStart, int skeletonStart, int boomerStart, int bruteStart, int maxBrutes) {
            this.levelNumber = levelNumber;
            this.totalWaves = totalWaves;
            this.minEnemiesPerWave = minEnemies;
            this.maxEnemiesPerWave = maxEnemies;
            
            this.goblinSpawnRates = new HashMap<>();
            this.skeletonSpawnRates = new HashMap<>();
            this.boomerSpawnRates = new HashMap<>();
            this.bruteSpawnRates = new HashMap<>();
            
            this.goblinStartWave = goblinStart;
            this.skeletonStartWave = skeletonStart;
            this.boomerStartWave = boomerStart;
            this.bruteStartWave = bruteStart;
            this.maxBrutesTotal = maxBrutes;
        }
    }
    
    /**
     * Creates configuration for Level 1.
     */
    public static Level createLevel1() {
        Level level = new Level(1, 5, 15, 20, 1, 3, 5, -1, -1);
        
        // Goblins spawn from wave 1, every 6 seconds
        for (int wave = 1; wave <= 5; wave++) {
            level.goblinSpawnRates.put(wave, 6.0);
        }
        
        // Skeletons spawn from wave 3, every 8 seconds
        for (int wave = 3; wave <= 5; wave++) {
            level.skeletonSpawnRates.put(wave, 8.0);
        }
        
        // Boomers spawn only on wave 5, every 8 seconds
        level.boomerSpawnRates.put(5, 8.0);
        
        return level;
    }
    
    /**
     * Creates configuration for Level 2.
     */
    public static Level createLevel2() {
        Level level = new Level(2, 7, 25, 30, 1, 2, 4, 5, 1);
        
        // Goblins spawn from wave 1, every 5 seconds
        for (int wave = 1; wave <= 7; wave++) {
            level.goblinSpawnRates.put(wave, 5.0);
        }
        
        // Skeletons spawn from wave 2, every 6 seconds
        for (int wave = 2; wave <= 7; wave++) {
            level.skeletonSpawnRates.put(wave, 6.0);
        }
        
        // Boomers spawn from wave 4, every 7 seconds
        for (int wave = 4; wave <= 7; wave++) {
            level.boomerSpawnRates.put(wave, 7.0);
        }
        
        // Brutes spawn once on waves 5, 6, 7 (one per wave)
        for (int wave = 5; wave <= 7; wave++) {
            level.bruteSpawnRates.put(wave, 999.0); // Large number = spawn once only
        }
        
        return level;
    }
    
    /**
     * Creates configuration for Level 3.
     */
    public static Level createLevel3() {
        Level level = new Level(3, 10, 35, 45, 1, 1, 2, 6, 5);
        
        // Wave 1: Goblins and Skeletons every 4 seconds
        level.goblinSpawnRates.put(1, 4.0);
        level.skeletonSpawnRates.put(1, 4.0);
        
        // Wave 2: Only Boomers every 4 seconds
        level.boomerSpawnRates.put(2, 4.0);
        
        // Waves 3-5: Goblins, Skeletons, Boomers every 5 seconds
        for (int wave = 3; wave <= 5; wave++) {
            level.goblinSpawnRates.put(wave, 5.0);
            level.skeletonSpawnRates.put(wave, 5.0);
            level.boomerSpawnRates.put(wave, 5.0);
        }
        
        // Waves 6-8: 2 Brutes per wave, other enemies continue
        for (int wave = 6; wave <= 8; wave++) {
            level.goblinSpawnRates.put(wave, 5.0);
            level.skeletonSpawnRates.put(wave, 5.0);
            level.boomerSpawnRates.put(wave, 5.0);
            level.bruteSpawnRates.put(wave, 999.0); // Will spawn 2 manually
        }
        
        // Waves 9-10: All enemies every 4 seconds, Brutes every 15 seconds
        for (int wave = 9; wave <= 10; wave++) {
            level.goblinSpawnRates.put(wave, 4.0);
            level.skeletonSpawnRates.put(wave, 4.0);
            level.boomerSpawnRates.put(wave, 4.0);
            level.bruteSpawnRates.put(wave, 15.0);
        }
        
        return level;
    }
    
    /**
     * Gets the level configuration by level number.
     */
    public static Level getLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return createLevel1();
            case 2: return createLevel2();
            case 3: return createLevel3();
            default: return createLevel1();
        }
    }
}