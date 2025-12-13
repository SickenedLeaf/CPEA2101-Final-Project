package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
    private int x, y;
    private int health;
    private int maxHealth;
    private boolean alive;
    private double pushCooldown;
    private double pushCooldownMax;
    private String direction;
    
    // Invulnerability frames
    private double invulnerabilityTimer;
    private double invulnerabilityDuration;
    private boolean isInvulnerable;

    // Sprite animator instance
    private SpriteAnimator animator;
    

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
        this.direction = "RIGHT";

        // Initialize animator
        this.animator = new SpriteAnimator(new Image(getClass().getResource("/assets/player/PushKnight.png").toExternalForm()), 72, 72);
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
        // Trigger movement animation (row 0, frames 0–3 as example)
        if ("DOWN".equals(this.direction)) {
            animator.playAction(2, 0, 6, 50_000_000, false, () -> playIdleAnimation());
        } else if ("UP".equals(this.direction)) {
            animator.playAction(2, 6, 6, 50_000_000, false, () -> playIdleAnimation());
        } else if ("RIGHT".equals(this.direction)) {
            animator.playAction(1, 0, 6, 50_000_000, false, () -> playIdleAnimation());
        } else if ("LEFT".equals(this.direction)) {
            animator.playAction(1, 6, 6, 50_000_000, false, () -> playIdleAnimation());
        }

    }
    
    public void takeDamage(int damage) {
        if (!alive || isInvulnerable) return;

        health -= damage;
        if ("DOWN".equals(this.direction)) {
            animator.playAction(5, 6, 3, 80_000_000, false, () -> playIdleAnimation());
        } else if ("UP".equals(this.direction)) {
            animator.playAction(5, 9, 3, 80_000_000, false, () -> playIdleAnimation());
        } else if ("RIGHT".equals(this.direction)) {
            animator.playAction(5, 0, 3, 80_000_000, false, () -> playIdleAnimation());
        } else if ("LEFT".equals(this.direction)) {
            animator.playAction(5, 3, 3, 80_000_000, false, () -> playIdleAnimation());
        }
        if (health <= 0) {
            health = 0;
            alive = false;
            System.out.println("[PLAYER] Player has died!");
            // Play death animation (row 2, frames 0–5 as example)
            AudioManager.getInstance().playPlayerSound("PLAYER_DEATH"); // Play death sound
        } else {
            // Activate invulnerability frames
            isInvulnerable = true;
            invulnerabilityTimer = invulnerabilityDuration;
            System.out.println("[PLAYER] Player took damage! HP: " + health + " (I-frames active)");
            // Play hurt animation (row 1, frames 0–2 as example)
            AudioManager.getInstance().playPlayerSound("DAMAGE"); // Play damage sound
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
        playPushAnimation();  // Play push animation
    }

    public void playPushAnimation() {
        if (animator != null) {
            // Play push/attack animation based on direction
            // Assuming push animation is similar to attack animation
            if("DOWN".equals(this.direction)) {
                animator.playAction(4, 0, 6, 100_000_000, false, () -> playIdleAnimation());
            } else if ("UP".equals(this.direction)) {
                animator.playAction(4, 6, 6, 100_000_000, false, () -> playIdleAnimation());
            } else if ("RIGHT".equals(this.direction)) {
                animator.playAction(3, 0, 6, 100_000_000, false, () -> playIdleAnimation());
            } else if ("LEFT".equals(this.direction)) {
                animator.playAction(3, 6, 6, 100_000_000, false, () -> playIdleAnimation());
            }
        }
    }
    
    // Check if player should flicker (for visual effect)
    public boolean shouldFlicker() {
        if (!isInvulnerable) return false;
        // Flicker at 10 times per second
        return ((int)(invulnerabilityTimer * 10) % 2) == 0;
    }
    
    public void setDirection(String direction) {
    	this.direction = direction;
    }
    
    public void playAttackAnimation() {
    	if (animator != null) {
	    	if("DOWN".equals(this.direction)) {
	    		animator.playAction(4, 0, 6, 50_000_000, false, () -> playIdleAnimation()); 
            } else if ("UP".equals(this.direction)) {
            	animator.playAction(4, 6, 6, 50_000_000, false, () -> playIdleAnimation()); 
            } else if ("RIGHT".equals(this.direction)) {
            	animator.playAction(3, 0, 6, 50_000_000, false, () -> playIdleAnimation()); 
            } else if ("LEFT".equals(this.direction)) {
            	animator.playAction(3, 6, 6, 50_000_000, false, () -> playIdleAnimation()); 
            }
	    }
    }
    
    public void playIdleAnimation() {
	    if (animator != null) {
	    	if("DOWN".equals(this.direction)) {
	    		animator.playAction(0, 6, 3, 500_000_000, true, null); 
            } else if ("UP".equals(this.direction)) {
            	animator.playAction(0, 9, 3, 500_000_000, true, null); 
            } else if ("RIGHT".equals(this.direction)) {
            	animator.playAction(0, 0, 3, 500_000_000, true, null); 
            } else if ("LEFT".equals(this.direction)) {
            	animator.playAction(0, 3, 3, 500_000_000, true, null); 
            }
	    }
	}

    // Accessor for rendering
    public ImageView getImageView() {
        return animator.getImageView();
    }
    public String getDirection() { return direction;}
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return alive; }
    public double getPushCooldown() { return pushCooldown; }
    public double getPushCooldownMax() { return pushCooldownMax; }
    public boolean isInvulnerable() { return isInvulnerable; }
    public double getInvulnerabilityTimer() { return invulnerabilityTimer; }
    public SpriteAnimator getAnimator() { return animator; }
}
