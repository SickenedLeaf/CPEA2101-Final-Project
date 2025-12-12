package application;

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
    
    public enum EnemyType {
        GOBLIN,
        SKELETON,
        BRUTE,
        BOOMER
    }
    
    public Enemy(int x, int y, EnemyType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.dead = false;
        this.attackCooldown = 0;
        this.attackCooldownMax = 1.0; // 1 second between attacks by default
    }
    
    public abstract int[] updateAI(double deltaTime, int playerX, int playerY, 
                                   Pathfinder pathfinder, int[][] grid);
    
    public void updateCooldowns(double deltaTime) {
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
            if (attackCooldown < 0) attackCooldown = 0;
        }
    }
    
    public boolean canAttackPlayer(int playerX, int playerY) {
        if (attackCooldown > 0) return false;
        
        int dx = Math.abs(x - playerX);
        int dy = Math.abs(y - playerY);
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
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
            System.out.println("[COMBAT] " + type + " defeated at (" + x + "," + y + ")");
        }
    }
    
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public boolean isDead() { return dead; }
    public EnemyType getType() { return type; }
    public double getMovementCooldown() { return movementCooldown; }
    public double getAttackCooldown() { return attackCooldown; }
}