package com.pushknight.entities;

/**
 * Abstract base class for character entities (Player, Enemy).
 * Extends Entity and implements Damageable interface.
 */
public abstract class Character extends Entity implements Damageable {
    protected int health;
    protected int maxHealth;

    /**
     * Creates a new character with the specified position, dimensions, and health.
     *
     * @param x      The x coordinate
     * @param y      The y coordinate
     * @param width  The width of the character
     * @param height The height of the character
     * @param health The initial health of the character
     */
    public Character(double x, double y, double width, double height, int health) {
        super(x, y, width, height);
        this.health = health;
        this.maxHealth = health;
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
     * Applies damage to this character.
     *
     * @param amount The amount of damage to apply
     */
    @Override
    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    /**
     * Heals this character by the specified amount.
     *
     * @param amount The amount to heal
     */
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    /**
     * Checks if this character is still alive.
     *
     * @return true if the character's health is greater than 0, false otherwise
     */
    @Override
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Sets the health of this character.
     *
     * @param health The new health value
     */
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }

    /**
     * Sets the maximum health of this character.
     *
     * @param maxHealth The new maximum health value
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = Math.min(health, maxHealth); // Ensure health doesn't exceed new max
    }

    /**
     * Gets the health percentage as a value between 0.0 and 1.0.
     *
     * @return The health percentage
     */
    public double getHealthPercentage() {
        return (double) health / maxHealth;
    }

    /**
     * Updates the character state based on elapsed time.
     * This method should be overridden by subclasses.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Default implementation does nothing
        // Subclasses should override this method
    }
}