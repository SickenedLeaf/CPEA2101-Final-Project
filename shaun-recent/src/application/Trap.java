package application;

public class Trap {
    private int x, y;
    private int damage;
    private boolean active;
    private boolean triggered;

    public Trap(int x, int y) {
        this.x = x;
        this.y = y;
        this.damage = 1;
        this.active = true;
        this.triggered = false;
    }

    public void update(double deltaTime) {
        // Bear traps are one-time use
    }

    public void trigger() {
        if (!active || triggered) return;

        triggered = true;
        active = false;

        System.out.println("[TRAP] Bear Trap triggered at (" + x + "," + y + ")");
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getDamage() { return damage; }
    public boolean isActive() { return active; }
    public boolean isTriggered() { return triggered; }
}