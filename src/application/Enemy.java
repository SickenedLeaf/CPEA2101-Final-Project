package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Enemy {
    protected int x, y;
    protected int hp;
    protected int maxHp;
    protected int damage;
    protected boolean dead;
    protected double movementCooldown;
    protected double movementCooldownMax;
    protected EnemyType type;

    // Attack cooldown system
    protected double attackCooldown;
    protected double attackCooldownMax;

    // Animator
    protected SpriteAnimator animator;

    // Direction (e.g., "UP", "DOWN", "LEFT", "RIGHT")
    protected String direction;

    public enum EnemyType {
        GOBLIN,
        SKELETON,
        BRUTE,
        BOOMER
    }

    public Enemy(int x, int y, EnemyType type, Image spriteSheet) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.dead = false;
        this.attackCooldownMax = 1.0; // 1 second between attacks by default
        this.attackCooldown = attackCooldownMax;
        
        // Initialize animator with enemy sprite sheet
        this.animator = new SpriteAnimator(spriteSheet, 72, 72);

        // Default facing direction
        this.direction = "RIGHT";
    }

    // --- AI ---
    public abstract int[] updateAI(double deltaTime, int playerX, int playerY,
                                   Pathfinder pathfinder, int[][] grid);

    public void updateCooldowns(double deltaTime, int playerX, int playerY) {
        int dx = Math.abs(x - playerX);
        int dy = Math.abs(y - playerY);
        boolean adjacent = (dx == 1 && dy == 0) || (dx == 0 && dy == 1);

        if (adjacent) {
            // Tick down cooldown while player is in range
            if (attackCooldown > 0) {
                attackCooldown -= deltaTime;
                if (attackCooldown < 0) attackCooldown = 0;
            }
        } else {
            // Reset cooldown if player leaves range
            attackCooldown = attackCooldownMax/2;
        }
    }

    public boolean canAttackPlayer(int playerX, int playerY) {
        int dx = Math.abs(x - playerX);
        int dy = Math.abs(y - playerY);
        boolean adjacent = (dx == 1 && dy == 0) || (dx == 0 && dy == 1);

        // Attack only if adjacent AND cooldown expired
        return adjacent && attackCooldown <= 0;
    }

    public void tryAttackPlayer(int playerX, int playerY) {
        if (canAttackPlayer(playerX, playerY)) {
            activateAttackCooldown(); // reset after attack
            playAttackAnimation();
            System.out.println("[COMBAT] " + type + " attacks player at (" + x + "," + y + ")");
        }
    }


    public void activateAttackCooldown() {
        attackCooldown = attackCooldownMax;
    }

    public void takeDamage(int damage) {
        if (dead) return;

        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            dead = true;
            playDeathAnimation();
            System.out.println("[COMBAT] " + type + " defeated at (" + x + "," + y + ")");
        }
    }

    public void moveTo(int newX, int newY, String direction) {
        this.x = newX;
        this.y = newY;
        this.direction = direction; // update facing direction
        playMoveAnimation();
    }

    // --- Abstract Animation Hooks ---
    public abstract void playIdleAnimation();
    public abstract void playMoveAnimation();
    public abstract void playAttackAnimation();
    public abstract void playDeathAnimation();

    // --- Accessors ---
    public ImageView getImageView() { return animator.getImageView(); }
    public SpriteAnimator getAnimator() { return animator; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public boolean isDead() { return dead; }
    public EnemyType getType() { return type; }
    public double getMovementCooldown() { return movementCooldown; }
    public double getAttackCooldown() { return attackCooldown; }
    public String getDirection() { return direction; }
    public void setDirection(String dir) { this.direction = dir; }
}
