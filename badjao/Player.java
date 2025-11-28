package com.pushknight.entities;

import com.pushknight.utils.Vector2D;
import com.pushknight.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player character with health system, movement, push ability, and collision detection.
 */
public class Player {
    // Position and movement
    private Vector2D position;          // Current position in world coordinates
    private Vector2D velocity;          // Current velocity vector
    private Vector2D direction;         // Direction the player is facing
    
    // Player stats
    private int health;                 // Current health (in hearts)
    private int maxHealth;              // Maximum health capacity
    private boolean isAlive;            // Player alive status
    
    // Movement properties
    private double speed;               // Movement speed in pixels per second
    private boolean isMoving;           // Is player currently moving
    
    // Push ability
    private boolean canPush;            // Can player use push ability
    private double pushCooldown;        // Time remaining until push is available
    private double pushCooldownMax;     // Maximum push cooldown time
    private double pushRange;           // Range of push ability in pixels
    private double pushKnockback;       // Knockback force applied to enemies
    
    // Combat
    private boolean isInvulnerable;     // Invulnerability after taking damage
    private double invulnerabilityTime; // Time remaining for invulnerability
    private double invulnerabilityMax;  // Maximum invulnerability duration
    
    // Collision
    private double hitboxRadius;        // Collision radius for the player
    
    // Animation state
    private String currentAnimation;    // Current animation state
    private double animationTimer;      // Timer for animation frames
    
    // Input state
    private boolean movingUp;
    private boolean movingDown;
    private boolean movingLeft;
    private boolean movingRight;
    private boolean pushPressed;
    
    /**
     * Creates a new Player at the specified position.
     * @param startX Initial X position in world coordinates
     * @param startY Initial Y position in world coordinates
     */
    public Player(double startX, double startY) {
        this.position = new Vector2D(startX, startY);
        this.velocity = new Vector2D(0, 0);
        this.direction = new Vector2D(0, -1); // Facing up by default
        
        // Initialize health system (3 hearts)
        this.maxHealth = 3;
        this.health = maxHealth;
        this.isAlive = true;
        
        // Initialize movement
        this.speed = Constants.PLAYER_SPEED; // Define in Constants.java
        this.isMoving = false;
        
        // Initialize push ability
        this.canPush = true;
        this.pushCooldown = 0;
        this.pushCooldownMax = 1.0; // 1 second cooldown
        this.pushRange = 80.0; // 80 pixels range
        this.pushKnockback = 300.0; // Knockback force
        
        // Initialize invulnerability
        this.isInvulnerable = false;
        this.invulnerabilityTime = 0;
        this.invulnerabilityMax = 1.5; // 1.5 seconds of invulnerability
        
        // Collision
        this.hitboxRadius = 20.0;
        
        // Animation
        this.currentAnimation = "idle";
        this.animationTimer = 0;
    }
    
    /**
     * Updates player state based on delta time.
     * @param deltaTime Time elapsed since last update in seconds
     * @param obstacles List of obstacles for collision detection
     * @param enemies List of enemies for collision detection
     * @param traps List of traps for collision detection
     * @param powerups List of powerups for collision detection
     */
    public void update(double deltaTime, List<Obstacle> obstacles, 
                      List<Enemy> enemies, List<Trap> traps, List<Powerup> powerups) {
        if (!isAlive) return;
        
        // Update timers
        updateTimers(deltaTime);
        
        // Process input and update velocity
        updateMovement(deltaTime);
        
        // Apply movement with collision detection
        applyMovement(deltaTime, obstacles);
        
        // Check collision with enemies
        checkEnemyCollisions(enemies);
        
        // Check collision with traps
        checkTrapCollisions(traps);
        
        // Check collision with powerups
        checkPowerupCollisions(powerups);
        
        // Update animation state
        updateAnimation(deltaTime);
    }
    
