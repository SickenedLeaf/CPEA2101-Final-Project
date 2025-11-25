package com.pushknight.utils;

/**
 * Enum representing different enemy types in the game.
 * Used for enemy creation and identification.
 */
public enum EnemyType {
    /** Basic enemy - moderate health and speed */
    SKELETON,
    
    /** Fast enemy - lower health but higher damage */
    GOBLIN,
    
    /** Tank enemy - high health, slow speed, acts as obstacle */
    SKELETON_BRUTE,
    
    /** Special enemy - explodes on death dealing area damage */
    BOOMER_GOBLIN
}

