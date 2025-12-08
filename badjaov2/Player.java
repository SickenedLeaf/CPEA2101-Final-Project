package application;

/**
 * Player character with health system and damage mechanics.
 * Takes damage from enemies and environmental hazards.
 */
public class Player {
    private int x;                          // Current X grid position
    private int y;                          // Current Y grid position
    private int health;                     // Current health (hearts)
    private int maxHealth;                  // Maximum health capacity
    private boolean isAlive;                // Is player alive
    private boolean isInvulnerable;         // Temporary invulnerability after damage
    private double invulnerabilityTimer;    // Time remaining for invulnerability
    private final double invulnerabilityDuration = 1.5; // Duration in seconds
    
    /**
     * Creates a player at the specified position.
     * @param x Initial X position
     * @param y Initial Y position
     */
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.maxHealth = 3;
        this.health = maxHealth;
        this.isAlive = true;
        this.isInvulnerable = false;
        this.invulnerabilityTimer = 0;
    }
    
    /**
     * Updates player state (invulnerability timer).
     * @param deltaTime Time elapsed in seconds
     */
    public void update(double deltaTime) {
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= deltaTime;
            if (invulnerabilityTimer <= 0) {
                invulnerabilityTimer = 0;
                isInvulnerable = false;
            }
        }
    }
    
    /**
     * Player takes damage from an enemy or hazard.
     * @param damage Amount of damage (in hearts)
     */
    public void takeDamage(int damage) {
        if (!isAlive || isInvulnerable) {
            return; // Can't take damage if dead or invulnerable
        }
        
        health -= damage;
        System.out.println("[DEBUG] Player took " + damage + " damage. Health: " + health + "/" + maxHealth);
        
        if (health <= 0) {
            health = 0;
            die();
        } else {
            // Start invulnerability period
            isInvulnerable = true;
            invulnerabilityTimer = invulnerabilityDuration;
        }
    }
    
    /**
     * Handles player death.
     */
    private void die() {
        isAlive = false;
        System.out.println("[GAME OVER] Player has died!");
    }
    
    /**
     * Heals the player.
     * @param amount Amount of hearts to heal
     */
    public void heal(int amount) {
        if (!isAlive) return;
        health = Math.min(health + amount, maxHealth);
        System.out.println("[DEBUG] Player healed " + amount + ". Health: " + health + "/" + maxHealth);
    }
    
    /**
     * Moves player to new position.
     * @param newX New X position
     * @param newY New Y position
     */
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
    
    /**
     * Checks if player should flicker (during invulnerability).
     * @return True if player should be invisible this frame
     */
    public boolean shouldFlicker() {
        if (!isInvulnerable) return false;
        // Flicker 10 times per second
        return ((int)(invulnerabilityTimer * 10) % 2 == 0);
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return isAlive; }
    public boolean isInvulnerable() { return isInvulnerable; }
    
    // Setters
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = Math.min(this.health, maxHealth);
    }
    
    @Override
    public String toString() {
        return "Player[x=" + x + ", y=" + y + ", health=" + health + "/" + maxHealth + ", alive=" + isAlive + "]";
    }
}
