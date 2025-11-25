package com.pushknight.systems;

import com.pushknight.entities.*;

import java.util.List;

/**
 * System responsible for handling collision detection and response between entities.
 */
public class CollisionSystem {

    /**
     * Checks for collisions between all entities in the provided list and handles them appropriately.
     *
     * @param entities The list of entities to check for collisions
     */
    public void checkCollisions(List<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entityA = entities.get(i);
            
            for (int j = i + 1; j < entities.size(); j++) {
                Entity entityB = entities.get(j);
                
                if (entityA.collidesWith(entityB)) {
                    handleCollision(entityA, entityB);
                }
            }
        }
    }

    /**
     * Checks for collisions between a specific entity and all others in the list.
     *
     * @param entity   The entity to check collisions for
     * @param entities The list of entities to check against
     */
    public void checkCollisions(Entity entity, List<Entity> entities) {
        for (Entity otherEntity : entities) {
            if (entity != otherEntity && entity.collidesWith(otherEntity)) {
                handleCollision(entity, otherEntity);
            }
        }
    }

    /**
     * Handles collision response between two colliding entities based on their types.
     *
     * @param entityA The first entity in the collision
     * @param entityB The second entity in the collision
     */
    private void handleCollision(Entity entityA, Entity entityB) {
        // Player vs Enemy collision
        if (entityA instanceof Player && entityB instanceof Enemy) {
            handlePlayerEnemyCollision((Player) entityA, (Enemy) entityB);
        } else if (entityA instanceof Enemy && entityB instanceof Player) {
            handlePlayerEnemyCollision((Player) entityB, (Enemy) entityA);
        }
        // Enemy vs Trap collision
        else if (entityA instanceof Enemy && entityB instanceof Trap) {
            handleEnemyTrapCollision((Enemy) entityA, (Trap) entityB);
        } else if (entityA instanceof Trap && entityB instanceof Enemy) {
            handleEnemyTrapCollision((Enemy) entityB, (Trap) entityA);
        }
        // Player vs Trap collision (if applicable)
        else if (entityA instanceof Player && entityB instanceof Trap) {
            handlePlayerTrapCollision((Player) entityA, (Trap) entityB);
        } else if (entityA instanceof Trap && entityB instanceof Player) {
            handlePlayerTrapCollision((Player) entityB, (Trap) entityA);
        }
        // Other collision types can be added as needed
    }

    /**
     * Handles collision between a player and an enemy.
     * Typically results in the player taking damage.
     *
     * @param player The player entity
     * @param enemy  The enemy entity
     */
    private void handlePlayerEnemyCollision(Player player, Enemy enemy) {
        // Apply damage to the player
        // This will be handled by the enemy's attack method which tracks cooldowns
        enemy.attack(player, System.nanoTime());
    }

    /**
     * Handles collision between an enemy and a trap.
     * Typically results in the enemy taking damage from the trap.
     *
     * @param enemy The enemy entity
     * @param trap  The trap entity
     */
    private void handleEnemyTrapCollision(Enemy enemy, Trap trap) {
        // Trigger the trap to damage the enemy
        // The trap will handle its own cooldown and state
        trap.trigger(enemy, System.nanoTime());
    }

    /**
     * Handles collision between a player and a trap.
     * This might be used for special interaction traps.
     *
     * @param player The player entity
     * @param trap   The trap entity
     */
    private void handlePlayerTrapCollision(Player player, Trap trap) {
        // For now, player doesn't interact with traps directly
        // This can be expanded later to include beneficial traps for players
    }

    /**
     * Checks if two entities are colliding.
     *
     * @param entityA The first entity
     * @param entityB The second entity
     * @return true if the entities are colliding, false otherwise
     */
    public boolean areColliding(Entity entityA, Entity entityB) {
        if (entityA instanceof Collidable && entityB instanceof Collidable) {
            return entityA.collidesWith(entityB);
        }
        return false;
    }

    /**
     * Checks if an entity is colliding with any other entity in the list.
     *
     * @param entity   The entity to check
     * @param entities The list of entities to check against
     * @return true if colliding with any entity in the list, false otherwise
     */
    public boolean isCollidingWithAny(Entity entity, List<Entity> entities) {
        for (Entity otherEntity : entities) {
            if (entity != otherEntity && areColliding(entity, otherEntity)) {
                return true;
            }
        }
        return false;
    }
}