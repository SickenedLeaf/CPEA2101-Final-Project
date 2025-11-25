package com.pushknight.entities;

/**
 * Interface for entities that need to be updated each frame.
 * Implemented by all game entities that have dynamic behavior.
 */
public interface Updatable {
    /**
     * Updates the entity based on elapsed time.
     * 
     * @param now The current timestamp in nanoseconds
     */
    void update(long now);
}

