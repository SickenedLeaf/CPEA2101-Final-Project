package com.pushknight.entities;

import com.pushknight.utils.Constants;

/**
 * Abstract base class for normal enemies.
 * Extends Enemy and provides common behavior for regular enemy types.
 */
public abstract class NormalEnemy extends Enemy {
    private boolean isObstacle;
    private long lastMoveTime;

    /**
     * Creates a new NormalEnemy with the specified properties.
     *
     * @param x           The x coordinate
     * @param y           The y coordinate
     * @param width       The width of the enemy
     * @param height      The height of the enemy
     * @param health      The health of the enemy
     * @param damage      The damage this enemy inflicts on the player
     * @param speed       The movement speed of this enemy
     */
    public NormalEnemy(double x, double y, double width, double height, int health, int damage, double speed) {
        super(x, y, width, height, health, damage, speed);
        this.isObstacle = false; // By default, enemies are not obstacles
        this.lastMoveTime = System.nanoTime();
    }

    /**
     * Checks if this enemy acts as an obstacle (blocks other enemies).
     *
     * @return true if this enemy is an obstacle, false otherwise
     */
    public boolean isObstacle() {
        return isObstacle;
    }

    /**
     * Sets whether this enemy acts as an obstacle.
     *
     * @param isObstacle true if this enemy should act as an obstacle, false otherwise
     */
    public void setIsObstacle(boolean isObstacle) {
        this.isObstacle = isObstacle;
    }

    /**
     * Updates the enemy state based on elapsed time.
     * This method should be called every frame to handle movement and other updates.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Call parent update method (Enemy)
        super.update(now);

        // Move the enemy toward the target if it exists
        if (getTarget() != null) {
            moveTowardsTarget(now);
        }

        // Update last move time for any time-based logic
        lastMoveTime = now;
    }

    /**
     * Moves this enemy towards its target player.
     * This is a basic implementation that moves in the direction of the player.
     * Subclasses can override this for more sophisticated AI.
     *
     * @param now The current timestamp in nanoseconds
     */
    protected void moveTowardsTarget(long now) {
        if (getTarget() == null) {
            return; // No target to move towards
        }

        // Calculate direction to player
        double targetX = getTarget().getCenterX();
        double targetY = getTarget().getCenterY();
        double currentX = this.getCenterX();
        double currentY = this.getCenterY();

        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        // Normalize the direction vector
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) {
            deltaX /= distance;
            deltaY /= distance;

            // Move the enemy towards the player based on its speed
            double moveDistance = getSpeed(); // This represents the speed per update
            setX(getX() + deltaX * moveDistance);
            setY(getY() + deltaY * moveDistance);

            // Keep the enemy within screen bounds
            setX(Math.max(0, Math.min(Constants.WINDOW_WIDTH - getWidth(), getX())));
            setY(Math.max(0, Math.min(Constants.WINDOW_HEIGHT - getHeight(), getY())));
        }
    }

    /**
     * Gets the time since this enemy last moved.
     *
     * @param now The current timestamp in nanoseconds
     * @return The time since last movement in nanoseconds
     */
    public long getTimeSinceLastMove(long now) {
        return now - lastMoveTime;
    }


    /**
     * Called when the enemy dies.
     * Subclasses should implement specific death behavior.
     * This method should handle the basic death logic like awarding points.
     */
    @Override
    public abstract void onDeath();

    /**
     * Checks if this enemy type is a physical obstacle that blocks other enemies.
     * This is a convenience method that returns the same as isObstacle() but is more descriptive.
     *
     * @return true if this enemy blocks other enemies, false otherwise
     */
    public boolean blocksOtherEnemies() {
        return isObstacle();
    }
}