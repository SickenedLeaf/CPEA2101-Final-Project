package com.pushknight.utils;

/**
 * Enum representing the current state of the game.
 * Used to control game flow and UI transitions.
 */
public enum GameState {
    /** Main menu screen */
    MENU,
    
    /** Active gameplay */
    PLAYING,
    
    /** Game is paused */
    PAUSED,
    
    /** Game over screen */
    GAME_OVER,
    
    /** Upgrade selection screen between waves */
    UPGRADE_SELECTION
}

