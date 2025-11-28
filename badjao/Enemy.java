package com.pushknight.entities;

import com.pushknight.utils.Vector2D;
import com.pushknight.utils.AStar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.ArrayList;

/**
 * Base class for all enemy types with AI pathfinding, health system, and collision detection.
 */
public abstract class Enemy {
    // Position and movement
    protected Vector2D position;          // Current position in world coordinates
    protected Vector2D velocity;          // Current velocity vector
    protected Vector2D targetPosition;    // Target position to move towards
    
    // Enemy stats
    protected int health;                 // Current health points
    protected int maxHealth;              // Maximum health capacity
    protected boolean isAlive;            // Enemy alive status
    protected int damage;                 // Damage dealt to player on contact
    
    // Movement and Enemy AI
    protected double speed;               // Movement speed in pixels per second
    protected double detectionRange;      // Range at which enemy detects player
    protected List<Vector2D> path;        // Current pathfinding path
    protected int pathIndex;              // Current index in path
    protected double pathUpdateTimer;     // Timer for path recalculation
    protected double pathUpdateInterval;  // How often to recalculate path
    
    // Combat
    protected double attackCooldown;      // Time until next attack
    protected double attackCooldownMax;   // Maximum attack cooldown
    protected double attackRange;         // Range at which enemy attacks
    
    // Knockback
    protected Vector2D knockbackVelocity; // Current knockback velocity
    protected double knockbackDecay;      // How quickly knockback decays
    protected boolean isKnockedBack;      // Is enemy currently knocked back
    
    // Collision
    protected double hitboxRadius;        // Collision radius
    
    // Animation
    protected String currentAnimation;    // Current animation state
    protected double animationTimer;      // Timer for animation frames
    
    // Visual
    protected Color color;                // Enemy color (for rendering)
    
    /**
     * Creates a new Enemy at the specified position.
     * @param startX Initial X position
     * @param startY Initial Y position
     */
    public Enemy(double startX, double startY) {
        this.position = new Vector2D(startX, startY);
        this.velocity = new Vector2D(0, 0);
        this.knockbackVelocity = new Vector2D(0, 0);
        this.targetPosition = null;
        
        this.isAlive = true;
        this.path = new ArrayList<>();
        this.pathIndex = 0;
        this.pathUpdateTimer = 0;
        this.pathUpdateInterval = 0.5; // Update path every 0.5 seconds
        
        this.attackCooldown = 0;
        this.knockbackDecay = 0.9;
        this.isKnockedBack = false;
        
        this.currentAnimation = "idle";
        this.animationTimer = 0;
    }
    
    /**
     * Updates enemy state. To be called every frame.
     * @param deltaTime Time elapsed since last update in seconds
     * @param player Reference to the player
     * @param obstacles List of obstacles for pathfinding and collision
     * @param enemies List of other enemies for collision avoidance
     * @param traps List of traps for collision detection
     */
    public void update(double deltaTime, Player player, List<Obstacle> obstacles, 
                      List<Enemy> enemies, List<Trap> traps) {
        if (!isAlive) return;
        
        // Update timers
        updateTimers(deltaTime);
        
        // Update knockback
        if (isKnockedBack) {
            updateKnockback(deltaTime, obstacles);
            return; // Skip AI while knocked back
        }
        
        // Update AI behavior
        updateAI(deltaTime, player, obstacles);
        
        // Apply movement
        applyMovement(deltaTime, obstacles, enemies);
        
        // Check collision with traps
        checkTrapCollisions(traps);
        
        // Update animation
        updateAnimation(deltaTime);
    }
    
    /**
     * Updates timers (attack cooldown, path update, etc.)
     * @param deltaTime Time elapsed in seconds
     */
    protected void updateTimers(double deltaTime) {
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        
        pathUpdateTimer += deltaTime;
    }
    
    /**
     * Updates AI behavior (pathfinding and decision making).
     * @param deltaTime Time elapsed in seconds
     * @param player Reference to the player
     * @param obstacles List of obstacles for pathfinding
     */
    protected void updateAI(double deltaTime, Player player, List<Obstacle> obstacles) {
        if (!player.isAlive()) {
            velocity.x = 0;
            velocity.y = 0;
            return;
        }
        
        double distanceToPlayer = Vector2D.distance(position, player.getPosition());
        
        // Check if player is in detection range
        if (distanceToPlayer > detectionRange) {
            // Player out of range, stop moving
            velocity.x = 0;
            velocity.y = 0;
            return;
        }
        
        // Check if player is in attack range
        if (distanceToPlayer <= attackRange) {
            // In attack range, perform attack behavior
            performAttackBehavior(player);
            velocity.x = 0;
            velocity.y = 0;
            return;
        }
        
        // Update path to player if needed
        if (pathUpdateTimer >= pathUpdateInterval || path.isEmpty()) {
            updatePath(player.getPosition(), obstacles);
            pathUpdateTimer = 0;
        }
        
        // Follow path
        followPath(deltaTime);
    }
    
