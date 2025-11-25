package com.pushknight.entities;

import com.pushknight.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents a trap in the game that can damage enemies.
 * Extends GameObject and implements Updatable and Renderable.
 */
public class Trap extends GameObject {
    private int damage;
    private long cooldown;
    private long lastActivationTime;
    private boolean active;
    private boolean activatedThisFrame;

    /**
     * Creates a new Trap at the specified position.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Trap(double x, double y) {
        super(x, y, Constants.TRAP_WIDTH, Constants.TRAP_HEIGHT);
        this.damage = Constants.TRAP_DAMAGE;
        this.cooldown = Constants.TRAP_COOLDOWN;
        this.lastActivationTime = 0;
        this.active = true;
        this.activatedThisFrame = false;
    }

    /**
     * Updates the trap state based on elapsed time.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Reset activation status for this frame
        activatedThisFrame = false;

        // Check if trap is off cooldown and can become active again
        if (!active && (now - lastActivationTime) >= cooldown) {
            active = true;
        }
    }

    /**
     * Renders the trap to the graphics context.
     *
     * @param gc The GraphicsContext to render to
     */
    @Override
    public void render(GraphicsContext gc) {
        // Draw trap as a colored rectangle
        if (active) {
            // Active trap - red color
            gc.setFill(Color.RED);
        } else {
            // Inactive trap - gray color
            gc.setFill(Color.GRAY);
        }
        
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
        
        // Draw a border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(getX(), getY(), getWidth(), getHeight());
        
        // Draw an inner symbol to represent it's a trap
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        double centerX = getX() + getWidth() / 2;
        double centerY = getY() + getHeight() / 2;
        double radius = Math.min(getWidth(), getHeight()) * 0.3;
        
        // Draw a simple "X" symbol for the trap
        gc.strokeLine(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        gc.strokeLine(centerX - radius, centerY + radius, centerX + radius, centerY - radius);
    }

    /**
     * Triggers the trap, damaging the entity if the trap is active and not on cooldown.
     *
     * @param entity The entity to damage (typically an enemy)
     * @param now    The current timestamp in nanoseconds
     * @return true if the trap was successfully triggered, false otherwise
     */
    public boolean trigger(Entity entity, long now) {
        if (!active) {
            return false; // Trap is on cooldown
        }

        // Damage the entity
        if (entity instanceof Damageable) {
            ((Damageable) entity).takeDamage(damage);
            activatedThisFrame = true;
            active = false; // Trap becomes inactive after activation
            lastActivationTime = now;
            return true;
        }
        
        return false;
    }

    /**
     * Checks if the trap is currently active (not on cooldown).
     *
     * @return true if the trap is active, false if it's on cooldown
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the damage this trap inflicts.
     *
     * @return The damage value
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Sets the damage this trap inflicts.
     *
     * @param damage The new damage value
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Gets the cooldown time for this trap.
     *
     * @return The cooldown time in nanoseconds
     */
    public long getCooldown() {
        return cooldown;
    }

    /**
     * Sets the cooldown time for this trap.
     *
     * @param cooldown The new cooldown time in nanoseconds
     */
    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Checks if this trap was activated in the current frame.
     *
     * @return true if activated this frame, false otherwise
     */
    public boolean wasActivatedThisFrame() {
        return activatedThisFrame;
    }

    /**
     * Gets the time since this trap was last activated.
     *
     * @param now The current timestamp in nanoseconds
     * @return The time since last activation in nanoseconds
     */
    public long getTimeSinceLastActivation(long now) {
        return now - lastActivationTime;
    }

    /**
     * Resets the trap's cooldown, making it immediately available.
     *
     * @param now The current timestamp in nanoseconds
     */
    public void resetCooldown(long now) {
        active = true;
        lastActivationTime = now - cooldown; // Set time far enough back to allow immediate activation
    }
}