package com.pushknight.entities;

import com.pushknight.utils.Constants;
import com.pushknight.utils.Direction;

/**
 * Component that handles movement with cooldowns for entities.
 * Used by Player and potentially other movable entities.
 */
public class MovementComponent {
    private double speed;
    private long cooldownTime;
    private long lastMoveTime;

    /**
     * Creates a new MovementComponent with default values.
     */
    public MovementComponent() {
        this.speed = Constants.PLAYER_SPEED;
        this.cooldownTime = Constants.PLAYER_MOVEMENT_COOLDOWN;
        this.lastMoveTime = System.nanoTime();
    }

    /**
     * Creates a new MovementComponent with the specified speed and cooldown.
     *
     * @param speed        The movement speed in pixels per second
     * @param cooldownTime The cooldown time in nanoseconds
     */
    public MovementComponent(double speed, long cooldownTime) {
        this.speed = speed;
        this.cooldownTime = cooldownTime;
        this.lastMoveTime = System.nanoTime();
    }

    /**
     * Checks if the entity can move based on the cooldown.
     *
     * @param now The current timestamp in nanoseconds
     * @return true if the entity can move, false otherwise
     */
    public boolean canMove(long now) {
        return (now - lastMoveTime) >= cooldownTime;
    }

    /**
     * Moves the specified entity in the given direction if possible.
     *
     * @param entity The entity to move
     * @param dir    The direction to move
     * @param now    The current timestamp in nanoseconds
     * @return true if the move was successful, false otherwise
     */
    public boolean move(Entity entity, Direction dir, long now) {
        if (!canMove(now)) {
            return false;
        }

        double moveX = 0;
        double moveY = 0;

        switch (dir) {
            case UP:
                moveY = -speed / Constants.TARGET_FPS; // Move up (negative Y)
                break;
            case DOWN:
                moveY = speed / Constants.TARGET_FPS; // Move down (positive Y)
                break;
            case LEFT:
                moveX = -speed / Constants.TARGET_FPS; // Move left (negative X)
                break;
            case RIGHT:
                moveX = speed / Constants.TARGET_FPS; // Move right (positive X)
                break;
            case NONE:
            default:
                return false; // No movement
        }

        // Update entity position
        entity.setX(entity.getX() + moveX);
        entity.setY(entity.getY() + moveY);

        // Enforce boundaries to keep entity within screen
        enforceBoundaries(entity);

        // Update last move time
        lastMoveTime = now;

        return true;
    }

    /**
     * Moves the specified entity by the given amount if possible.
     *
     * @param entity The entity to move
     * @param deltaX The amount to move in X direction
     * @param deltaY The amount to move in Y direction
     * @param now    The current timestamp in nanoseconds
     * @return true if the move was successful, false otherwise
     */
    public boolean moveBy(Entity entity, double deltaX, double deltaY, long now) {
        if (!canMove(now)) {
            return false;
        }

        // Update entity position
        entity.setX(entity.getX() + deltaX);
        entity.setY(entity.getY() + deltaY);

        // Enforce boundaries to keep entity within screen
        enforceBoundaries(entity);

        // Update last move time
        lastMoveTime = now;

        return true;
    }

    /**
     * Enforces screen boundaries for the given entity.
     *
     * @param entity The entity to enforce boundaries for
     */
    private void enforceBoundaries(Entity entity) {
        // Keep entity within screen bounds
        entity.setX(Math.max(0, Math.min(Constants.WINDOW_WIDTH - entity.getWidth(), entity.getX())));
        entity.setY(Math.max(0, Math.min(Constants.WINDOW_HEIGHT - entity.getHeight(), entity.getY())));
    }

    /**
     * Gets the current movement speed.
     *
     * @return The movement speed in pixels per second
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the movement speed.
     *
     * @param speed The new movement speed in pixels per second
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Gets the cooldown time.
     *
     * @return The cooldown time in nanoseconds
     */
    public long getCooldownTime() {
        return cooldownTime;
    }

    /**
     * Sets the cooldown time.
     *
     * @param cooldownTime The new cooldown time in nanoseconds
     */
    public void setCooldownTime(long cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    /**
     * Resets the movement cooldown, allowing the entity to move immediately.
     */
    public void resetCooldown() {
        lastMoveTime = System.nanoTime() - cooldownTime; // Set time far enough back to allow immediate movement
    }

    /**
     * Gets the time since last movement in nanoseconds.
     *
     * @param now The current timestamp in nanoseconds
     * @return The time since last movement
     */
    public long getTimeSinceLastMove(long now) {
        return now - lastMoveTime;
    }
}