    /**
     * Updates the pathfinding path to target position using A* algorithm.
     * @param targetPos Target position to path to
     * @param obstacles List of obstacles to avoid
     */
    protected void updatePath(Vector2D targetPos, List<Obstacle> obstacles) {
        this.targetPosition = targetPos;
        
        // Use A* pathfinding
        path = AStar.findPath(position, targetPos, obstacles);
        pathIndex = 0;
    }
    
    /**
     * Follows the current pathfinding path.
     * @param deltaTime Time elapsed in seconds
     */
    protected void followPath(double deltaTime) {
        if (path == null || path.isEmpty() || pathIndex >= path.size()) {
            velocity.x = 0;
            velocity.y = 0;
            return;
        }
        
        Vector2D waypoint = path.get(pathIndex);
        Vector2D direction = Vector2D.subtract(waypoint, position);
        double distance = direction.length();
        
        // Check if reached waypoint
        if (distance < 10) {
            pathIndex++;
            if (pathIndex >= path.size()) {
                velocity.x = 0;
                velocity.y = 0;
                return;
            }
            waypoint = path.get(pathIndex);
            direction = Vector2D.subtract(waypoint, position);
        }
        
        // Move towards waypoint
        direction.normalize();
        velocity.x = direction.x * speed;
        velocity.y = direction.y * speed;
    }
    
    /**
     * Performs attack behavior when player is in range.
     * Abstract method to be implemented by specific enemy types.
     * @param player Reference to the player
     */
    protected abstract void performAttackBehavior(Player player);
    
    /**
     * Applies movement to enemy position with collision detection.
     * @param deltaTime Time elapsed in seconds
     * @param obstacles List of obstacles for collision
     * @param enemies List of other enemies for collision avoidance
     */
    protected void applyMovement(double deltaTime, List<Obstacle> obstacles, List<Enemy> enemies) {
        if (velocity.length() == 0) return;
        
        Vector2D newPosition = new Vector2D(
            position.x + velocity.x * deltaTime,
            position.y + velocity.y * deltaTime
        );
        
        // Check collision with obstacles
        boolean collisionX = false;
        boolean collisionY = false;
        
        for (Obstacle obstacle : obstacles) {
            Vector2D testPosX = new Vector2D(newPosition.x, position.y);
            if (checkCircleRectCollision(testPosX, hitboxRadius, obstacle)) {
                collisionX = true;
            }
            
            Vector2D testPosY = new Vector2D(position.x, newPosition.y);
            if (checkCircleRectCollision(testPosY, hitboxRadius, obstacle)) {
                collisionY = true;
            }
        }
        
        // Check collision with other enemies (simple separation)
        for (Enemy other : enemies) {
            if (other == this || !other.isAlive()) continue;
            
            Vector2D testPos = new Vector2D(newPosition.x, newPosition.y);
            if (checkCircleCollision(testPos, hitboxRadius, other.getPosition(), other.getHitboxRadius())) {
                // Push away from other enemy
                Vector2D pushDir = Vector2D.subtract(position, other.getPosition());
                pushDir.normalize();
                pushDir.multiply(20 * deltaTime);
                newPosition.x += pushDir.x;
                newPosition.y += pushDir.y;
            }
        }
        
        // Apply movement
        if (!collisionX) {
            position.x = newPosition.x;
        }
        if (!collisionY) {
            position.y = newPosition.y;
        }
    }
    
    /**
     * Applies knockback force to the enemy.
     * @param direction Direction of knockback (normalized)
     * @param force Knockback force magnitude
     * @param obstacles List of obstacles for collision damage
     * @param traps List of traps for collision damage
     */
    public void applyKnockback(Vector2D direction, double force, 
                              List<Obstacle> obstacles, List<Trap> traps) {
        knockbackVelocity.x = direction.x * force;
        knockbackVelocity.y = direction.y * force;
        isKnockedBack = true;
        currentAnimation = "knockback";
        
        // Check for collision damage during knockback
        checkKnockbackDamage(obstacles, traps);
    }
    
    /**
     * Updates knockback physics.
     * @param deltaTime Time elapsed in seconds
     * @param obstacles List of obstacles for collision
     */
    protected void updateKnockback(double deltaTime, List<Obstacle> obstacles) {
        // Apply knockback velocity
        Vector2D newPosition = new Vector2D(
            position.x + knockbackVelocity.x * deltaTime,
            position.y + knockbackVelocity.y * deltaTime
        );
        
        // Check collision with obstacles (stops knockback)
        boolean collision = false;
        for (Obstacle obstacle : obstacles) {
            if (checkCircleRectCollision(newPosition, hitboxRadius, obstacle)) {
                collision = true;
                break;
            }
        }
        
        if (!collision) {
            position.x = newPosition.x;
            position.y = newPosition.y;
        } else {
            // Stop knockback on collision
            knockbackVelocity.x = 0;
            knockbackVelocity.y = 0;
        }
        
        // Decay knockback velocity
        knockbackVelocity.multiply(knockbackDecay);
        
        // Stop knockback when velocity is low
        if (knockbackVelocity.length() < 10) {
            knockbackVelocity.x = 0;
            knockbackVelocity.y = 0;
            isKnockedBack = false;
        }
    }
    
