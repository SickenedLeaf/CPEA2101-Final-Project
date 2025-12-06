package application;

/**
 * Concrete obstacle: Campfire.
 * - Passable.
 * - Deals 1 damage when an entity moves onto it.
 */
public class Campfire extends Obstacle {
    public Campfire() {
        // entityType=4 (new type for visualization), collisionDamage=0, passDamage=1, passable=true
        super(4, 0, 1, true);
    }
}