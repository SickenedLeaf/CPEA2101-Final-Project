package com.pushknight.entities;

import com.pushknight.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Abstract base class for all enemy entities.
 * Extends Character and adds enemy-specific properties.
 */
public abstract class Enemy extends Character {
    protected int damage;
    protected double speed;
    protected Player target; // The player the enemy is targeting
    protected long lastDamageTime;
    protected long damageCooldown;

    /**
     * Creates a new Enemy with the specified properties.
     *
     * @param x           The x coordinate
     * @param y           The y coordinate
     * @param width       The width of the enemy
     * @param height      The height of the enemy
     * @param health      The health of the enemy
     * @param damage      The damage this enemy inflicts on the player
     * @param speed       The movement speed of this enemy
     */
    public Enemy(double x, double y, double width, double height, int health, int damage, double speed) {
        super(x, y, width, height, health);
        this.damage = damage;
        this.speed = speed;
        this.target = null; // Will be set by game manager
        this.lastDamageTime = 0;
        this.damageCooldown = Constants.PLAYER_DAMAGE_COOLDOWN; // Use the player damage cooldown constant
    }

    /**
     * Gets the damage this enemy inflicts.
     *
     * @return The damage value
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the movement speed of this enemy.
     *
     * @return The speed value
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the target player for this enemy.
     *
     * @param target The player to target
     */
    public void setTarget(Player target) {
        this.target = target;
    }

    /**
     * Gets the target player for this enemy.
     *
     * @return The target player
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Performs the attack action against the player.
     * This method should be called when the enemy is in contact with the player.
     *
     * @param player The player to attack
     * @param now    The current timestamp in nanoseconds
     * @return true if the attack was successful, false if the enemy is still in cooldown
     */
    public boolean attack(Player player, long now) {
        if (player != null && (now - lastDamageTime) >= damageCooldown) {
            player.takeDamage(damage);
            lastDamageTime = now;
            return true;
        }
        return false;
    }

    /**
     * Renders the enemy with a simple color-coded rectangle based on type.
     *
     * @param gc The GraphicsContext to render to
     */
    @Override
    public void render(GraphicsContext gc) {
        // Draw enemy as a colored rectangle based on type (subclasses can override this)
        gc.setFill(getEnemyColor());
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
        
        // Draw a border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(getX(), getY(), getWidth(), getHeight());
        
        // Optional: Draw health bar above enemy
        double healthBarWidth = getWidth();
        double healthBarHeight = 3;
        double healthPercentage = getHealthPercentage();

        // Health bar background
        gc.setFill(Color.RED);
        gc.fillRect(getX(), getY() - 6, healthBarWidth, healthBarHeight);

        // Health bar fill
        gc.setFill(Color.GREEN);
        gc.fillRect(getX(), getY() - 6, healthBarWidth * healthPercentage, healthBarHeight);
    }

    /**
     * Gets the color used to render this enemy.
     * This method can be overridden by subclasses to provide different colors.
     *
     * @return The color for this enemy type
     */
    protected Color getEnemyColor() {
        // Default color - should be overridden by specific enemy types
        return Color.RED;
    }

    /**
     * Called when the enemy dies.
     * Subclasses should implement specific death behavior.
     */
    public abstract void onDeath();

    /**
     * Updates the enemy state based on elapsed time.
     * For basic enemies this includes updating attack cooldowns.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Call parent update method (Character)
        super.update(now);

        // Update attack cooldown tracking
        // Additional enemy-specific update code would go here
    }

    /**
     * Checks if this enemy can attack based on the damage cooldown.
     *
     * @param now The current timestamp in nanoseconds
     * @return true if the enemy can attack, false otherwise
     */
    public boolean canAttack(long now) {
        return (now - lastDamageTime) >= damageCooldown;
    }

    /**
     * Resets the attack cooldown, allowing the enemy to attack immediately.
     *
     * @param now The current timestamp in nanoseconds
     */
    public void resetAttackCooldown(long now) {
        lastDamageTime = now - damageCooldown; // Set time far enough back to allow immediate attack
    }
}