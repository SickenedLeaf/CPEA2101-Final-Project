package com.pushknight.entities;

/**
 * Abstract base class for characters in the game (Player and Enemies).
 * Extends Entity and implements Damageable interface to provide health management.
 * All characters have health, can take damage, and can be healed.
 */
public abstract class Character extends Entity implements Damageable {
    protected int health;
    protected int maxHealth;
    
    /**
     * Creates a new character at the specified position with the given dimensions and health.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param width The width of the character
     * @param height The height of the character
     * @param maxHealth The maximum health of the character
     */
    public Character(double x, double y, double width, double height, int maxHealth) {
        super(x, y, width, height);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }
    
    /**
     * Gets the current health of this character.
     * 
     * @return The current health value
     */
    @Override
    public int getHealth() {
        return health;
    }
    
    /**
     * Gets the maximum health of this character.
     * 
     * @return The maximum health value
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Sets the maximum health of this character.
     * Does not change current health unless it exceeds the new max.
     * 
     * @param maxHealth The new maximum health
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
    
    /**
     * Applies damage to this character.
     * Health cannot go below 0.
     * 
     * @param amount The amount of damage to apply
     */
    @Override
    public void takeDamage(int amount) {
        if (amount < 0) {
            return; // Don't allow negative damage
        }
        health = Math.max(0, health - amount);
    }
    
    /**
     * Heals this character by the specified amount.
     * Health cannot exceed maxHealth.
     * 
     * @param amount The amount of health to restore
     */
    public void heal(int amount) {
        if (amount < 0) {
            return; // Don't allow negative healing
        }
        health = Math.min(maxHealth, health + amount);
    }
    
    /**
     * Fully heals this character to maximum health.
     */
    public void healToFull() {
        health = maxHealth;
    }
    
    /**
     * Checks if this character is still alive.
     * 
     * @return true if health > 0, false otherwise
     */
    @Override
    public boolean isAlive() {
        return health > 0;
    }
    
    /**
     * Gets the health percentage (0.0 to 1.0).
     * 
     * @return The health percentage
     */
    public double getHealthPercentage() {
        if (maxHealth <= 0) {
            return 0.0;
        }
        return (double) health / maxHealth;
    }
}