    /**
     * Checks if enemy collides with obstacles or traps during knockback.
     * Deals damage if collision occurs.
     * @param obstacles List of obstacles
     * @param traps List of traps
     */
    protected void checkKnockbackDamage(List<Obstacle> obstacles, List<Trap> traps) {
        // Check obstacle collision
        for (Obstacle obstacle : obstacles) {
            if (checkCircleRectCollision(position, hitboxRadius, obstacle)) {
                takeDamage(obstacle.getCollisionDamage());
                return;
            }
        }
        
        // Check trap collision
        for (Trap trap : traps) {
            if (trap.isActive() && checkCircleCollision(position, hitboxRadius, 
                                                       trap.getPosition(), trap.getRadius())) {
                takeDamage(trap.getDamage());
                trap.trigger();
                return;
            }
        }
    }
    
    /**
     * Handles enemy taking damage.
     * @param damage Amount of damage to take
     */
    public void takeDamage(int damage) {
        if (!isAlive) return;
        
        health -= damage;
        currentAnimation = "hurt";
        
        if (health <= 0) {
            health = 0;
            die();
        }
    }
    
    /**
     * Handles enemy death.
     */
    protected void die() {
        isAlive = false;
        currentAnimation = "death";
        velocity.x = 0;
        velocity.y = 0;
        knockbackVelocity.x = 0;
        knockbackVelocity.y = 0;
    }
    
    /**
     * Checks collision with traps.
     * @param traps List of traps to check against
     */
    protected void checkTrapCollisions(List<Trap> traps) {
        for (Trap trap : traps) {
            if (trap.isActive() && checkCircleCollision(position, hitboxRadius, 
                                                       trap.getPosition(), trap.getRadius())) {
                takeDamage(trap.getDamage());
                trap.trigger();
                break;
            }
        }
    }
    
    /**
     * Updates animation state.
     * @param deltaTime Time elapsed in seconds
     */
    protected void updateAnimation(double deltaTime) {
        animationTimer += deltaTime;
        
        if (!isAlive) {
            currentAnimation = "death";
        } else if (currentAnimation.equals("hurt") && animationTimer > 0.3) {
            currentAnimation = velocity.length() > 0 ? "walk" : "idle";
            animationTimer = 0;
        } else if (currentAnimation.equals("knockback") && !isKnockedBack) {
            currentAnimation = "idle";
        } else if (!currentAnimation.equals("hurt") && !currentAnimation.equals("knockback")) {
            currentAnimation = velocity.length() > 0 ? "walk" : "idle";
        }
    }
    
    /**
     * Renders the enemy on the canvas.
     * @param gc Graphics context to draw on
     * @param cameraX Camera X offset
     * @param cameraY Camera Y offset
     */
    public void render(GraphicsContext gc, double cameraX, double cameraY) {
        double screenX = position.x - cameraX;
        double screenY = position.y - cameraY;
        
        // To do: Render sprite based on currentAnimation
        // For now, draw a circle
        gc.setFill(color);
        gc.fillOval(screenX - hitboxRadius, screenY - hitboxRadius, 
                   hitboxRadius * 2, hitboxRadius * 2);
        
        // Render health bar
        renderHealthBar(gc, screenX, screenY);
    }
    
    /**
     * Renders the enemy health bar above the enemy.
     * @param gc Graphics context to draw on
     * @param screenX Enemy screen X position
     * @param screenY Enemy screen Y position
     */
    protected void renderHealthBar(GraphicsContext gc, double screenX, double screenY) {
        double barWidth = 40;
        double barHeight = 5;
        double barX = screenX - barWidth / 2;
        double barY = screenY - hitboxRadius - 10;
        
        // Background
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(barX, barY, barWidth, barHeight);
        
        // Health
        double healthPercentage = (double) health / maxHealth;
        gc.setFill(Color.RED);
        gc.fillRect(barX, barY, barWidth * healthPercentage, barHeight);
        
        // Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }
    
    // COLLISION DETECTION HELPERS
    
    protected boolean checkCircleCollision(Vector2D pos1, double radius1, 
                                          Vector2D pos2, double radius2) {
        double distance = Vector2D.distance(pos1, pos2);
        return distance < (radius1 + radius2);
    }
    
    protected boolean checkCircleRectCollision(Vector2D circlePos, double radius, Obstacle obstacle) {
        double closestX = Math.max(obstacle.getX(), Math.min(circlePos.x, obstacle.getX() + obstacle.getWidth()));
        double closestY = Math.max(obstacle.getY(), Math.min(circlePos.y, obstacle.getY() + obstacle.getHeight()));
        
        double distanceX = circlePos.x - closestX;
        double distanceY = circlePos.y - closestY;
        double distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        
        return distanceSquared < (radius * radius);
    }
    
    // GETTERS
    
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return isAlive; }
    public double getHitboxRadius() { return hitboxRadius; }
    public int getDamage() { return damage; }
    public String getCurrentAnimation() { return currentAnimation; }
    public double getSpeed() { return speed; }
}
