package com.pushknight.entities;

import com.pushknight.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A special boomer goblin enemy.
 * Explodes when it dies, dealing area damage to nearby entities.
 */
public class BoomerGoblin extends NormalEnemy {
    private boolean isExploding;
    private long explosionStartTime;
    private static final long EXPLOSION_DURATION = 500_000_000L; // 500ms in nanoseconds

    /**
     * Creates a new BoomerGoblin at the specified position.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public BoomerGoblin(double x, double y) {
        super(x, y, 
              Constants.BOOMER_GOBLIN_WIDTH, 
              Constants.BOOMER_GOBLIN_HEIGHT,
              Constants.BOOMER_GOBLIN_HEALTH,
              Constants.BOOMER_GOBLIN_DAMAGE,
              Constants.BOOMER_GOBLIN_SPEED);
        
        this.isExploding = false;
        this.explosionStartTime = 0;
    }

    /**
     * Called when the boomer goblin dies.
     * Initiates the explosion sequence that damages nearby entities.
     */
    @Override
    public void onDeath() {
        // Begin explosion sequence
        isExploding = true;
        explosionStartTime = System.nanoTime();
        
        // Award points to the player for defeating the boomer goblin
        if (getTarget() != null) {
            getTarget().addScore(Constants.SCORE_BOOMER_GOBLIN);
        }
    }

    /**
     * Gets the color used to render this enemy.
     *
     * @return The color for this enemy type (orange-red for boomer goblin)
     */
    @Override
    protected Color getEnemyColor() {
        if (isExploding) {
            // Flash red when exploding
            double timeSinceExplosion = System.nanoTime() - explosionStartTime;
            boolean shouldFlash = ((int)(timeSinceExplosion / 100_000_000L)) % 2 == 0; // Flash every 100ms
            return shouldFlash ? Color.RED : Color.ORANGERED;
        }
        return Color.ORANGERED;
    }

    /**
     * Renders the boomer goblin, with special effects when exploding.
     *
     * @param gc The GraphicsContext to render to
     */
    @Override
    public void render(GraphicsContext gc) {
        if (isExploding) {
            // Draw explosion effect
            double explosionRadius = getCurrentExplosionRadius();
            gc.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.3)); // Semi-transparent
            gc.fillOval(getCenterX() - explosionRadius, getCenterY() - explosionRadius, 
                       explosionRadius * 2, explosionRadius * 2);
            
            // Draw the enemy itself
            gc.setFill(getEnemyColor());
            gc.fillRect(getX(), getY(), getWidth(), getHeight());
            
            // Draw a border
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeRect(getX(), getY(), getWidth(), getHeight());
        } else {
            // Normal rendering
            super.render(gc);
        }
    }

    /**
     * Updates the boomer goblin's state based on elapsed time.
     * Handles the explosion sequence if the enemy is exploding.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Call the parent update method for base functionality
        super.update(now);

        if (isExploding) {
            // Check if explosion is complete
            if (now - explosionStartTime >= EXPLOSION_DURATION) {
                // Explosion is over, remove this enemy from the game
                // The game controller should handle removal
                isExploding = false;
            }
        }
    }

    /**
     * Gets the current radius of the explosion based on time elapsed.
     *
     * @return The current explosion radius
     */
    private double getCurrentExplosionRadius() {
        if (!isExploding) {
            return 0;
        }
        
        long timeElapsed = System.nanoTime() - explosionStartTime;
        double progress = Math.min(1.0, (double) timeElapsed / EXPLOSION_DURATION);
        
        // Scale explosion from 0 to full radius
        return Constants.BOOMER_GOBLIN_EXPLOSION_RADIUS * progress;
    }

    /**
     * Checks if this boomer goblin is currently in the explosion state.
     *
     * @return true if exploding, false otherwise
     */
    public boolean isExploding() {
        return isExploding;
    }

    /**
     * Checks if the explosion sequence is complete.
     *
     * @param now The current timestamp in nanoseconds
     * @return true if explosion is complete, false otherwise
     */
    public boolean isExplosionComplete(long now) {
        return isExploding && (now - explosionStartTime >= EXPLOSION_DURATION);
    }

    /**
     * Checks if another entity is within the explosion radius.
     *
     * @param entity The entity to check
     * @return true if the entity is within explosion range, false otherwise
     */
    public boolean isEntityInExplosionRadius(Entity entity) {
        if (!isExploding) {
            return false;
        }
        
        double distance = Math.sqrt(
            Math.pow(entity.getCenterX() - getCenterX(), 2) +
            Math.pow(entity.getCenterY() - getCenterY(), 2)
        );
        
        return distance <= getCurrentExplosionRadius();
    }

    /**
     * Gets the maximum explosion radius.
     *
     * @return The maximum explosion radius
     */
    public double getMaxExplosionRadius() {
        return Constants.BOOMER_GOBLIN_EXPLOSION_RADIUS;
    }
}