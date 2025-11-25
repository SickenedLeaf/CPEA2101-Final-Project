package com.pushknight.entities;

/**
 * Abstract base class for game objects that are not characters.
 * Examples include traps, obstacles, collectibles, etc.
 */
public abstract class GameObject extends Entity {
    /**
     * Creates a new GameObject at the specified position with the given dimensions.
     *
     * @param x      The x coordinate
     * @param y      The y coordinate
     * @param width  The width of the game object
     * @param height The height of the game object
     */
    public GameObject(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Updates the game object state based on elapsed time.
     * This method should be overridden by subclasses.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Default implementation does nothing
        // Subclasses should override this method
    }
}