    /**
     * Updates all timers (cooldowns, invulnerability, etc.)
     * @param deltaTime Time elapsed in seconds
     */
    private void updateTimers(double deltaTime) {
        // Push cooldown
        if (pushCooldown > 0) {
            pushCooldown -= deltaTime;
            if (pushCooldown <= 0) {
                pushCooldown = 0;
                canPush = true;
            }
        }
        
        // Invulnerability timer
        if (invulnerabilityTime > 0) {
            invulnerabilityTime -= deltaTime;
            if (invulnerabilityTime <= 0) {
                invulnerabilityTime = 0;
                isInvulnerable = false;
            }
        }
    }
    
    /**
     * Updates player velocity based on input.
     * @param deltaTime Time elapsed in seconds
     */
    private void updateMovement(double deltaTime) {
        Vector2D inputDirection = new Vector2D(0, 0);
        
        // Gather input
        if (movingUp) inputDirection.y -= 1;
        if (movingDown) inputDirection.y += 1;
        if (movingLeft) inputDirection.x -= 1;
        if (movingRight) inputDirection.x += 1;
        
        // Normalize input direction if moving diagonally
        if (inputDirection.length() > 0) {
            inputDirection.normalize();
            this.direction = inputDirection.copy(); // Update facing direction
            this.isMoving = true;
            
            // Set velocity
            velocity.x = inputDirection.x * speed;
            velocity.y = inputDirection.y * speed;
        } else {
            this.isMoving = false;
            velocity.x = 0;
            velocity.y = 0;
        }
    }
    
    /**
     * Applies movement to player position with collision detection.
     * @param deltaTime Time elapsed in seconds
     * @param obstacles List of obstacles to check collision against
     */
    private void applyMovement(double deltaTime, List<Obstacle> obstacles) {
        if (velocity.length() == 0) return;
        
        // Calculate new position
        Vector2D newPosition = new Vector2D(
            position.x + velocity.x * deltaTime,
            position.y + velocity.y * deltaTime
        );
        
        // Check collision with obstacles
        boolean collisionX = false;
        boolean collisionY = false;
        
        for (Obstacle obstacle : obstacles) {
            // Check X axis collision
            Vector2D testPosX = new Vector2D(newPosition.x, position.y);
            if (checkCircleRectCollision(testPosX, hitboxRadius, obstacle)) {
                collisionX = true;
            }
            
            // Check Y axis collision
            Vector2D testPosY = new Vector2D(position.x, newPosition.y);
            if (checkCircleRectCollision(testPosY, hitboxRadius, obstacle)) {
                collisionY = true;
            }
        }
        
        // Apply movement only on axes without collision
        if (!collisionX) {
            position.x = newPosition.x;
        }
        if (!collisionY) {
            position.y = newPosition.y;
        }
    }
    
