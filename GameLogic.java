package application;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * GameLogic (Model): Manages the entire game state and enforces all game rules.
 * This class is UI-agnostic and does not contain any JavaFX code. Includes
 * recursive push logic and enemy health tracking. * UPDATED to use the new
 * Obstacle class hierarchy (Wall=1, WallSpikes=3, Campfire=4).
 */
public class GameLogic {
	// --- Constants & Configuration ---
	public static final int GRID_WIDTH = 20;
	public static final int GRID_HEIGHT = 14;
	public static final int MAX_ENEMY_HP = 3;
	// WALL_DAMAGE is now obsolete, as damage is determined by the Obstacle object.

	// --- Model: Game State ---
	private int playerX = 1;
	private int playerY = 1;

	// entityGrid: 0=Empty, 1=Wall, 2=Enemy, 3=WallSpikes, 4=Campfire
	private int[][] entityGrid = new int[GRID_WIDTH][GRID_HEIGHT];

	// New grid to store the actual Obstacle objects, required to access
	// damage/passability properties
	private Obstacle[][] obstacleGrid = new Obstacle[GRID_WIDTH][GRID_HEIGHT];

	// Tracks current enemy health
	private int[][] enemyHealthGrid = new int[GRID_WIDTH][GRID_HEIGHT];

	private Map<String, GameUpdateEvent> pendingEvents = new HashMap<>();
	private final Random random = new Random();

	public GameLogic() {
		initializeGameGrid();
	}

	/**
	 * Creates and returns a random Obstacle type: Wall, WallSpikes, or Campfire.
	 */
	private Obstacle createRandomObstacle() {
		// 0: Wall, 1: WallSpikes, 2: Campfire
		int type = random.nextInt(3);

		switch (type) {
		case 0:
			return new Wall();
		case 1:
			return new Spikes();
		case 2:
			return new Campfire();
		default:
			// Should not happen, but return a default Wall as a fallback
			return new Wall();
		}
	}

