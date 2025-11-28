package com.pushknight.entities;

import com.pushknight.utils.Vector2D;
import javafx.scene.paint.Color;
import java.util.List;

/**
 * Goblin enemy - Fast, weak, basic melee enemy.
 */
public class Goblin extends Enemy {
    /**
     * Creates a Goblin at the specified position.
     * @param startX Initial X position
     * @param startY Initial Y position
     */
    public Goblin(double startX, double startY) {
        super(startX, startY);
        
        // Goblin stats
        this.maxHealth = 2;
        this.health = maxHealth;
        this.damage = 1;
        
        // THESE ARE NOT FINAL VALUES, JUST PLACEHOLDERS
        // Movement
        this.speed = 120.0; // Fast movement
        this.detectionRange = 400.0;
        
        // Combat
        this.attackRange = 30.0;
        this.attackCooldownMax = 1.0;
        
        // Collision
        this.hitboxRadius = 15.0;
        
        // Visual
        this.color = Color.GREEN;
    }
    
    @Override
    protected void performAttackBehavior(Player player) {
        if (attackCooldown <= 0 && player.isAlive()) {
            // Simple melee attack
            double distance = Vector2D.distance(position, player.getPosition());
            if (distance <= attackRange) {
                Vector2D attackDir = Vector2D.subtract(player.getPosition(), position);
                player.takeDamage(damage, attackDir, 150.0);
                attackCooldown = attackCooldownMax;
                currentAnimation = "attack";
            }
        }
    }
}

/**
 * Skeleton enemy - Balanced stats, medium speed and health.
 */
public class Skeleton extends Enemy {
    /**
     * Creates a Skeleton at the specified position.
     * @param startX Initial X position
     * @param startY Initial Y position
     */
    public Skeleton(double startX, double startY) {
        super(startX, startY);
        
        // Skeleton stats
        this.maxHealth = 3;
        this.health = maxHealth;
        this.damage = 1;
        
        // THESE ARE NOT FINAL VALUES, JUST PLACEHOLDERS
        // Movement
        this.speed = 90.0; // Medium speed
        this.detectionRange = 450.0;
        
        // Combat
        this.attackRange = 35.0;
        this.attackCooldownMax = 1.2;
        
        // Collision
        this.hitboxRadius = 18.0;
        
        // Visual
        this.color = Color.LIGHTGRAY;
    }
    
    @Override
    protected void performAttackBehavior(Player player) {
        if (attackCooldown <= 0 && player.isAlive()) {
            double distance = Vector2D.distance(position, player.getPosition());
            if (distance <= attackRange) {
                Vector2D attackDir = Vector2D.subtract(player.getPosition(), position);
                player.takeDamage(damage, attackDir, 180.0);
                attackCooldown = attackCooldownMax;
                currentAnimation = "attack";
            }
        }
    }
}

/**
 * Boomer Goblin - Explodes on death or when close to player, dealing area damage.
 */
public class BoomerGoblin extends Enemy {
    private boolean hasExploded;
    private double explosionRadius;
    private int explosionDamage;
    private double fuseTime;
    private double fuseTimer;
    private boolean fuseActive;
    
    /**
     * Creates a Boomer Goblin at the specified position.
     * @param startX Initial X position
     * @param startY Initial Y position
     */
    public BoomerGoblin(double startX, double startY) {
        super(startX, startY);
        
        // Boomer Goblin stats
        // THESE ARE NOT FINAL VALUES, JUST PLACEHOLDERS, ESPECIALLY FOR THIS GUY
        this.maxHealth = 1; // Very weak
        this.health = maxHealth;
        this.damage = 0; // No contact damage
        
        // Movement
        this.speed = 140.0; // Very fast
        this.detectionRange = 500.0;
        
        // Combat (explosion)
        this.attackRange = 60.0; // Explosion trigger range
        this.attackCooldownMax = 0;
        this.explosionRadius = 100.0;
        this.explosionDamage = 2; // High damage
        
        // Fuse mechanics
        this.fuseTime = 1.5; // 1.5 seconds fuse
        this.fuseTimer = 0;
        this.fuseActive = false;
        this.hasExploded = false;
        
        // Collision
        this.hitboxRadius = 16.0;
        
        // Visual
        this.color = Color.ORANGE;
    }
    
    @Override
    public void update(double deltaTime, Player player, List<Obstacle> obstacles, 
                      List<Enemy> enemies, List<Trap> traps) {
        if (!isAlive && !hasExploded) {
            // Explode on death
            explode(player, enemies);
            return;
        }
        
        if (fuseActive) {
            fuseTimer += deltaTime;
            if (fuseTimer >= fuseTime) {
                explode(player, enemies);
                return;
            }
        }
        
        super.update(deltaTime, player, obstacles, enemies, traps);
    }
    
    @Override
    protected void performAttackBehavior(Player player) {
        // Start fuse when in range
        if (!fuseActive && player.isAlive()) {
            double distance = Vector2D.distance(position, player.getPosition());
            if (distance <= attackRange) {
                fuseActive = true;
                fuseTimer = 0;
                currentAnimation = "fuse";
            }
        }
    }
    
