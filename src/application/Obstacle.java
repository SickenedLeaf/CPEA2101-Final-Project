package application;

public abstract class Obstacle {
    private final int entityType;
    private final int collisionDamage;
    private final int passDamage;
    private final boolean passable;

    public Obstacle(int entityType, int collisionDamage, int passDamage, boolean passable) {
        this.entityType = entityType;
        this.collisionDamage = collisionDamage;
        this.passDamage = passDamage;
        this.passable = passable;
    }

    public int getEntityType() { return entityType; }
    public int getCollisionDamage() { return collisionDamage; }
    public int getPassDamage() { return passDamage; }
    public boolean isPassable() { return passable; }
}