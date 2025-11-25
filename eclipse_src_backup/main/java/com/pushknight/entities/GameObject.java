package com.pushknight.entities;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for game objects that are not characters.
 * Used for environmental objects like traps, obstacles, and interactive elements.
 * Extends Entity to provide position, size, and rendering capabilities.
 */
public abstract class GameObject extends Entity {
    
    /**
     * Creates a new game object at the specified position with the given dimensions.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param width The width of the game object
     * @param height The height of the game object
     */
    public GameObject(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
    
    /**
     * Updates the game object. Must be implemented by subclasses.
     * 
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public abstract void update(long now);
    
    /**
     * Renders the game object. Must be implemented by subclasses.
     * 
     * @param gc The GraphicsContext to render to
     */
    @Override
    public abstract void render(GraphicsContext gc);
}

