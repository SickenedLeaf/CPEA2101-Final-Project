package application;

public class Player {
    private int x, y;
    private int health;
    private int maxHealth;
    private boolean alive;
    private double pushCooldown;
    private double pushCooldownMax;
    
    // Invulnerability frames
    private double invulnerabilityTimer;
    private double invulnerabilityDuration;
    private boolean isInvulnerable;
    
    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.maxHealth = 3;
        this.health = maxHealth;
        this.alive = true;
        this.pushCooldown = 0;
        this.pushCooldownMax = 0.5;
        
        // I-frames configuration
        this.invulnerabilityTimer = 0;
        this.invulnerabilityDuration = 1.5; // 1.5 seconds of invulnerability
        this.isInvulnerable = false;
    }
    
    public void update(double deltaTime) {
        // Update push cooldown
        if (pushCooldown > 0) {
            pushCooldown -= deltaTime;
            if (pushCooldown < 0) pushCooldown = 0;
        }
        
        // Update invulnerability timer
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= deltaTime;
            if (invulnerabilityTimer <= 0) {
                invulnerabilityTimer = 0;
                isInvulnerable = false;
            }
        }
    }
    
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
    
    public void takeDamage(int damage) {
        if (!alive || isInvulnerable) return;
        
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
            System.out.println("[PLAYER] Player has died!");
        } else {
            // Activate invulnerability frames
            isInvulnerable = true;
            invulnerabilityTimer = invulnerabilityDuration;
            System.out.println("[PLAYER] Player took damage! HP: " + health + " (I-frames active)");
        }
    }
    
    public void heal(int amount) {
        if (!alive) return;
        health = Math.min(health + amount, maxHealth);
    }
    
    public boolean canPush() {
        return pushCooldown <= 0;
    }
    
    public void activatePushCooldown() {
        pushCooldown = pushCooldownMax;
    }
    
    // Check if player should flicker (for visual effect)
    public boolean shouldFlicker() {
        if (!isInvulnerable) return false;
        // Flicker at 10 times per second
        return ((int)(invulnerabilityTimer * 10) % 2) == 0;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return alive; }
    public double getPushCooldown() { return pushCooldown; }
    public double getPushCooldownMax() { return pushCooldownMax; }
    public boolean isInvulnerable() { return isInvulnerable; }
    public double getInvulnerabilityTimer() { return invulnerabilityTimer; }
}