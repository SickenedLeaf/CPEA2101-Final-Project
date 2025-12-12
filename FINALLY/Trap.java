package application;

public class Trap {
    private int x, y;
    private int damage;
    private boolean active;
    private boolean persistent;
    private double cooldown;
    private double cooldownMax;
    
    public Trap(int x, int y, int damage, boolean persistent) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.persistent = persistent;
        this.active = true;
        this.cooldownMax = 1.0;
        this.cooldown = 0;
    }
    
    public void update(double deltaTime) {
        if (!active && persistent) {
            cooldown -= deltaTime;
            if (cooldown <= 0) {
                active = true;
            }
        }
    }
    
    public void trigger() {
        if (!active) return;
        
        active = false;
        
        if (persistent) {
            cooldown = cooldownMax;
        }
        
        System.out.println("[TRAP] Trap triggered at (" + x + "," + y + ")");
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDamage() { return damage; }
    public boolean isActive() { return active; }
    public boolean isPersistent() { return persistent; }
}
