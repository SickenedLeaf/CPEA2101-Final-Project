package com.pushknight.systems;

import com.pushknight.entities.Player;

/**
 * Manages player upgrades and progression system.
 * Handles the selection and application of upgrades between waves.
 */
public class UpgradeManager {
    private static UpgradeManager instance;
    
    private Player player;
    
    /**
     * Private constructor to enforce Singleton pattern.
     */
    private UpgradeManager() {
    }
    
    /**
     * Gets the singleton instance of UpgradeManager.
     * 
     * @return The UpgradeManager instance
     */
    public static UpgradeManager getInstance() {
        if (instance == null) {
            instance = new UpgradeManager();
        }
        return instance;
    }
    
    /**
     * Sets the player for this upgrade manager.
     * 
     * @param player The player to apply upgrades to
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * Applies a health upgrade to the player.
     * Increases maximum health and heals the player.
     */
    public void applyHealthUpgrade() {
        if (player != null) {
            int newMaxHealth = player.getMaxHealth() + 25;
            player.setMaxHealth(newMaxHealth);
            player.heal(newMaxHealth); // Heal to full after increasing max health
        }
    }
    
    /**
     * Applies a speed upgrade to the player.
     * Increases player movement speed.
     */
    public void applySpeedUpgrade() {
        if (player != null) {
            // Reduce movement cooldown by 20% (increase speed)
            double currentSpeed = player.getMovement().getSpeed();
            player.getMovement().setSpeed(currentSpeed * 1.2);
        }
    }
    
    /**
     * Applies a damage upgrade to the player.
     * Increases push force.
     */
    public void applyDamageUpgrade() {
        // For now, push force is a constant, but we could add a multiplier
        // In a full implementation, this would increase the push damage
    }
    
    /**
     * Applies a cooldown reduction upgrade to the player.
     * Reduces movement cooldown.
     */
    public void applyCooldownReductionUpgrade() {
        if (player != null) {
            // Reduce movement cooldown by 15%
            long currentCooldown = player.getMovement().getCooldownTime();
            player.getMovement().setCooldownTime((long) (currentCooldown * 0.85));
        }
    }
    
    /**
     * Applies a trap upgrade to the player.
     * Increases trap capacity or damage.
     */
    public void applyTrapUpgrade() {
        // In a full implementation, this would increase trap effectiveness
        // For now, it's a placeholder
    }
    
    /**
     * Applies an area push upgrade to the player.
     * Increases push ability radius.
     */
    public void applyAreaPushUpgrade() {
        // In a full implementation, this would increase push radius
        // For now, it's a placeholder
    }
    
    /**
     * Applies a multi-push upgrade to the player.
     * Allows push to hit multiple times.
     */
    public void applyMultiPushUpgrade() {
        // In a full implementation, this would allow multiple pushes
        // For now, it's a placeholder
    }
    
    /**
     * Applies a score multiplier upgrade.
     * Increases points earned from defeating enemies.
     */
    public void applyScoreMultiplierUpgrade() {
        // In a full implementation, this would increase score multiplier
        // For now, it's a placeholder
    }
    
    /**
     * Resets the upgrade manager.
     */
    public void reset() {
        this.player = null;
    }
}