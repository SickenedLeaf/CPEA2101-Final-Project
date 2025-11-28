package com.pushknight.utils;

/**
 * Contains all constant values used throughout the game.
 */
public class Constants {
    
    // WINDOW SETTINGS 
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final String GAME_TITLE = "Push Knight Peril";
    public static final int TARGET_FPS = 60;
    
    // PLAYER CONSTANTS
    public static final double PLAYER_SPEED = 200.0;              // pixels per second
    public static final int PLAYER_MAX_HEALTH = 3;                // hearts
    public static final double PLAYER_HITBOX_RADIUS = 20.0;       // pixels
    public static final double PLAYER_PUSH_RANGE = 80.0;          // pixels
    public static final double PLAYER_PUSH_KNOCKBACK = 300.0;     // force
    public static final double PLAYER_PUSH_COOLDOWN = 1.0;        // seconds
    public static final double PLAYER_INVULNERABILITY_TIME = 1.5; // seconds
    
    // CAMERA CONSTANTS 
    public static final double CAMERA_LERP_SPEED = 5.0;           // smoothing factor
    public static final double CAMERA_DEAD_ZONE = 50.0;           // pixels
    
    // ENEMY CONSTANTS
    
    // Goblin
    public static final double GOBLIN_SPEED = 120.0;
    public static final int GOBLIN_MAX_HEALTH = 2;
    public static final int GOBLIN_DAMAGE = 1;
    public static final double GOBLIN_DETECTION_RANGE = 400.0;
    public static final double GOBLIN_ATTACK_RANGE = 30.0;
    public static final double GOBLIN_HITBOX_RADIUS = 15.0;
    
    // Skeleton
    public static final double SKELETON_SPEED = 90.0;
    public static final int SKELETON_MAX_HEALTH = 3;
    public static final int SKELETON_DAMAGE = 1;
    public static final double SKELETON_DETECTION_RANGE = 450.0;
    public static final double SKELETON_ATTACK_RANGE = 35.0;
    public static final double SKELETON_HITBOX_RADIUS = 18.0;
    
    // Boomer Goblin
    public static final double BOOMER_SPEED = 140.0;
    public static final int BOOMER_MAX_HEALTH = 1;
    public static final int BOOMER_DAMAGE = 2;                    // explosion damage
    public static final double BOOMER_DETECTION_RANGE = 500.0;
    public static final double BOOMER_TRIGGER_RANGE = 60.0;
    public static final double BOOMER_EXPLOSION_RADIUS = 100.0;
    public static final double BOOMER_FUSE_TIME = 1.5;
    public static final double BOOMER_HITBOX_RADIUS = 16.0;
    
    // Skeleton Brute
    public static final double BRUTE_SPEED = 60.0;
    public static final int BRUTE_MAX_HEALTH = 6;
    public static final int BRUTE_DAMAGE = 2;
    public static final double BRUTE_DETECTION_RANGE = 400.0;
    public static final double BRUTE_ATTACK_RANGE = 40.0;
    public static final double BRUTE_CHARGE_SPEED = 250.0;
    public static final double BRUTE_CHARGE_DURATION = 0.8;
    public static final double BRUTE_HITBOX_RADIUS = 25.0;
    public static final double BRUTE_KNOCKBACK_RESISTANCE = 0.5;  // 50% resistance
    
    // PATHFINDING CONSTANTS 
    public static final int PATHFINDING_GRID_SIZE = 20;           // pixels per cell
    public static final int PATHFINDING_MAX_ITERATIONS = 1000;
    public static final double PATH_UPDATE_INTERVAL = 0.5;        // seconds
    
    // WORLD CONSTANTS 
    public static final int WORLD_WIDTH = 3200;                   // pixels
    public static final int WORLD_HEIGHT = 3200;                  // pixels
    
    //  OBSTACLE CONSTANTS 
    public static final int OBSTACLE_COLLISION_DAMAGE = 1;
    
    // TRAP CONSTANTS 
    public static final double TRAP_RADIUS = 30.0;                // pixels
    public static final int TRAP_DAMAGE = 1;
    public static final double TRAP_REARM_TIME = 3.0;             // seconds
    
    // POWERUP CONSTANTS 
    public static final double POWERUP_RADIUS = 20.0;             // pixels
    public static final double POWERUP_SPAWN_CHANCE = 0.15;       // 15% per enemy
    public static final double SPEED_BOOST_MULTIPLIER = 1.2;      // 20% increase
    public static final double PUSH_RANGE_MULTIPLIER = 1.3;       // 30% increase
    public static final double PUSH_POWER_MULTIPLIER = 1.3;       // 30% increase
    
    // SPAWN SYSTEM CONSTANTS 
    public static final double INITIAL_SPAWN_INTERVAL = 2.0;      // seconds
    public static final double MIN_SPAWN_INTERVAL = 0.5;          // seconds
    public static final int INITIAL_ENEMIES_PER_WAVE = 5;
    public static final double WAVE_SCALING_FACTOR = 2.5;         // enemies increase per wave
    
    // Wave 1-2: Basic enemies
    public static final double WAVE1_GOBLIN_RATE = 0.6;
    public static final double WAVE1_SKELETON_RATE = 0.3;
    public static final double WAVE1_BOOMER_RATE = 0.1;
    public static final double WAVE1_BRUTE_RATE = 0.0;
    
    // Wave 3-4: Brutes introduced
    public static final double WAVE3_GOBLIN_RATE = 0.4;
    public static final double WAVE3_SKELETON_RATE = 0.35;
    public static final double WAVE3_BOOMER_RATE = 0.15;
    public static final double WAVE3_BRUTE_RATE = 0.1;
    
    // Wave 5-9: Balanced composition
    public static final double WAVE5_GOBLIN_RATE = 0.3;
    public static final double WAVE5_SKELETON_RATE = 0.3;
    public static final double WAVE5_BOOMER_RATE = 0.2;
    public static final double WAVE5_BRUTE_RATE = 0.2;
    
    // Wave 10+: Late game
    public static final double WAVE10_GOBLIN_RATE = 0.2;
    public static final double WAVE10_SKELETON_RATE = 0.25;
    public static final double WAVE10_BOOMER_RATE = 0.25;
    public static final double WAVE10_BRUTE_RATE = 0.3;
    
    // ANIMATION CONSTANTS
    public static final double HURT_ANIMATION_DURATION = 0.3;     // seconds
    public static final double PUSH_ANIMATION_DURATION = 0.4;     // seconds
    public static final double ATTACK_ANIMATION_DURATION = 0.5;   // seconds
    public static final int INVULNERABILITY_FLICKER_RATE = 10;    // times per second
    
    // PHYSICS CONSTANTS 
    public static final double KNOCKBACK_DECAY = 0.9;             // per frame
    public static final double MIN_KNOCKBACK_VELOCITY = 10.0;     // pixels per second
    
    // UI CONSTANTS 
    public static final double HEALTH_HEART_SIZE = 30.0;          // pixels
    public static final double HEALTH_HEART_SPACING = 35.0;       // pixels
    public static final double HEALTH_UI_X = 20.0;                // screen position
    public static final double HEALTH_UI_Y = 20.0;                // screen position
    public static final double HEALTHBAR_WIDTH = 40.0;            // pixels
    public static final double HEALTHBAR_HEIGHT = 5.0;            // pixels
    public static final double HEALTHBAR_OFFSET_Y = 10.0;         // pixels above enemy
    
    // DEBUG CONSTANTS 
    public static final boolean DEBUG_MODE = false;
    public static final boolean SHOW_HITBOXES = false;
    public static final boolean SHOW_PATHFINDING = false;
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