	/**
	 * Helper to place a given obstacle and update both grids.
	 */
	private void placeObstacle(int x, int y, Obstacle obs) {
		if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
			entityGrid[x][y] = obs.getEntityType();
			obstacleGrid[x][y] = obs;
		}
	}

	/**
	 * Initializes the game grid, including border walls and random internal
	 * obstacles.
	 */
	private void initializeGameGrid() {
		// 1. Create border obstacles (randomly generated)
		for (int x = 0; x < GRID_WIDTH; x++) {
			placeObstacle(x, 0, new Wall());
			placeObstacle(x, GRID_HEIGHT - 1, new Wall());
		}
		for (int y = 0; y < GRID_HEIGHT; y++) {
			placeObstacle(0, y, new Wall());
			placeObstacle(GRID_WIDTH - 1, y, new Wall());
		}

		// 2. Setup initial entities for testing recursive push
		entityGrid[2][2] = 2;
		enemyHealthGrid[2][2] = MAX_ENEMY_HP;
		entityGrid[3][2] = 2;
		enemyHealthGrid[3][2] = MAX_ENEMY_HP;
		entityGrid[4][2] = 2;
		enemyHealthGrid[4][2] = MAX_ENEMY_HP;


		entityGrid[8][6] = 2;
		enemyHealthGrid[8][6] = MAX_ENEMY_HP;

		// 3. Random internal obstacle generation
		for (int x = 1; x < GRID_WIDTH - 1; x++) {
			for (int y = 1; y < GRID_HEIGHT - 1; y++) {
				// Only place obstacles if the spot is currently empty (0)
				if (entityGrid[x][y] == 0 && (x < 8 || x > 9) && (y < 7 || y > 8)) {
					// 20% chance to place an obstacle
					if (random.nextInt(1, 100) <= 20) {
						placeObstacle(x, y, createRandomObstacle());
					}
				}
			}
		}
		playerX = 9;
		playerY = 6; // Player starting position
		entityGrid[playerX][playerY] = 0;
	}

	/**
	 * Checks if an entity type is an obstacle (Wall, WallSpikes, Campfire).
	 */
	private boolean isObstacle(int type) {
		return type == 1 || type == 3 || type == 4;
	}

	/**
	 * Main game logic for player movement and interaction.
	 * 
	 * @param dirX The direction of the push (+1, -1, or 0) on the X axis.
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

		} else if (isObstacle(targetType)) {
			// Target is an obstacle: Check passability and damage
			Obstacle obstacle = obstacleGrid[targetX][targetY];

			if (obstacle.isPassable()) {
				// Passable Obstacle (e.g., Campfire): Move player, apply damage
				int damage = obstacle.getPassDamage();
				if (damage > 0) {
					// Future enhancement: Apply damage to playerHealth here
				}
				movePlayer(targetX, targetY);
				actionTaken = true;

			} else {
				// Impassable Obstacle (Wall/WallSpikes): Blocked
				System.out.println(
						"Blocked by " + obstacle.getClass().getSimpleName() + " at (" + targetX + ", " + targetY + ")");
			}

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

				} else if (isObstacle(pushTargetType)) {
					// Pushed into an obstacle: Damage/Defeat enemy and move player
					Obstacle obstacle = obstacleGrid[pushX][pushY];

					if(obstacle.isPassable()) {
						moveEnemy(targetX, targetY, pushX, pushY);
					} else {
						// Apply collision damage to the enemy
						takeDamage(targetX, targetY, obstacle.getCollisionDamage());
					}					
					actionTaken = true;
					// Register impact event for visual feedback
					addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));

				} else if (pushTargetType == 2) {
					if (attemptEntityPush(pushX, pushY, dirX, dirY)) {
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
			return false; // Out of bounds
		}

		int nextType = entityGrid[nextX][nextY];

		if (nextType == 0) {
			// Next spot is empty: Successful push. Move this entity.
			moveEnemy(entityX, entityY, nextX, nextY);
			return true;
		} else if (isObstacle(nextType)) {
			// Next spot is an obstacle: Damage/Defeat entity.
			Obstacle obstacle = obstacleGrid[nextX][nextY];
			// Only damage if it's an impassable obstacle (Walls, WallSpikes)
			if (!obstacle.isPassable()) {
				// Apply collision damage to the current enemy (entityX, entityY)
				takeDamage(entityX, entityY, obstacle.getCollisionDamage());
			} else {
				moveEnemy(entityX, entityY, nextX, nextY);
			}
			// True if the entity was defeated (spot cleared)
			return entityGrid[entityX][entityY] == 0;
		} else if (nextType == 2) {
			// Next spot is another enemy: Recursive push attempt.
			if (attemptEntityPush(nextX, nextY, dirX, dirY)) {
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

		// If the old spot had an obstacle, it stays there (enemies don't delete
		// obstacles)
		// If the new spot has an obstacle (e.g., a passable Campfire), the enemy moves
		// on top
		Obstacle obsAtNew = obstacleGrid[newX][newY];
		if (obsAtNew != null) {
			// If the enemy moves onto a damage-dealing passable obstacle (like Campfire)
			if (obsAtNew.isPassable() && obsAtNew.getPassDamage() > 0) {
				takeDamage(oldX, oldY, obsAtNew.getPassDamage()); // Apply damage before moving
			}
		}

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
		// Note: We MUST NOT clear obstacleGrid[x][y] here, as the enemy might have been
		// on a Campfire!

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

	/**
	 * Returns the integer entity type identifier at (x, y). (0=Empty, 1=Wall,
	 * 2=Enemy, 3=WallSpikes, 4=Campfire)
	 */
	public int getEntityAt(int x, int y) {
		return entityGrid[x][y];
	}

	public int getEnemyHealthAt(int x, int y) {
		return enemyHealthGrid[x][y];
	}

	public int getPlayerX() {
		return playerX;
	}

	public int getPlayerY() {
		return playerY;
	}

	// Inner class to pass structured events to the UI/Controller
	public static class GameUpdateEvent {
		public enum Type {
			PLAYER_MOVE, ENEMY_MOVE, REMOVE_ENTITY, DAMAGE, IMPACT
		}

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