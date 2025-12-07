package application;

import java.util.HashMap;
import java.util.List;
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
	public static final int GRID_WIDTH = 14;
	public static final int GRID_HEIGHT = 10;
	public static final int MAX_ENEMY_HP = 3;
	// WALL_DAMAGE is now obsolete, as damage is determined by the Obstacle object.

	// --- Model: Game State ---
	private int playerX = 1;
	private int playerY = 1;

	// entityGrid: 0=Empty, 1=Wall, 2=Enemy, 3=WallSpikes, 4=Campfire
	private int[][] entityGrid = new int[GRID_WIDTH][GRID_HEIGHT];
	private Enemy[][] enemyGrid = new Enemy[GRID_WIDTH][GRID_HEIGHT];

	// New grid to store the actual Obstacle objects, required to access
	// damage/passability properties
	private Obstacle[][] obstacleGrid = new Obstacle[GRID_WIDTH][GRID_HEIGHT];

	private String direction = "RIGHT";

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
		int type = random.nextInt(10);

		if (type >= 4) {
			return new Wall();
		} else if (type >= 2) {
			return new Spikes();
		} else {
			return new Campfire();
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
		enemyGrid[2][2] = new Enemy(MAX_ENEMY_HP);
		entityGrid[2][2] = 2;

		enemyGrid[3][2] = new Enemy(MAX_ENEMY_HP);
		entityGrid[3][2] = 2;
		enemyGrid[4][2] = new Enemy(MAX_ENEMY_HP);
		entityGrid[4][2] = 2;

		enemyGrid[8][6] = new Enemy(MAX_ENEMY_HP);
		entityGrid[8][6] = 2;

		// 3. Random internal obstacle generation
		for (int x = 1; x < GRID_WIDTH - 1; x++) {
			for (int y = 1; y < GRID_HEIGHT - 1; y++) {
				// Only place obstacles if the spot is currently empty (0)
				if (entityGrid[x][y] == 0 && (x < 5 || x > 6) && (y < 5 || y > 6)) {
					// 20% chance to place an obstacle
					if (random.nextInt(1, 100) <= 25) {
						placeObstacle(x, y, createRandomObstacle());
					}
				}
			}
		}
		playerX = 5;
		playerY = 5; // Player starting position
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
		// Update facing direction immediately
	    if (dirX > 0) direction = "RIGHT";
	    else if (dirX < 0) direction = "LEFT";
	    else if (dirY > 0) direction = "DOWN";
	    else if (dirY < 0) direction = "UP";
		
		int targetX = playerX + dirX;
		int targetY = playerY + dirY;
		    

		if (targetX < 0 || targetX >= GRID_WIDTH || targetY < 0 || targetY >= GRID_HEIGHT) {
			return false;
		}

		int targetType = entityGrid[targetX][targetY];
		boolean actionTaken = false;

		if (targetType == 0) {
			movePlayer(targetX, targetY);
			actionTaken = true;

		} else if (isObstacle(targetType)) {
			Obstacle obstacle = obstacleGrid[targetX][targetY];
			if (obstacle.isPassable()) {
				movePlayer(targetX, targetY);
				actionTaken = true;
			}

		} else if (targetType == 2) {
			int pushX = targetX + dirX;
			int pushY = targetY + dirY;

			if (pushX >= 0 && pushX < GRID_WIDTH && pushY >= 0 && pushY < GRID_HEIGHT) {
				int pushTargetType = entityGrid[pushX][pushY];

				if (pushTargetType == 0) {
					moveEnemy(targetX, targetY, pushX, pushY);
					actionTaken = true;

				} else if (isObstacle(pushTargetType)) {
					Obstacle obstacle = obstacleGrid[pushX][pushY];

					if (obstacle.isPassable()) {
						moveEnemy(targetX, targetY, pushX, pushY);
						actionTaken = true;
					} else {
						// Impassable wall/spikes collision
						addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));

						// Apply damage once
						takeDamage(targetX, targetY, obstacle.getCollisionDamage());

						// Stop here — don’t re‑apply damage in subsequent checks
						actionTaken = true;
					}
				} else if (pushTargetType == 2) {
					attemptEntityPush(pushX, pushY, dirX, dirY);
					actionTaken = true;
				}
			}
		}

		return actionTaken;
	}

	/**
	 * Recursively pushes an entity and subsequent entities.
	 */
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
			moveEnemy(entityX, entityY, nextX, nextY);
			return true;

		} else if (isObstacle(nextType)) {
			Obstacle obstacle = obstacleGrid[nextX][nextY];

			if (obstacle.isPassable()) {
				moveEnemy(entityX, entityY, nextX, nextY);
				return true;
			} else {
				addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
				// Then apply collision damage to the new position
				takeDamage(entityX, entityY, obstacle.getCollisionDamage());

				return false;
			}
		} else if (nextType == 2) {
			attemptEntityPush(nextX, nextY, dirX, dirY);
		}

		return false;
	}

	/**
	 * Applies damage to an enemy at (x, y) and updates the model.
	 */
	/**
	 * Applies damage to an enemy at (x, y) and updates the model.
	 */
	private void takeDamage(int x, int y, int damage) {
		Enemy enemy = enemyGrid[x][y];
		if (enemy == null) {

			return;
		}

		enemy.takeDamage(damage);
		int afterHp = enemy.getHp();

		addEvent(new GameUpdateEvent(GameUpdateEvent.Type.DAMAGE, x, y, afterHp));

		if (enemy.isDead()) {
			removeEntity(x, y);
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
		Enemy enemy = enemyGrid[oldX][oldY];
		if (enemy == null)
			return;

		// Clear old spot
		entityGrid[oldX][oldY] = 0;
		enemyGrid[oldX][oldY] = null;

		// Place enemy
		entityGrid[newX][newY] = 2;
		enemyGrid[newX][newY] = enemy;

		addEvent(new GameUpdateEvent(GameUpdateEvent.Type.ENEMY_MOVE, oldX, oldY, newX, newY, enemy.getHp()));

		// Apply campfire damage if needed
		Obstacle obsAtNew = obstacleGrid[newX][newY];
		if (obsAtNew != null && obsAtNew.isPassable() && obsAtNew.getPassDamage() > 0) {
			takeDamage(newX, newY, obsAtNew.getPassDamage());
		}
	}

	/**
	 * Removes an entity from the game state.
	 */
	private void removeEntity(int x, int y) {
		entityGrid[x][y] = 0;
		enemyGrid[x][y] = null;
		addEvent(new GameUpdateEvent(GameUpdateEvent.Type.REMOVE_ENTITY, x, y));
	}

	// --- Event Handling for UI Synchronization ---

	/**
	 * Adds a game event to the queue for the UI to process.
	 */
	private void addEvent(GameUpdateEvent event) {
		String key = event.type.name() + event.newX + "," + event.newY;

		if (event.type == GameUpdateEvent.Type.IMPACT) {
			// Allow multiple IMPACT events per tile by making the key unique with a
			// timestamp
			key += "_" + System.nanoTime();
		}

		pendingEvents.put(key, event);
	}

	/**
	 * Retrieves and clears the list of pending events.
	 */
	public List<GameUpdateEvent> flushEvents() {
		List<GameUpdateEvent> events = new java.util.ArrayList<>(pendingEvents.values());
		pendingEvents.clear();

		// Stable, deterministic order to avoid race conditions in the view
		events.sort((a, b) -> Integer.compare(priority(a.type), priority(b.type)));
		return events;
	}

	private int priority(GameUpdateEvent.Type type) {
		switch (type) {
		case ENEMY_MOVE:
			return 1;
		case DAMAGE:
			return 2;
		case REMOVE_ENTITY:
			return 3;
		case IMPACT:
			return 4;
		case PLAYER_MOVE:
			return 5;
		default:
			return 99;
		}
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
		Enemy enemy = enemyGrid[x][y];
		return (enemy != null) ? enemy.getHp() : 0;
	}

	public int getPlayerX() {
		return playerX;
	}

	public int getPlayerY() {
		return playerY;
	}
	
	public String getDirection() {
		return direction;
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