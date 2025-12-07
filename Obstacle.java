package application;

/**
 * Abstract base class for all fixed obstacles in the game grid.
 * Defines common properties like damage and passability.
 */
public abstract class Obstacle {

    // Unique integer identifier used for GamePanel visualization (e.g., 1, 3, 4)
    private final int entityType; 
    
    // Damage dealt when an entity is pushed *into* an impassable obstacle
    private final int collisionDamage; 
    
    // Damage dealt when an entity moves *onto* a passable obstacle
    private final int passDamage; 
    
    // True if entities can move onto this tile (e.g., Campfire), false otherwise (e.g., Wall)
    private final boolean passable;

    public Obstacle(int entityType, int collisionDamage, int passDamage, boolean passable) {
        this.entityType = entityType;
        this.collisionDamage = collisionDamage;
        this.passDamage = passDamage;
        this.passable = passable;
    }

    public int getEntityType() { 
        return entityType; 
    }
    
    public int getCollisionDamage() { 
        return collisionDamage; 
    }
    
    public int getPassDamage() { 
        return passDamage; 
    }
    
    public boolean isPassable() { 
        return passable; 
    }
}
