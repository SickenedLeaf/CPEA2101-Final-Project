package application;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * GameLogic (Model): Manages the entire game state and enforces all game rules.
 * This class is UI-agnostic and does not contain any JavaFX code.
 * Includes recursive push logic and enemy health tracking.
 */
public class GameLogic {

	// --- Constants & Configuration (Updated from ControllerTest3) ---
	public static final int GRID_WIDTH = 20;
	public static final int GRID_HEIGHT = 14;
	public static final int MAX_ENEMY_HP = 3;
	public static final int WALL_DAMAGE = 1; 
	
	// --- Model: Game State ---
	private int playerX = 1;
	private int playerY = 1;

	// EntityGrid: 1=Wall, 2=Enemy, 0=Empty
	private int[][] entityGrid = new int[GRID_WIDTH][GRID_HEIGHT];

	// Tracks current enemy health
	private int[][] enemyHealthGrid = new int[GRID_WIDTH][GRID_HEIGHT];
	
	// Map to track changes for the UI to render (e.g., enemy moved from A to B)
	private Map<String, GameUpdateEvent> pendingEvents = new HashMap<>();


	public GameLogic() {
		initializeGameGrid();
	}

	/**
	 * Initializes the game grid, including border walls and random internal walls.
	 * Logic adapted from the user's ControllerTest3.
	 */
	private void initializeGameGrid() {
		// 1. Create border walls
		for (int x = 0; x < GRID_WIDTH; x++) {
			entityGrid[x][0] = 1; // Wall
			entityGrid[x][GRID_HEIGHT - 1] = 1; // Wall
		}
		for (int y = 0; y < GRID_HEIGHT; y++) {
			entityGrid[0][y] = 1; // Wall
			entityGrid[GRID_WIDTH - 1][y] = 1; // Wall
		}

		// 2. Setup initial entities for testing recursive push (from user's logic)
		entityGrid[2][2] = 2; enemyHealthGrid[2][2] = MAX_ENEMY_HP;
		entityGrid[3][2] = 2; enemyHealthGrid[3][2] = MAX_ENEMY_HP;
		entityGrid[4][2] = 2; enemyHealthGrid[4][2] = MAX_ENEMY_HP;
		entityGrid[5][1] = 1; // Wall to stop the chain

		entityGrid[8][6] = 2; enemyHealthGrid[8][6] = MAX_ENEMY_HP;
		
		// 3. Random internal wall generation (from user's logic)
		Random random = new Random();
		for (int x = 1; x < GRID_WIDTH - 1; x++) {
			for (int y = 1; y < GRID_HEIGHT - 1; y++) {
				// Only place walls if the spot is currently empty (0)
				// The complex condition (x < 8 || x > 9) && (y < 7 || y > 8) is preserved
				if (entityGrid[x][y] == 0 && (x < 8 || x > 9) && (y < 7 || y > 8)) {
					// 20% chance to place a wall
					if (random.nextInt(1, 100) <= 20) {
						entityGrid[x][y] = 1;
					}
				}
			}
		}

		playerX = 9;
		playerY = 6; // Player starting position updated from user's logic
		entityGrid[playerX][playerY] = 0; 
	}

