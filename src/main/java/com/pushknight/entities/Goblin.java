package com.pushknight.entities;

import com.pushknight.utils.Constants;

/**
 * A fast goblin enemy.
 * Faster than skeleton but with less health.
 */
public class Goblin extends NormalEnemy {

    /**
     * Creates a new Goblin at the specified position.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Goblin(double x, double y) {
        super(x, y, 
              Constants.GOBLIN_WIDTH, 
              Constants.GOBLIN_HEIGHT,
              Constants.GOBLIN_HEALTH,
              Constants.GOBLIN_DAMAGE,
              Constants.GOBLIN_SPEED);
    }

    /**
     * Called when the goblin dies.
     * Awards points to the player and removes from the game.
     */
    @Override
    public void onDeath() {
        // Award points to the player
        if (getTarget() != null) {
            getTarget().addScore(Constants.SCORE_GOBLIN);
        }
    }

    /**
     * Gets the color used to render this enemy.
     *
     * @return The color for this enemy type (green for goblin)
     */
    @Override
    protected javafx.scene.paint.Color getEnemyColor() {
        return javafx.scene.paint.Color.GREEN;
    }

    /**
     * Updates the goblin's state based on elapsed time.
     * Includes aggressive behavior for moving toward the player.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Call the parent update method for base functionality
        super.update(now);

        // Additional goblin-specific behavior could be added here
        // For now, we're using the default movement behavior from NormalEnemy
    }
}