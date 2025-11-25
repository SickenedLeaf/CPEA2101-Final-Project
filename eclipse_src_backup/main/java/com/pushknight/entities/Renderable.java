package com.pushknight.entities;

import javafx.scene.canvas.GraphicsContext;

/**
 * Interface for entities that can be rendered.
 * Implemented by all game entities that need visual representation.
 */
public interface Renderable {
    /**
     * Renders the entity to the graphics context.
     * 
     * @param gc The GraphicsContext to render to
     */
    void render(GraphicsContext gc);
}

