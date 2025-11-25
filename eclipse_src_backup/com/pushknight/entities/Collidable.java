package com.pushknight.entities;

import javafx.geometry.Rectangle2D;

/**
 * Interface for entities that can participate in collision detection.
 * Provides methods for checking collisions with other collidable entities.
 */
public interface Collidable {
    /**
     * Checks if this entity collides with another collidable entity.
     * 
     * @param other The other collidable entity
     * @return true if the entities are colliding, false otherwise
     */
    boolean collidesWith(Collidable other);
    
    /**
     * Gets the bounding box of this entity for collision detection.
     * 
     * @return A Rectangle2D representing the entity's bounds
     */
    Rectangle2D getBounds();
}