    /**
     * Executes the push ability.
     * @param enemies List of enemies to potentially push
     * @param obstacles List of obstacles for push damage detection
     * @param traps List of traps for push damage detection
     */
    public void executePush(List<Enemy> enemies, List<Obstacle> obstacles, List<Trap> traps) {
        if (!canPush || !isAlive) return;
        
        // Trigger push
        canPush = false;
        pushCooldown = pushCooldownMax;
        currentAnimation = "push";
        
        // Find enemies in push range
        List<Enemy> affectedEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                double distance = Vector2D.distance(position, enemy.getPosition());
                if (distance <= pushRange) {
                    affectedEnemies.add(enemy);
                }
            }
        }
        
        // Apply knockback to affected enemies
        for (Enemy enemy : affectedEnemies) {
            Vector2D pushDirection = Vector2D.subtract(enemy.getPosition(), position);
            pushDirection.normalize();
            
            // Apply knockback force
            enemy.applyKnockback(pushDirection, pushKnockback, obstacles, traps);
        }
    }
    
    /**
     * Handles player taking damage.
     * @param damage Amount of damage (in hearts)
     * @param knockbackDirection Direction of knockback
     * @param knockbackForce Force of knockback
     */
    public void takeDamage(int damage, Vector2D knockbackDirection, double knockbackForce) {
        if (!isAlive || isInvulnerable) return;
        
        // Apply damage
        health -= damage;
        currentAnimation = "hurt";
        
        // Apply knockback
        if (knockbackDirection != null && knockbackForce > 0) {
            Vector2D knockback = knockbackDirection.copy();
            knockback.normalize();
            knockback.multiply(knockbackForce);
            
            // Apply instant knockback (simplified)
            position.x += knockback.x * 0.1;
            position.y += knockback.y * 0.1;
        }
        
        // Check if dead
        if (health <= 0) {
            health = 0;
            die();
        } else {
            // Start invulnerability
            isInvulnerable = true;
            invulnerabilityTime = invulnerabilityMax;
        }
    }
    
    /**
     * Handles player death.
     */
    private void die() {
        isAlive = false;
        currentAnimation = "death";
        velocity.x = 0;
        velocity.y = 0;
    }
    
    /**
     * Heals the player.
     * @param amount Amount of hearts to heal
     */
    public void heal(int amount) {
        if (!isAlive) return;
        health = Math.min(health + amount, maxHealth);
    }
    
    /**
     * Checks collision between player and enemies.
     * @param enemies List of enemies to check against
     */
    private void checkEnemyCollisions(List<Enemy> enemies) {
        if (isInvulnerable) return;
        
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && checkCircleCollision(position, hitboxRadius, 
                                                        enemy.getPosition(), enemy.getHitboxRadius())) {
                // Player takes damage from enemy
                Vector2D knockbackDir = Vector2D.subtract(position, enemy.getPosition());
                takeDamage(1, knockbackDir, 200.0);
                break; // Only one enemy can hit per frame
            }
        }
    }
    
    /**
     * Checks collision between player and traps.
     * @param traps List of traps to check against
     */
    private void checkTrapCollisions(List<Trap> traps) {
        if (isInvulnerable) return;
        
        for (Trap trap : traps) {
            if (trap.isActive() && checkCircleCollision(position, hitboxRadius, 
                                                        trap.getPosition(), trap.getRadius())) {
                // Player takes damage from trap
                takeDamage(trap.getDamage(), null, 0);
                trap.trigger(); // Deactivate trap after triggering
                break;
            }
        }
    }
    
    /**
     * Checks collision between player and powerups.
     * @param powerups List of powerups to check against
     */
    private void checkPowerupCollisions(List<Powerup> powerups) {
        List<Powerup> toRemove = new ArrayList<>();
        
        for (Powerup powerup : powerups) {
            if (checkCircleCollision(position, hitboxRadius, 
                                    powerup.getPosition(), powerup.getRadius())) {
                // Apply powerup effect
                powerup.applyEffect(this);
                toRemove.add(powerup);
            }
        }
        
        // Remove collected powerups
        powerups.removeAll(toRemove);
    }
    
    /**
     * Updates animation state based on player actions.
     * @param deltaTime Time elapsed in seconds
     */
    private void updateAnimation(double deltaTime) {
        animationTimer += deltaTime;
        
        // Determine animation state based on actions
        if (!isAlive) {
            currentAnimation = "death";
        } else if (currentAnimation.equals("hurt") && animationTimer > 0.3) {
            currentAnimation = isMoving ? "walk" : "idle";
            animationTimer = 0;
        } else if (currentAnimation.equals("push") && animationTimer > 0.4) {
            currentAnimation = isMoving ? "walk" : "idle";
            animationTimer = 0;
        } else if (!currentAnimation.equals("hurt") && !currentAnimation.equals("push")) {
            currentAnimation = isMoving ? "walk" : "idle";
        }
    }
    
    /**
     * Renders the player on the canvas.
     * @param gc Graphics context to draw on
     * @param cameraX Camera X offset
     * @param cameraY Camera Y offset
     */
    public void render(GraphicsContext gc, double cameraX, double cameraY) {
        double screenX = position.x - cameraX;
        double screenY = position.y - cameraY;
        
        // Flicker effect during invulnerability
        if (isInvulnerable && ((int)(invulnerabilityTime * 10) % 2 == 0)) {
            return; // Skip rendering for flicker effect
        }
        
        // to do: Render sprite based on currentAnimation and direction
        // For now, draw a circle as placeholder
        gc.setFill(Color.BLUE);
        gc.fillOval(screenX - hitboxRadius, screenY - hitboxRadius, 
                   hitboxRadius * 2, hitboxRadius * 2);
        
        // Draw direction indicator
        gc.setStroke(Color.YELLOW);
        gc.strokeLine(screenX, screenY, 
                     screenX + direction.x * 30, 
                     screenY + direction.y * 30);
    }
    
    /**
     * Renders player health UI.
     * @param gc Graphics context to draw on
     * @param x Screen X position for health display
     * @param y Screen Y position for health display
     */
    public void renderHealth(GraphicsContext gc, double x, double y) {
        double heartSize = 30;
        double spacing = 35;
        
        for (int i = 0; i < maxHealth; i++) {
            double heartX = x + i * spacing;
            if (i < health) {
                // Full heart
                gc.setFill(Color.RED);
            } else {
                // Empty heart
                gc.setFill(Color.DARKGRAY);
            }
            gc.fillOval(heartX, y, heartSize, heartSize);
        }
    }
    
    // COLLISION DETECTION HELPERS 
    
    /**
     * Checks collision between two circles.
     * @param pos1 Position of first circle
     * @param radius1 Radius of first circle
     * @param pos2 Position of second circle
     * @param radius2 Radius of second circle
     * @return True if circles are colliding
     */
    private boolean checkCircleCollision(Vector2D pos1, double radius1, 
                                        Vector2D pos2, double radius2) {
        double distance = Vector2D.distance(pos1, pos2);
        return distance < (radius1 + radius2);
    }
    
    /**
     * Checks collision between a circle and a rectangle.
     * @param circlePos Position of the circle
     * @param radius Radius of the circle
     * @param obstacle Rectangle obstacle to check against
     * @return True if circle and rectangle are colliding
     */
    private boolean checkCircleRectCollision(Vector2D circlePos, double radius, Obstacle obstacle) {
        // Find closest point on rectangle to circle center
        double closestX = Math.max(obstacle.getX(), Math.min(circlePos.x, obstacle.getX() + obstacle.getWidth()));
        double closestY = Math.max(obstacle.getY(), Math.min(circlePos.y, obstacle.getY() + obstacle.getHeight()));
        
        // Calculate distance
        double distanceX = circlePos.x - closestX;
        double distanceY = circlePos.y - closestY;
        double distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        
        return distanceSquared < (radius * radius);
    }
    
    // INPUT HANDLERS 
    
    public void setMovingUp(boolean moving) { this.movingUp = moving; }
    public void setMovingDown(boolean moving) { this.movingDown = moving; }
    public void setMovingLeft(boolean moving) { this.movingLeft = moving; }
    public void setMovingRight(boolean moving) { this.movingRight = moving; }
    public void setPushPressed(boolean pressed) { this.pushPressed = pressed; }
    
    // GETTERS AND SETTERS 
    
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public Vector2D getDirection() { return direction; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return isAlive; }
    public boolean isInvulnerable() { return isInvulnerable; }
    public double getHitboxRadius() { return hitboxRadius; }
    public double getPushRange() { return pushRange; }
    public boolean canPush() { return canPush; }
    public double getPushCooldown() { return pushCooldown; }
    public String getCurrentAnimation() { return currentAnimation; }
    
    public void setSpeed(double speed) { this.speed = speed; }
    public void setPushRange(double range) { this.pushRange = range; }
    public void setPushKnockback(double knockback) { this.pushKnockback = knockback; }
    public void setMaxHealth(int maxHealth) { 
        this.maxHealth = maxHealth;
        this.health = Math.min(this.health, maxHealth);
    }
}