    /**
     * Triggers explosion, damaging player and nearby enemies.
     * @param player Reference to the player
     * @param enemies List of other enemies
     */
    private void explode(Player player, List<Enemy> enemies) {
        if (hasExploded) return;
        
        hasExploded = true;
        isAlive = false;
        currentAnimation = "explode";
        
        // Damage player if in range
        if (player.isAlive()) {
            double distanceToPlayer = Vector2D.distance(position, player.getPosition());
            if (distanceToPlayer <= explosionRadius) {
                Vector2D knockbackDir = Vector2D.subtract(player.getPosition(), position);
                player.takeDamage(explosionDamage, knockbackDir, 300.0);
            }
        }
        
        // Damage other enemies in range
        for (Enemy enemy : enemies) {
            if (enemy == this || !enemy.isAlive()) continue;
            
            double distanceToEnemy = Vector2D.distance(position, enemy.getPosition());
            if (distanceToEnemy <= explosionRadius) {
                enemy.takeDamage(explosionDamage);
                Vector2D knockbackDir = Vector2D.subtract(enemy.getPosition(), position);
                knockbackDir.normalize();
                enemy.applyKnockback(knockbackDir, 200.0, new java.util.ArrayList<>(), new java.util.ArrayList<>());
            }
        }
    }
    
    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc, double cameraX, double cameraY) {
        if (hasExploded && !currentAnimation.equals("explode")) {
            return; // Don't render after explosion animation
        }
        
        double screenX = position.x - cameraX;
        double screenY = position.y - cameraY;
        
        // Render explosion radius when fuse is active
        if (fuseActive) {
            double alpha = Math.min(1.0, fuseTimer / fuseTime);
            gc.setFill(Color.rgb(255, 100, 0, 0.3 * alpha));
            gc.fillOval(screenX - explosionRadius, screenY - explosionRadius, 
                       explosionRadius * 2, explosionRadius * 2);
        }
        
        super.render(gc, cameraX, cameraY);
    }
    
    public boolean hasExploded() { return hasExploded; }
}

/**
 * Skeleton Brute - Slow, tanky enemy with high health and damage.
 */
public class SkeletonBrute extends Enemy {
    private double chargeSpeed;
    private boolean isCharging;
    private double chargeDuration;
    private double chargeTimer;
    private Vector2D chargeDirection;
    
    /**
     * Creates a Skeleton Brute at the specified position.
     * @param startX Initial X position
     * @param startY Initial Y position
     */
    public SkeletonBrute(double startX, double startY) {
        super(startX, startY);
        
        // Skeleton Brute stats
        // THESE ARE NOT FINAL VALUES, JUST PLACEHOLDERS
        this.maxHealth = 6; // Very tanky
        this.health = maxHealth;
        this.damage = 2; // High damage
        
        // Movement
        this.speed = 60.0; // Slow movement
        this.detectionRange = 400.0;
        
        // Combat
        this.attackRange = 40.0;
        this.attackCooldownMax = 2.0; // Long cooldown
        
        // Charge ability
        this.chargeSpeed = 250.0;
        this.isCharging = false;
        this.chargeDuration = 0.8;
        this.chargeTimer = 0;
        this.chargeDirection = null;
        
        // Collision
        this.hitboxRadius = 25.0; // Larger hitbox
        
        // Visual
        this.color = Color.DARKGRAY;
    }
    
    @Override
    public void update(double deltaTime, Player player, List<Obstacle> obstacles, 
                      List<Enemy> enemies, List<Trap> traps) {
        if (isCharging) {
            updateCharge(deltaTime, player, obstacles);
            checkTrapCollisions(traps);
            updateAnimation(deltaTime);
            return;
        }
        
        super.update(deltaTime, player, obstacles, enemies, traps);
    }
    
    @Override
    protected void performAttackBehavior(Player player) {
        if (attackCooldown <= 0 && player.isAlive() && !isCharging) {
            // Initiate charge attack
            Vector2D directionToPlayer = Vector2D.subtract(player.getPosition(), position);
            directionToPlayer.normalize();
            
            chargeDirection = directionToPlayer;
            isCharging = true;
            chargeTimer = 0;
            attackCooldown = attackCooldownMax;
            currentAnimation = "charge";
        }
    }
    
    /**
     * Updates charge attack behavior.
     * @param deltaTime Time elapsed in seconds
     * @param player Reference to the player
     * @param obstacles List of obstacles for collision
     */
    private void updateCharge(double deltaTime, Player player, List<Obstacle> obstacles) {
        chargeTimer += deltaTime;
        
        if (chargeTimer >= chargeDuration) {
            // End charge
            isCharging = false;
            velocity.x = 0;
            velocity.y = 0;
            return;
        }
        
        // Move in charge direction
        velocity.x = chargeDirection.x * chargeSpeed;
        velocity.y = chargeDirection.y * chargeSpeed;
        
        Vector2D newPosition = new Vector2D(
            position.x + velocity.x * deltaTime,
            position.y + velocity.y * deltaTime
        );
        
        // Check collision with obstacles (stops charge)
        for (Obstacle obstacle : obstacles) {
            if (checkCircleRectCollision(newPosition, hitboxRadius, obstacle)) {
                isCharging = false;
                velocity.x = 0;
                velocity.y = 0;
                return;
            }
        }
        
        // Check collision with player
        if (checkCircleCollision(newPosition, hitboxRadius, 
                                player.getPosition(), player.getHitboxRadius())) {
            // Deal damage to player
            player.takeDamage(damage, chargeDirection, 400.0);
            isCharging = false;
            velocity.x = 0;
            velocity.y = 0;
            return;
        }
        
        // Apply movement
        position.x = newPosition.x;
        position.y = newPosition.y;
    }
    
    @Override
    public void applyKnockback(Vector2D direction, double force, 
                              List<Obstacle> obstacles, List<Trap> traps) {
        // Brute is resistant to knockback (reduced by 50%)
        super.applyKnockback(direction, force * 0.5, obstacles, traps);
    }
}
