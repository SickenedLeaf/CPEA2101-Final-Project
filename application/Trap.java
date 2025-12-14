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
        // No update needed - traps disappear after triggering
    }

    public void trigger() {
        if (!active) return;

        triggered = true;
        active = false;  // Trap becomes inactive when triggered and disappears

        System.out.println("[TRAP] Bear Trap triggered and disappeared at (" + x + "," + y + ")");
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getDamage() { return damage; }
    public boolean isActive() { return active; }
    public boolean isTriggered() { return triggered; }
}