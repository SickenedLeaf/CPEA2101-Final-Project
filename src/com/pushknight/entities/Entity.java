package com.pushknight.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for all game entities.
 * Provides common functionality for position, size, and rendering.
 * All game objects (player, enemies, traps) extend this class.
 */
public abstract class Entity implements Updatable, Renderable, Collidable {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    
    /**
     * Creates a new entity at the specified position with the given dimensions.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param width The width of the entity
     * @param height The height of the entity
     */
    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Gets the x coordinate.
     * 
     * @return The x coordinate
     */
    public double getX() {
        return x;
    }
    
    /**
     * Gets the y coordinate.
     * 
     * @return The y coordinate
     */
    public double getY() {
        return y;
    }
    
    /**
     * Sets the x coordinate.
     * 
     * @param x The new x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Sets the y coordinate.
     * 
     * @param y The new y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Gets the width.
     * 
     * @return The width
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Gets the height.
     * 
     * @return The height
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * Sets the width.
     * 
     * @param width The new width
     */
    public void setWidth(double width) {
        this.width = width;
    }
    
    /**
     * Sets the height.
     * 
     * @param height The new height
     */
    public void setHeight(double height) {
        this.height = height;
    }
    
    /**
     * Gets the center x coordinate.
     * 
     * @return The center x coordinate
     */
    public double getCenterX() {
        return x + width / 2.0;
    }
    
    /**
     * Gets the center y coordinate.
     * 
     * @return The center y coordinate
     */
    public double getCenterY() {
        return y + height / 2.0;
    }
    
    /**
     * Gets the bounding box of this entity for collision detection.
     * 
     * @return A Rectangle2D representing the entity's bounds
     */
    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }
    
    /**
     * Checks if this entity collides with another collidable entity.
     * Uses axis-aligned bounding box (AABB) collision detection.
     * 
     * @param other The other collidable entity
     * @return true if the entities are colliding, false otherwise
     */
    @Override
    public boolean collidesWith(Collidable other) {
        if (other == null) {
            return false;
        }
        Rectangle2D thisBounds = this.getBounds();
        Rectangle2D otherBounds = other.getBounds();
        return thisBounds.intersects(otherBounds);
    }
    
    /**
     * Updates the entity. Must be implemented by subclasses.
     * 
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public abstract void update(long now);
    
    /**
     * Renders the entity. Must be implemented by subclasses.
     * 
     * @param gc The GraphicsContext to render to
     */
    @Override
    public abstract void render(GraphicsContext gc);
}

