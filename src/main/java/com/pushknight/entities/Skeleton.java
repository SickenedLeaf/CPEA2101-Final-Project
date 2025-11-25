package com.pushknight.entities;

import com.pushknight.utils.Constants;

/**
 * A basic skeleton enemy.
 * Standard enemy with moderate health and speed.
 */
public class Skeleton extends NormalEnemy {

    /**
     * Creates a new Skeleton at the specified position.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Skeleton(double x, double y) {
        super(x, y, 
              Constants.SKELETON_WIDTH, 
              Constants.SKELETON_HEIGHT,
              Constants.SKELETON_HEALTH,
              Constants.SKELETON_DAMAGE,
              Constants.SKELETON_SPEED);
    }

    /**
     * Called when the skeleton dies.
     * Awards points to the player and removes from the game.
     */
    @Override
    public void onDeath() {
        // Award points to the player
        if (getTarget() != null) {
            getTarget().addScore(Constants.SCORE_SKELETON);
        }
    }

    /**
     * Gets the color used to render this enemy.
     *
     * @return The color for this enemy type (light gray for skeleton)
     */
    @Override
    protected javafx.scene.paint.Color getEnemyColor() {
        return javafx.scene.paint.Color.LIGHTGRAY;
    }

    /**
     * Updates the skeleton's state based on elapsed time.
     * Includes basic AI behavior for moving toward the player.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Call the parent update method for base functionality
        super.update(now);

        // Additional skeleton-specific behavior could be added here
        // For now, we're using the default movement behavior from NormalEnemy
    }
}