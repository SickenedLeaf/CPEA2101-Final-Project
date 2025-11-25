package com.pushknight.objects;

import com.pushknight.entities.Character;
import com.pushknight.entities.GameObject;
import com.pushknight.utils.Constants;
import com.pushknight.utils.TrapType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Abstract base class for traps in the game.
 * Traps are environmental objects that can be triggered by enemies.
 * Extends GameObject and provides common trap functionality.
 */
public abstract class Trap extends GameObject {
    protected TrapType trapType;
    protected int damage;
    protected long cooldownTime;
    protected long lastActivationTime;
    protected boolean activated;
    
    /**
     * Creates a new trap at the specified position.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param trapType The type of trap
     * @param damage The damage this trap deals
     * @param cooldownTime The cooldown time in nanoseconds between activations
     */
    public Trap(double x, double y, TrapType trapType, int damage, long cooldownTime) {
        super(x, y, Constants.TRAP_WIDTH, Constants.TRAP_HEIGHT);
        this.trapType = trapType;
        this.damage = damage;
        this.cooldownTime = cooldownTime;
        this.lastActivationTime = 0;
        this.activated = false;
    }
    
    /**
     * Gets the trap type.
     * 
     * @return The trap type
     */
    public TrapType getTrapType() {
        return trapType;
    }
    
    /**
     * Gets the damage this trap deals.
     * 
     * @return The damage value
     */
    public int getDamage() {
        return damage;
    }
    
    /**
     * Sets the damage this trap deals.
     * 
     * @param damage The new damage value
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    /**
     * Checks if the trap can be activated (cooldown has passed).
     * 
     * @param now The current timestamp in nanoseconds
     * @return true if the trap can be activated, false otherwise
     */
    public boolean canActivate(long now) {
        return (now - lastActivationTime) >= cooldownTime;
    }
    
    /**
     * Checks if the trap is currently activated.
     * 
     * @return true if activated, false otherwise
     */
    public boolean isActivated() {
        return activated;
    }
    
    /**
     * Gets the time remaining on the cooldown in nanoseconds.
     * 
     * @param now The current timestamp in nanoseconds
     * @return The remaining cooldown time, or 0 if ready
     */
    public long getRemainingCooldown(long now) {
        long elapsed = now - lastActivationTime;
        if (elapsed >= cooldownTime) {
            return 0;
        }
        return cooldownTime - elapsed;
    }
    
    /**
     * Gets the cooldown progress as a percentage (0.0 to 1.0).
     * 
     * @param now The current timestamp in nanoseconds
     * @return The cooldown progress percentage
     */
    public double getCooldownProgress(long now) {
        if (cooldownTime <= 0) {
            return 1.0;
        }
        long remaining = getRemainingCooldown(now);
        return 1.0 - ((double) remaining / cooldownTime);
    }
    
    /**
     * Triggers the trap on an enemy.
     * Must be implemented by subclasses to define specific trap behavior.
     * 
     * @param enemy The enemy that triggered the trap
     * @param now The current timestamp in nanoseconds
     * @return true if the trap was successfully triggered, false otherwise
     */
    public abstract boolean trigger(Character enemy, long now);
    
    /**
     * Updates the trap state. Manages cooldown and activation state.
     * 
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Reset activated state after a short time
        if (activated && canActivate(now)) {
            activated = false;
        }
    }
    
    /**
     * Renders the trap. Provides a basic implementation that can be overridden.
     * 
     * @param gc The GraphicsContext to render to
     */
    @Override
    public void render(GraphicsContext gc) {
        // Basic trap rendering - subclasses should override for better visuals
        if (activated) {
            gc.setFill(Color.RED);
        } else if (canActivate(System.nanoTime())) {
            gc.setFill(Color.ORANGE);
        } else {
            gc.setFill(Color.GRAY);
        }
        
        gc.fillRect(x, y, width, height);
        
        // Draw border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(x, y, width, height);
    }
    
    /**
     * Activates the trap and records the activation time.
     * 
     * @param now The current timestamp in nanoseconds
     */
    protected void activate(long now) {
        this.activated = true;
        this.lastActivationTime = now;
    }
}

