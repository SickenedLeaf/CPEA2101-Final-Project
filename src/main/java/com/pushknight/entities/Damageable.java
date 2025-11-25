package com.pushknight.entities;

/**
 * Interface for entities that can take damage.
 * Implemented by player and enemies that have health.
 */
public interface Damageable {
    /**
     * Applies damage to this entity.
     * 
     * @param amount The amount of damage to apply
     */
    void takeDamage(int amount);
    
    /**
     * Checks if this entity is still alive.
     * 
     * @return true if the entity is alive, false otherwise
     */
    boolean isAlive();
    
    /**
     * Gets the current health of this entity.
     * 
     * @return The current health value
     */
    int getHealth();
}

