package com.pushknight.systems;

import com.pushknight.entities.Collidable;
import com.pushknight.entities.Entity;
import javafx.geometry.Rectangle2D;

/**
 * Utility class for collision detection in the game.
 * Provides static methods for checking collisions between entities using
 * Axis-Aligned Bounding Box (AABB) collision detection.
 */
public class CollisionSystem {
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private CollisionSystem() {
        throw new AssertionError("CollisionSystem should not be instantiated");
    }
    
    /**
     * Checks if two entities are colliding using AABB collision detection.
     * 
     * @param a The first entity
     * @param b The second entity
     * @return true if the entities are colliding, false otherwise
     */
    public static boolean checkCollision(Entity a, Entity b) {
        if (a == null || b == null) {
            return false;
        }
        
        Rectangle2D boundsA = a.getBounds();
        Rectangle2D boundsB = b.getBounds();
        
        return boundsA.intersects(boundsB);
    }
    
    /**
     * Checks if two collidable objects are colliding.
     * 
     * @param a The first collidable object
     * @param b The second collidable object
     * @return true if the objects are colliding, false otherwise
     */
    public static boolean checkCollision(Collidable a, Collidable b) {
        if (a == null || b == null) {
            return false;
        }
        
        Rectangle2D boundsA = a.getBounds();
        Rectangle2D boundsB = b.getBounds();
        
        return boundsA.intersects(boundsB);
    }
    
    /**
     * Checks if an entity is within the game bounds.
     * 
     * @param entity The entity to check
     * @param worldWidth The width of the game world
     * @param worldHeight The height of the game world
     * @return true if the entity is within bounds, false otherwise
     */
    public static boolean isWithinBounds(Entity entity, double worldWidth, double worldHeight) {
        if (entity == null) {
            return false;
        }
        
        double x = entity.getX();
        double y = entity.getY();
        double width = entity.getWidth();
        double height = entity.getHeight();
        
        return x >= 0 && y >= 0 && (x + width) <= worldWidth && (y + height) <= worldHeight;
    }
    
    /**
     * Clamps an entity's position to keep it within game bounds.
     * 
     * @param entity The entity to clamp
     * @param worldWidth The width of the game world
     * @param worldHeight The height of the game world
     */
    public static void clampToBounds(Entity entity, double worldWidth, double worldHeight) {
        if (entity == null) {
            return;
        }
        
        double x = entity.getX();
        double y = entity.getY();
        double width = entity.getWidth();
        double height = entity.getHeight();
        
        // Clamp X position
        if (x < 0) {
            entity.setX(0);
        } else if (x + width > worldWidth) {
            entity.setX(worldWidth - width);
        }
        
        // Clamp Y position
        if (y < 0) {
            entity.setY(0);
        } else if (y + height > worldHeight) {
            entity.setY(worldHeight - height);
        }
    }
    
    /**
     * Calculates the distance between two entities' centers.
     * 
     * @param a The first entity
     * @param b The second entity
     * @return The distance between the centers of the two entities
     */
    public static double getDistance(Entity a, Entity b) {
        if (a == null || b == null) {
            return Double.MAX_VALUE;
        }
        
        double dx = a.getCenterX() - b.getCenterX();
        double dy = a.getCenterY() - b.getCenterY();
        
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Checks if an entity is within a certain radius of another entity.
     * 
     * @param a The first entity (center)
     * @param b The second entity
     * @param radius The radius to check
     * @return true if entity b is within radius of entity a, false otherwise
     */
    public static boolean isWithinRadius(Entity a, Entity b, double radius) {
        return getDistance(a, b) <= radius;
    }
}

