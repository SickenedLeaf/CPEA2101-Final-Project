package application;

/**
 * Simple model class for enemies, used by GameLogic.
 */
public class Enemy {
    private int health;

    public Enemy(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }
}