	/**
	 * Main game logic for player movement and interaction.
	 * * @param dirX The direction of the push (+1, -1, or 0) on the X axis.
	 * @param dirY The direction of the push (+1, -1, or 0) on the Y axis.
	 * @return true if an action (move/push) occurred, false otherwise.
	 */
	public boolean attemptMove(int dirX, int dirY) {
		int targetX = playerX + dirX;
		int targetY = playerY + dirY;

		// 1. Check bounds
		if (targetX < 0 || targetX >= GRID_WIDTH || targetY < 0 || targetY >= GRID_HEIGHT) {
			return false;
		}
		
		boolean actionTaken = false;
		int targetType = entityGrid[targetX][targetY];

		if (targetType == 0) {
			// Target is empty: Move player
			movePlayer(targetX, targetY);
			actionTaken = true;

		} else if (targetType == 1) {
			// Target is a wall: Blocked
			System.out.println("Blocked by Wall at (" + targetX + ", " + targetY + ")");

		} else if (targetType == 2) {
			// Target is an enemy: Try to push
			
			int pushX = targetX + dirX;
			int pushY = targetY + dirY;

			if (pushX >= 0 && pushX < GRID_WIDTH && pushY >= 0 && pushY < GRID_HEIGHT) {
				
				int pushTargetType = entityGrid[pushX][pushY];

				if (pushTargetType == 0) {
					// Pushed into empty space: Move enemy and then player
					moveEnemy(targetX, targetY, pushX, pushY);
					actionTaken = true;

				} else if (pushTargetType == 1) {
					// Pushed into a wall: Damage/Defeat enemy and move player
					takeDamage(targetX, targetY, WALL_DAMAGE);
					
					// Register impact event for visual feedback
					addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY)); 

				} else if (pushTargetType == 2) {
					// Pushed into another enemy: Trigger recursive chain check
					
					if (attemptEntityPush(pushX, pushY, dirX, dirY)) {
						// If the chain moved/cleared up, Enemy 1 can now move into (pushX, pushY)
						moveEnemy(targetX, targetY, pushX, pushY);
						actionTaken = true;
					}
				}
			}
		}
		return actionTaken;
	}

	/**
	 * Recursively pushes an entity and subsequent entities.
	 */
	private boolean attemptEntityPush(int entityX, int entityY, int dirX, int dirY) {
	    int nextX = entityX + dirX;
	    int nextY = entityY + dirY;
	    
	    if (nextX < 0 || nextX >= GRID_WIDTH || nextY < 0 || nextY >= GRID_HEIGHT) {
	        return false; 
	    }

	    int nextType = entityGrid[nextX][nextY];

	    if (nextType == 0) {
	        // Next spot is empty: Successful push. Move this entity.
	        moveEnemy(entityX, entityY, nextX, nextY);
	        return true;

	    } else if (nextType == 1) {
	        // Next spot is a wall: Damage/Defeat entity.
	        takeDamage(entityX, entityY, WALL_DAMAGE);
	        addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
	        
	        // True if the entity was defeated (spot cleared)
	        return entityGrid[entityX][entityY] == 0; 
	        
	    } else if (nextType == 2) {
	        // Next spot is another enemy: Recursive push attempt.
	        
	        if (attemptEntityPush(nextX, nextY, dirX, dirY)) {
	            // Chain cleared: Move this entity into the now empty spot (nextX, nextY).
	            moveEnemy(entityX, entityY, nextX, nextY);
	            return true;
	        } else {
	            // Chain blocked.
	            return false;
	        }
	    }
	    
	    return false;
	}


	/**
	 * Applies damage to an enemy at (x, y) and updates the model.
	 */
	private void takeDamage(int x, int y, int damage) {
		int currentHealth = enemyHealthGrid[x][y];
		int newHealth = currentHealth - damage;
		enemyHealthGrid[x][y] = newHealth;

		addEvent(new GameUpdateEvent(GameUpdateEvent.Type.DAMAGE, x, y, newHealth));

		if (newHealth <= 0) {
			removeEntity(x, y);
			System.out.println("Enemy at (" + x + ", " + y + ") defeated!");
		}
	}


	/**
	 * Updates the player's position in the model and registers a move event.
	 */
	private void movePlayer(int newX, int newY) {
		int oldX = playerX;
		int oldY = playerY;
		
		playerX = newX;
		playerY = newY;
		
		addEvent(new GameUpdateEvent(GameUpdateEvent.Type.PLAYER_MOVE, oldX, oldY, newX, newY));
	}

	/**
	 * Moves an enemy from (oldX, oldY) to (newX, newY) and updates the model.
	 */
	private void moveEnemy(int oldX, int oldY, int newX, int newY) {
		int health = enemyHealthGrid[oldX][oldY];
		
		// Clear old spot
		entityGrid[oldX][oldY] = 0; 
		enemyHealthGrid[oldX][oldY] = 0; 
		
		// Set new spot
		entityGrid[newX][newY] = 2; 
		enemyHealthGrid[newX][newY] = health;
		
		addEvent(new GameUpdateEvent(GameUpdateEvent.Type.ENEMY_MOVE, oldX, oldY, newX, newY, health));
	}

	/**
	 * Removes an entity from the game state.
	 */
	private void removeEntity(int x, int y) {
		entityGrid[x][y] = 0; 
		enemyHealthGrid[x][y] = 0; 
		
		addEvent(new GameUpdateEvent(GameUpdateEvent.Type.REMOVE_ENTITY, x, y));
	}
	
	// --- Event Handling for UI Synchronization ---
	
	/**
	 * Adds a game event to the queue for the UI to process.
	 */
	private void addEvent(GameUpdateEvent event) {
		// Use the event type and position as a unique key
		String key = event.type.name() + event.newX + "," + event.newY;
		pendingEvents.put(key, event);
	}
	
	/**
	 * Retrieves and clears the list of pending events.
	 */
	public Map<String, GameUpdateEvent> flushEvents() {
		Map<String, GameUpdateEvent> events = new HashMap<>(pendingEvents);
		pendingEvents.clear();
		return events;
	}

	// --- Getters for UI Access ---
	public int getEntityAt(int x, int y) { return entityGrid[x][y]; }
	public int getEnemyHealthAt(int x, int y) { return enemyHealthGrid[x][y]; }
	public int getPlayerX() { return playerX; }
	public int getPlayerY() { return playerY; }
	
	// Inner class to pass structured events to the UI/Controller
	public static class GameUpdateEvent {
		public enum Type { PLAYER_MOVE, ENEMY_MOVE, REMOVE_ENTITY, DAMAGE, IMPACT }
		
		public final Type type;
		public final int oldX, oldY;
		public final int newX, newY;
		public final int value; // Used for health/damage

		// For Move events
		public GameUpdateEvent(Type type, int oldX, int oldY, int newX, int newY) {
			this.type = type;
			this.oldX = oldX;
			this.oldY = oldY;
			this.newX = newX;
			this.newY = newY;
			this.value = 0;
		}
		
		// For Damage/Remove/Impact events
		public GameUpdateEvent(Type type, int x, int y) {
			this(type, x, y, x, y); // Calls the Move constructor
		}

		// For Damage events
		public GameUpdateEvent(Type type, int x, int y, int health) {
			this.type = type;
			this.oldX = x;
			this.oldY = y;
			this.newX = x;
			this.newY = y;
			this.value = health;
		}
		
		// For ENEMY_MOVE with health
		public GameUpdateEvent(Type type, int oldX, int oldY, int newX, int newY, int health) {
			this.type = type;
			this.oldX = oldX;
			this.oldY = oldY;
			this.newX = newX;
			this.newY = newY;
			this.value = health;
		}
	}
}