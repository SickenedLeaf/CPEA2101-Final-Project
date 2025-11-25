package com.pushknight.entities;

import com.pushknight.utils.Constants;

/**
 * A heavy skeleton brute enemy.
 * Tanky enemy with high health but slower speed. Acts as an obstacle.
 */
public class SkeletonBrute extends NormalEnemy {

    /**
     * Creates a new SkeletonBrute at the specified position.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public SkeletonBrute(double x, double y) {
        super(x, y, 
              Constants.SKELETON_BRUTE_WIDTH, 
              Constants.SKELETON_BRUTE_HEIGHT,
              Constants.SKELETON_BRUTE_HEALTH,
              Constants.SKELETON_BRUTE_DAMAGE,
              Constants.SKELETON_BRUTE_SPEED);
        
        // Set this enemy as an obstacle that blocks other enemies
        setIsObstacle(true);
    }

    /**
     * Called when the skeleton brute dies.
     * Awards points to the player and removes from the game.
     */
    @Override
    public void onDeath() {
        // Award points to the player
        if (getTarget() != null) {
            getTarget().addScore(Constants.SCORE_SKELETON_BRUTE);
        }
    }

    /**
     * Gets the color used to render this enemy.
     *
     * @return The color for this enemy type (dark gray for brute)
     */
    @Override
    protected javafx.scene.paint.Color getEnemyColor() {
        return javafx.scene.paint.Color.DARKGRAY;
    }

    /**
     * Updates the skeleton brute's state based on elapsed time.
     * Includes slower movement behavior.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Call the parent update method for base functionality
        super.update(now);

        // Additional skeleton brute-specific behavior could be added here
        // For now, we're using the default movement behavior from NormalEnemy
    }
}