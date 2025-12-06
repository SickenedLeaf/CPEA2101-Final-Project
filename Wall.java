package application;

/**
 * Concrete obstacle: Standard Wall.
 * - Impassable.
 * - Deals 1 damage on collision.
 */
public class Wall extends Obstacle {
    public Wall() {
        // entityType=1 (standard wall), collisionDamage=1, passDamage=0, passable=false
        super(1, 1, 0, false);
    }
}