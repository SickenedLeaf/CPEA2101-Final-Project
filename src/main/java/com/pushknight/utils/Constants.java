package com.pushknight.utils;

/**
 * Constants class containing all game configuration values.
 * Centralizes game settings for easy tuning and maintenance.
 */
public class Constants {
    
    // Window dimensions
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    // Grid
    public static final int GRID_CELL_SIZE = 32;
    
    // Game timing
    public static final int TARGET_FPS = 60;
    public static final double FRAME_TIME = 1.0 / TARGET_FPS; // ~16.67ms per frame
    public static final long NANOS_PER_FRAME = (long) (1_000_000_000.0 / TARGET_FPS);
    
    // Player properties
    public static final double PLAYER_SPEED = 200.0; // pixels per second
    public static final long PLAYER_MOVEMENT_COOLDOWN = 100_000_000L; // 100ms in nanoseconds
    public static final int PLAYER_INITIAL_HEALTH = 100;
    public static final int PLAYER_INITIAL_MAX_HEALTH = 100;
    public static final double PLAYER_WIDTH = 32.0;
    public static final double PLAYER_HEIGHT = 32.0;
    
    // Push ability
    public static final double PUSH_FORCE = 300.0; // pixels per second
    public static final double PUSH_RADIUS = 80.0; // pixels
    public static final long PUSH_COOLDOWN = 500_000_000L; // 500ms in nanoseconds
    
    // Enemy properties - Skeleton
    public static final int SKELETON_HEALTH = 30;
    public static final int SKELETON_DAMAGE = 10;
    public static final double SKELETON_SPEED = 1.5; // pixels per frame (scaled)
    public static final double SKELETON_WIDTH = 24.0;
    public static final double SKELETON_HEIGHT = 24.0;
    
    // Enemy properties - Goblin
    public static final int GOBLIN_HEALTH = 20;
    public static final int GOBLIN_DAMAGE = 15;
    public static final double GOBLIN_SPEED = 2.0; // pixels per frame (scaled)
    public static final double GOBLIN_WIDTH = 24.0;
    public static final double GOBLIN_HEIGHT = 24.0;
    
    // Enemy properties - SkeletonBrute
    public static final int SKELETON_BRUTE_HEALTH = 80;
    public static final int SKELETON_BRUTE_DAMAGE = 20;
    public static final double SKELETON_BRUTE_SPEED = 0.8; // pixels per frame (scaled)
    public static final double SKELETON_BRUTE_WIDTH = 48.0;
    public static final double SKELETON_BRUTE_HEIGHT = 48.0;
    
    // Enemy properties - BoomerGoblin
    public static final int BOOMER_GOBLIN_HEALTH = 25;
    public static final int BOOMER_GOBLIN_DAMAGE = 30; // explosion damage
    public static final double BOOMER_GOBLIN_SPEED = 1.8; // pixels per frame (scaled)
    public static final double BOOMER_GOBLIN_WIDTH = 28.0;
    public static final double BOOMER_GOBLIN_HEIGHT = 28.0;
    public static final double BOOMER_GOBLIN_EXPLOSION_RADIUS = 100.0;
    
    // Spawning system
    public static final double BASE_SPAWN_RATE = 2.0; // enemies per second
    public static final double SPAWN_RATE_INCREASE_PER_WAVE = 0.3; // additional enemies per second
    public static final long MIN_SPAWN_INTERVAL = 500_000_000L; // 500ms minimum between spawns
    
    // Wave system
    public static final int ENEMIES_PER_WAVE_BASE = 10;
    public static final int ENEMIES_PER_WAVE_INCREASE = 5; // additional enemies per wave
    public static final long WAVE_START_DELAY = 2_000_000_000L; // 2 seconds before first spawn
    public static final long WAVE_COMPLETE_DELAY = 3_000_000_000L; // 3 seconds after wave complete
    
    // Damage system
    public static final long PLAYER_DAMAGE_COOLDOWN = 1_000_000_000L; // 1 second invincibility after hit
    
    // Trap properties
    public static final int TRAP_DAMAGE = 50;
    public static final long TRAP_COOLDOWN = 2_000_000_000L; // 2 seconds between activations
    public static final double TRAP_WIDTH = 40.0;
    public static final double TRAP_HEIGHT = 40.0;
    public static final int MAX_TRAPS = 5;
    
    // Score system
    public static final int SCORE_SKELETON = 10;
    public static final int SCORE_GOBLIN = 15;
    public static final int SCORE_SKELETON_BRUTE = 50;
    public static final int SCORE_BOOMER_GOBLIN = 25;
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }
}

