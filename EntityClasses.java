package com.pushknight.entities;

import com.pushknight.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents an obstacle in the game world that blocks movement.
 */
public class Obstacle {
    private double x;                    // X position (top-left corner)
    private double y;                    // Y position (top-left corner)
    private double width;                // Width of obstacle
    private double height;               // Height of obstacle
    private int collisionDamage;         // Damage dealt when enemy is pushed into it
    
    /**
     * Creates an obstacle at the specified position.
     * @param x X position of top-left corner
     * @param y Y position of top-left corner
     * @param width Width of the obstacle
     * @param height Height of the obstacle
     */
    public Obstacle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collisionDamage = 1; // Default damage
    }
    
    /**
     * Renders the obstacle.
     * @param gc Graphics context to draw on
     * @param cameraX Camera X offset
     * @param cameraY Camera Y offset
     */
    public void render(GraphicsContext gc, double cameraX, double cameraY) {
        double screenX = x - cameraX;
        double screenY = y - cameraY;
        
        gc.setFill(Color.BROWN);
        gc.fillRect(screenX, screenY, width, height);
        
        gc.setStroke(Color.DARKGRAY);
        gc.strokeRect(screenX, screenY, width, height);
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getCollisionDamage() { return collisionDamage; }
    
    public void setCollisionDamage(int damage) { this.collisionDamage = damage; }
}

/**
 * Represents a trap that damages entities when triggered.
 */
public class Trap {
    private Vector2D position;          // Center position of trap
    private double radius;              // Activation radius
    private int damage;                 // Damage dealt
    private boolean active;             // Is trap currently active
    private boolean reusable;           // Can trap be triggered multiple times
    private double rearmTime;           // Time to rearm if reusable
    private double rearmTimer;          // Current rearm timer
    
    /**
     * Creates a trap at the specified position.
     * @param x X position (center)
     * @param y Y position (center)
     * @param radius Activation radius
     * @param damage Damage dealt to entities
     * @param reusable Whether trap can be triggered multiple times
     */
    public Trap(double x, double y, double radius, int damage, boolean reusable) {
        this.position = new Vector2D(x, y);
        this.radius = radius;
        this.damage = damage;
        this.active = true;
        this.reusable = reusable;
        this.rearmTime = 3.0; // 3 seconds to rearm
        this.rearmTimer = 0;
    }
    
    /**
     * Updates trap state (rearming timer).
     * @param deltaTime Time elapsed in seconds
     */
    public void update(double deltaTime) {
        if (!active && reusable) {
            rearmTimer += deltaTime;
            if (rearmTimer >= rearmTime) {
                active = true;
                rearmTimer = 0;
            }
        }
    }
    
    /**
     * Triggers the trap, deactivating it temporarily or permanently.
     */
    public void trigger() {
        active = false;
        rearmTimer = 0;
    }
    
    /**
     * Renders the trap.
     * @param gc Graphics context to draw on
     * @param cameraX Camera X offset
     * @param cameraY Camera Y offset
     */
    public void render(GraphicsContext gc, double cameraX, double cameraY) {
        double screenX = position.x - cameraX;
        double screenY = position.y - cameraY;
        
        if (active) {
            gc.setFill(Color.rgb(255, 0, 0, 0.5));
        } else {
            gc.setFill(Color.rgb(100, 100, 100, 0.3));
        }
        
        gc.fillOval(screenX - radius, screenY - radius, radius * 2, radius * 2);
        
        // Draw spikes or trap indicator
        gc.setStroke(active ? Color.DARKRED : Color.GRAY);
        gc.strokeOval(screenX - radius, screenY - radius, radius * 2, radius * 2);
    }
    
    // Getters
    public Vector2D getPosition() { return position; }
    public double getRadius() { return radius; }
    public int getDamage() { return damage; }
    public boolean isActive() { return active; }
}

/**
 * Represents a powerup that enhances player abilities.
 */
public class Powerup {
    private Vector2D position;          // Position of powerup
    private double radius;              // Collection radius
    private PowerupType type;           // Type of powerup
    private boolean collected;          // Has powerup been collected
    
    /**
     * Types of powerups available.
     */
    public enum PowerupType {
        HEALTH,         // Restores health
        SPEED,          // Increases movement speed
        PUSH_RANGE,     // Increases push ability range
        PUSH_POWER,     // Increases push knockback
        MAX_HEALTH      // Increases maximum health, BTW this is undecided, but i'll leave it here
    }
    
    /**
     * Creates a powerup at the specified position.
     * @param x X position
     * @param y Y position
     * @param type Type of powerup
     */
    public Powerup(double x, double y, PowerupType type) {
        this.position = new Vector2D(x, y);
        this.radius = 20.0;
        this.type = type;
        this.collected = false;
    }
    
    /**
     * Applies the powerup effect to the player.
     * @param player Player to apply effect to
     */
    public void applyEffect(Player player) {
        if (collected) return;
        
        collected = true;
        
        switch (type) {
            case HEALTH:
                player.heal(1);
                break;
            case SPEED:
                player.setSpeed(player.getSpeed() * 1.2); // 20% speed increase
                break;
            case PUSH_RANGE:
                player.setPushRange(player.getPushRange() * 1.3); // 30% range increase
                break;
            case PUSH_POWER:
                player.setPushKnockback(player.getPushKnockback() * 1.3); // 30% power increase
                break;
            case MAX_HEALTH:
                player.setMaxHealth(player.getMaxHealth() + 1);
                player.heal(1);
                break;
        }
    }
    
    /**
     * Renders the powerup.
     * @param gc Graphics context to draw on
     * @param cameraX Camera X offset
     * @param cameraY Camera Y offset
     */
    public void render(GraphicsContext gc, double cameraX, double cameraY) {
        if (collected) return;
        
        double screenX = position.x - cameraX;
        double screenY = position.y - cameraY;
        
        // Different colors for different powerup types
        Color color;
        switch (type) {
            case HEALTH: color = Color.RED; break;
            case SPEED: color = Color.CYAN; break;
            case PUSH_RANGE: color = Color.YELLOW; break;
            case PUSH_POWER: color = Color.ORANGE; break;
            case MAX_HEALTH: color = Color.PURPLE; break;
            default: color = Color.WHITE;
        }
        
        gc.setFill(color);
        gc.fillOval(screenX - radius, screenY - radius, radius * 2, radius * 2);
        
        gc.setStroke(Color.WHITE);
        gc.strokeOval(screenX - radius, screenY - radius, radius * 2, radius * 2);
    }
    
    // Getters
    public Vector2D getPosition() { return position; }
    public double getRadius() { return radius; }
    public PowerupType getType() { return type; }
    public boolean isCollected() { return collected; }
}

// SPAWN SYSTEM

/**
 * Manages enemy spawning with wave-based difficulty progression.
 */
public class SpawnSystem {
    private int currentWave;
    private double spawnTimer;
    private double spawnInterval;
    private int enemiesPerWave;
    private int enemiesSpawned;
    private List<Vector2D> spawnPoints;
    private Random random;
    
    // Spawn rates for each enemy type (increases with waves)
    private double goblinSpawnRate;
    private double skeletonSpawnRate;
    private double boomerGoblinSpawnRate;
    private double skeletonBruteSpawnRate;
    
    /**
     * Creates a spawn system with specified spawn points.
     * @param spawnPoints List of positions where enemies can spawn
     */
    public SpawnSystem(List<Vector2D> spawnPoints) {
        this.spawnPoints = spawnPoints;
        this.random = new Random();
        this.currentWave = 1;
        this.spawnTimer = 0;
        this.spawnInterval = 2.0; // 2 seconds between spawns
        this.enemiesPerWave = 5;
        this.enemiesSpawned = 0;
        
        // Initial spawn rates (wave 1)
        this.goblinSpawnRate = 0.6;       // 60% goblins
        this.skeletonSpawnRate = 0.3;     // 30% skeletons
        this.boomerGoblinSpawnRate = 0.1; // 10% boomer goblins
        this.skeletonBruteSpawnRate = 0.0; // 0% brutes (introduced later)
    }
    
    /**
     * Updates spawn system and spawns enemies.
     * @param deltaTime Time elapsed in seconds
     * @param enemies List to add spawned enemies to
     */
    public void update(double deltaTime, List<Enemy> enemies) {
        if (enemiesSpawned >= enemiesPerWave) {
            // Check if all enemies are dead to start next wave
            boolean allDead = true;
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    allDead = false;
                    break;
                }
            }
            
            if (allDead) {
                startNextWave();
            }
            return;
        }
        
        spawnTimer += deltaTime;
        
        if (spawnTimer >= spawnInterval) {
            spawnEnemy(enemies);
            spawnTimer = 0;
        }
    }
    
    /**
     * Spawns a random enemy at a random spawn point.
     * @param enemies List to add spawned enemy to
     */
    private void spawnEnemy(List<Enemy> enemies) {
        if (spawnPoints.isEmpty()) return;
        
        // Pick random spawn point
        Vector2D spawnPoint = spawnPoints.get(random.nextInt(spawnPoints.size()));
        
        // Determine enemy type based on spawn rates
        double roll = random.nextDouble();
        Enemy enemy;
        
        if (roll < goblinSpawnRate) {
            enemy = new Goblin(spawnPoint.x, spawnPoint.y);
        } else if (roll < goblinSpawnRate + skeletonSpawnRate) {
            enemy = new Skeleton(spawnPoint.x, spawnPoint.y);
        } else if (roll < goblinSpawnRate + skeletonSpawnRate + boomerGoblinSpawnRate) {
            enemy = new BoomerGoblin(spawnPoint.x, spawnPoint.y);
        } else {
            enemy = new SkeletonBrute(spawnPoint.x, spawnPoint.y);
        }
        
        enemies.add(enemy);
        enemiesSpawned++;
    }
    
    /**
     * Starts the next wave with increased difficulty.
     */
    private void startNextWave() {
        currentWave++;
        enemiesSpawned = 0;
        
        // Increase enemies per wave
        enemiesPerWave = (int)(5 + currentWave * 2.5);
        
        // Decrease spawn interval (faster spawning)
        spawnInterval = Math.max(0.5, 2.0 - currentWave * 0.1);
        
        // Adjust spawn rates for increased difficulty
        if (currentWave >= 3) {
            // Introduce skeleton brutes at wave 3
            goblinSpawnRate = 0.4;
            skeletonSpawnRate = 0.35;
            boomerGoblinSpawnRate = 0.15;
            skeletonBruteSpawnRate = 0.1;
        }
        
        if (currentWave >= 5) {
            // More varied composition at wave 5+
            goblinSpawnRate = 0.3;
            skeletonSpawnRate = 0.3;
            boomerGoblinSpawnRate = 0.2;
            skeletonBruteSpawnRate = 0.2;
        }
        
        if (currentWave >= 10) {
            // Late game composition
            goblinSpawnRate = 0.2;
            skeletonSpawnRate = 0.25;
            boomerGoblinSpawnRate = 0.25;
            skeletonBruteSpawnRate = 0.3;
        }
    }
    
    // Getters
    public int getCurrentWave() { return currentWave; }
    public int getEnemiesSpawned() { return enemiesSpawned; }
    public int getEnemiesPerWave() { return enemiesPerWave; }
}
