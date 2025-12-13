# 1. Final Gameplay Overview

This is a grid-based survival game where players must survive waves of enemies in a dungeon-like environment. The game features real-time movement and combat, with enemies spawning in waves that players must push back with telekinetic abilities. Players can collect books after each wave to select upgrades that enhance their push abilities (range, strength, area effects) or gain other benefits like spawning bear traps. The core mechanics include player movement, enemy AI pathfinding, push mechanics for crowd control, and environmental hazards like campfires and spikes. In the final version, bear traps now function as one-time damage-dealing obstacles that disappear after being triggered, rather than immobilizing enemies.

Key features that made it in:
- Real-time player movement and enemy AI
- Push mechanics with multiple upgrade paths
- Wave-based enemy spawning system
- Upgrade system via book collection
- Multiple enemy types (Goblins, Skeletons, Brutes, Boomer Goblins)
- Environmental obstacles (Walls, Spikes, Campfires)
- Bear traps that deal damage and disappear
- Health system with visual feedback

# 2. Class Structure & OOP Details

## Classes in the Project:

- **Main**: Entry point class that starts the JavaFX application
- **GameLauncher**: Launches the main game window and manages the JavaFX application lifecycle
- **GamePanel**: Handles visual rendering of the game grid and entities using JavaFX
- **GameLogic**: Core game engine managing game state, entities, and game mechanics
- **InputHandler**: Processes keyboard and mouse input for player actions
- **AudioManager**: Singleton class managing sound effects and music
- **Player**: Represents the player character with health, movement, and abilities
- **Enemy (Abstract)**: Base class for all enemy types with shared behaviors and stats
- **Goblin**: Basic enemy type with simple AI
- **Skeleton**: Medium-tier enemy
- **SkeletonBrute**: Heavy enemy with higher health and damage
- **BoomerGoblin**: Special enemy that explodes when attacking
- **UpgradeManager**: Manages upgrade selection and application system
- **UpgradeSelectionView**: UI component for displaying upgrade choices
- **MainMenuView**: Main menu UI screen
- **LevelSelectView**: Level selection screen
- **SettingsView**: Game settings screen
- **Book**: Item that appears after clearing waves for upgrade selection
- **Trap**: Bear trap obstacle that damages enemies and disappears
- **Obstacle (Abstract)**: Base class for environmental objects
- **Wall**: Impassable obstacle
- **Spikes**: Damage-dealing passable obstacle
- **Campfire**: Damage-dealing passable obstacle
- **Pathfinder**: Pathfinding algorithm for enemy movement
- **SpawnSystem**: Manages enemy spawning in waves
- **SpriteAnimator**: Handles animated sprites for entities
- **GameUpdateEvent**: Data class for communication between game logic and UI
- **Constants**: Stores game constants and configuration values

## OOP Pillars Implementation:

**Abstraction**: 
- `Enemy` class is abstract with abstract methods like `updateAI()`, `playIdleAnimation()`, etc., forcing all enemy types to implement these behaviors
- `Obstacle` class is abstract with abstract methods for obstacle properties
- The `EnemyType` enum abstracts enemy categories making enemy handling more organized

**Encapsulation**:
- Private fields in classes like `Player` (health, position), `Enemy` (hp, damage), and `GameLogic` (player, enemies, traps)
- Public getter and setter methods to access internal state safely (e.g., `getPlayer()`, `getEnemies()`, `getHp()`, `isAlive()`)
- Protected fields in `Enemy` class to allow subclasses to access core properties while hiding them from external classes

**Inheritance**:
- `GameObject` → `Entity` → `Enemy` → `Goblin`/`Skeleton`/`SkeletonBrute`/`BoomerGoblin`
- `Obstacle` → `Wall`/`Spikes`/`Campfire`
- All enemies inherit from the base `Enemy` class, getting shared behaviors like health management, damage handling, and movement

**Polymorphism**:
- The `updateAI()` method is overridden by each enemy type to provide unique AI behaviors
- Animation methods (`playIdleAnimation()`, `playMoveAnimation()`, etc.) are overridden by each enemy type
- The game loop treats all enemies the same way using the base `Enemy` class reference, calling their specific overridden methods
- All enemies are stored in a common `List<Enemy>` and processed uniformly in the game loop, but their unique behaviors execute

# 3. Key Code Snippets

## The Main Game Loop class (GameLogic.java):

```java
package application;

import java.util.*;

/**
 * Core game logic with integrated spawn system, enemy AI, and push mechanics.
 */
public class GameLogic {
    // Grid dimensions
    public static final int GRID_WIDTH = 14;
    public static final int GRID_HEIGHT = 9;

    // Game entities
    private Player player;
    private List<Enemy> enemies;
    private List<Trap> traps;
    private int[][] entityGrid;
    private Obstacle[][] obstacleGrid;

    // Systems
    private Pathfinder pathfinder;
    private SpawnSystem spawnSystem;
    private UpgradeManager upgradeManager;

    // Event handling
    private Map<String, GameUpdateEvent> pendingEvents;
    private Random random;

    private String direction = "RIGHT";

    // Timing
    private long lastUpdateTime;

    // Game state
    private boolean isPaused;
    private boolean waitingForUpgrade;

    // Books and items
    private Book currentBook;

    /**
     * Creates game logic for specified level.
     */
    public GameLogic(int levelNumber) {
        lastUpdateTime = System.currentTimeMillis();
        enemies = new ArrayList<>();
        traps = new ArrayList<>();
        entityGrid = new int[GRID_WIDTH][GRID_HEIGHT];
        obstacleGrid = new Obstacle[GRID_WIDTH][GRID_HEIGHT];
        pendingEvents = new HashMap<>();
        random = new Random();

        pathfinder = new Pathfinder();
        upgradeManager = new UpgradeManager();

        isPaused = false;
        waitingForUpgrade = false;
        currentBook = null;

        initializeGrid();
        initializeSpawnSystem(levelNumber);
    }

    /**
     * Initializes game grid with walls and obstacles.
     */
    private void initializeGrid() {
        // Border walls
        for (int x = 0; x < GRID_WIDTH; x++) {
            placeObstacle(x, 0, new Wall());
            placeObstacle(x, GRID_HEIGHT - 1, new Wall());
        }
        for (int y = 0; y < GRID_HEIGHT; y++) {
            placeObstacle(0, y, new Wall());
            placeObstacle(GRID_WIDTH - 1, y, new Wall());
        }

        // Random interior obstacles
        for (int x = 1; x < GRID_WIDTH - 1; x++) {
            for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                // Skip center area for player spawn
                if (x >= 6 && x <= 8 && y >= 3 && y <= 5) continue;  // 3x3 safe area around (7,4)

                if (entityGrid[x][y] == 0 && random.nextInt(100) < 12) {
                    placeObstacle(x, y, createRandomObstacle());
                }
            }
        }

        // Initialize player in the geometric center of the grid
        player = new Player(7, 4);  // True center of 14x9 grid

        System.out.println("[INIT] Grid initialized, player at (" +
                          player.getX() + "," + player.getY() + ")");
    }

    /**
     * Creates random obstacle type.
     */
    private Obstacle createRandomObstacle() {
        int type = random.nextInt(3);
        switch (type) {
            case 0: return new Wall();
            case 1: return new Spikes();
            case 2: return new Campfire();
            default: return new Wall();
        }
    }

    /**
     * Places obstacle in grids.
     */
    private void placeObstacle(int x, int y, Obstacle obs) {
        if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
            entityGrid[x][y] = obs.getEntityType();
            obstacleGrid[x][y] = obs;
        }
    }

    /**
     * Initializes spawn system.
     */
    private void initializeSpawnSystem(int levelNumber) {
        List<SpawnSystem.SpawnPoint> spawnPoints = new ArrayList<>();

        // Add spawn points around edges (away from center player position at (7,4))
        // Top edge
        spawnPoints.add(new SpawnSystem.SpawnPoint(2, 1));  // Top-left
        spawnPoints.add(new SpawnSystem.SpawnPoint(6, 1));  // Top-center-left
        spawnPoints.add(new SpawnSystem.SpawnPoint(8, 1));  // Top-center-right
        spawnPoints.add(new SpawnSystem.SpawnPoint(12, 1)); // Top-right

        // Bottom edge
        spawnPoints.add(new SpawnSystem.SpawnPoint(2, 7));  // Bottom-left
        spawnPoints.add(new SpawnSystem.SpawnPoint(6, 7));  // Bottom-center-left
        spawnPoints.add(new SpawnSystem.SpawnPoint(8, 7));  // Bottom-center-right
        spawnPoints.add(new SpawnSystem.SpawnPoint(12, 7)); // Bottom-right

        // Left edge (excluding corners)
        spawnPoints.add(new SpawnSystem.SpawnPoint(1, 3));
        spawnPoints.add(new SpawnSystem.SpawnPoint(1, 5));

        // Right edge (excluding corners)
        spawnPoints.add(new SpawnSystem.SpawnPoint(12, 3));
        spawnPoints.add(new SpawnSystem.SpawnPoint(12, 5));

        if (levelNumber == 0) {
            spawnSystem = new SpawnSystem(spawnPoints); // Endless mode
        } else {
            spawnSystem = new SpawnSystem(spawnPoints, levelNumber);
        }
    }

    /**
     * Main update loop.
     */
    public void updateGame() {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;
        deltaTime = Math.min(deltaTime, 0.1);

        // Pause check
        if (isPaused || waitingForUpgrade) return;

        // Update player
        player.update(deltaTime);

        // Update traps (though they don't have an update in this version)
        for (Trap trap : traps) {
            trap.update(deltaTime);
        }

        // Remove triggered traps
        traps.removeIf(trap -> !trap.isActive());
        // Update the entity grid to remove trap markers for disappeared traps
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (entityGrid[x][y] == 6) {
                    // Check if there's still an active trap at this location
                    boolean trapStillExists = false;
                    for (Trap trap : traps) {
                        if (trap.getX() == x && trap.getY() == y && trap.isActive()) {
                            trapStillExists = true;
                            break;
                        }
                    }
                    if (!trapStillExists) {
                        entityGrid[x][y] = 0; // Clear the trap marker
                    }
                }
            }
        }

        // Check book collection
        if (currentBook != null && !currentBook.isCollected()) {
            if (player.getX() == currentBook.getX() && player.getY() == currentBook.getY()) {
                currentBook.collect();
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.BOOK_COLLECTED,
                                            currentBook.getX(), currentBook.getY()));
                waitingForUpgrade = true;
            }
        }

        // Update enemy AI
        updateEnemyAI(deltaTime);

        // Update spawn system
        Enemy newEnemy = spawnSystem.update(deltaTime, enemies, entityGrid);
        if (newEnemy != null) {
            enemies.add(newEnemy);
            entityGrid[newEnemy.getX()][newNew.getY()] = 2;
            addEvent(new GameUpdateEvent(
                GameUpdateEvent.Type.ENEMY_SPAWN,
                newNew.getX(), newNew.getY(), newNew.getHp()
            ));
        }

        // Spawn book after wave complete
        if (spawnSystem.shouldSpawnBook() && currentBook == null) {
            spawnBook();
        }

        // Check player environment damage
        checkEnvironmentDamage();
    }

    /**
     * Spawns a book at random location near player.
     */
    private void spawnBook() {
        // Find a suitable location near the player for the book
        List<int[]> validPositions = new ArrayList<>();

        // Check positions around player (avoiding immediate adjacent since player might move there)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                // Skip center (player position) and adjacent positions
                if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) continue;

                int x = player.getX() + dx;
                int y = player.getY() + dy;

                // Check if position is within bounds
                if (x >= 1 && x < GRID_WIDTH - 1 && y >= 1 && y < GRID_HEIGHT - 1) {
                    // Check if position is empty
                    if (entityGrid[x][y] == 0) {
                        validPositions.add(new int[]{x, y});
                    }
                }
            }
        }

        // If no suitable positions near player, try random positions
        if (validPositions.isEmpty()) {
            for (int x = 1; x < GRID_WIDTH - 1; x++) {
                for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                    if (entityGrid[x][y] == 0 &&
                        !(x == player.getX() && y == player.getY())) {
                        validPositions.add(new int[]{x, y});
                    }
                }
            }
        }

        if (!validPositions.isEmpty()) {
            // Pick random position
            int[] pos = validPositions.get(random.nextInt(validPositions.size()));
            currentBook = new Book(pos[0], pos[1]);
            entityGrid[pos[0]][pos[1]] = 5; // Book entity type
            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.BOOK_SPAWN, pos[0], pos[1]));
            System.out.println("[BOOK] Book spawned at (" + pos[0] + "," + pos[1] + ")");
        } else {
            System.out.println("[ERROR] No valid position to spawn book!");
        }
    }

    public void spawnBearTraps() {
        int trapCount = 2 + random.nextInt(2); // 2 or 3 traps
        List<int[]> validPositions = new ArrayList<>();

        // Find all valid empty positions
        for (int x = 1; x < GRID_WIDTH - 1; x++) {
            for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                if (entityGrid[x][y] == 0 &&
                    !(x == player.getX() && y == player.getY())) {
                    validPositions.add(new int[]{x, y});
                }
            }
        }

        Collections.shuffle(validPositions);

        int spawned = 0;
        for (int i = 0; i < Math.min(trapCount, validPositions.size()); i++) {
            int[] pos = validPositions.get(i);
            Trap trap = new Trap(pos[0], pos[1]);
            traps.add(trap);
            entityGrid[pos[0]][pos[1]] = 6; // Trap entity type
            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.TRAP_SPAWN, pos[0], pos[1]));
            spawned++;
            System.out.println("[TRAP] Bear Trap spawned at (" + pos[0] + "," + pos[1] + ")");
        }

        System.out.println("[TRAP] Spawned " + spawned + " bear traps");
    }

    // Pause/Resume
    public void pauseGame() { isPaused = true; }
    public void resumeGame() {
        isPaused = false;
        waitingForUpgrade = false;
        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Updates all enemy AI.
     */
    private void updateEnemyAI(double deltaTime) {
        List<Enemy> toRemove = new ArrayList<>();

        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
                continue;
            }

            // Update enemy cooldowns
            enemy.updateCooldowns(deltaTime, player.getX(), player.getY());

            // Check if can attack player (with cooldown check)
         // Check if can attack player (with cooldown check)
            if (enemy.canAttackPlayer(player.getX(), player.getY())) {
                // Trigger attack animation + cooldown
                enemy.tryAttackPlayer(player.getX(), player.getY());

                // Apply damage after animation trigger
                // For BoomerGoblins, they explode on attack, so skip normal damage to prevent double damage
                if (!(enemy instanceof BoomerGoblin)) {
                    player.takeDamage(enemy.getDamage());
                }

                // Play attack sound for the enemy type
                // Use per-enemy attack sounds to avoid wrong samples playing
                if (enemy instanceof Goblin) {
                    AudioManager.getInstance().playMobSound("GOBLIN_ATTACK");
                } else if (enemy instanceof SkeletonBrute) {
                    AudioManager.getInstance().playMobSound("BRUTE_ATTACK");
                } else if (enemy instanceof Skeleton) {
                    AudioManager.getInstance().playMobSound("SKELETON_ATTACK");
                } else if (enemy instanceof BoomerGoblin) {
                    AudioManager.getInstance().playMobSound("BOOMER_ATTACK");
                } else {
                    // Fallback
                    AudioManager.getInstance().playMobSound("GOBLIN_ATTACK");
                }

                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.PLAYER_DAMAGE,
                    player.getX(), player.getY(), player.getHealth()
                ));

                if (player.isInvulnerable()) {
                    System.out.println("[COMBAT] " + enemy.getType() +
                                     " attacked but player has I-frames!");
                } else {
                    System.out.println("[COMBAT] " + enemy.getType() +
                                     " attacked player! Player HP: " + player.getHealth());
                }
                continue;
            }

            // Get AI movement
            int[] move = enemy.updateAI(deltaTime, player.getX(), player.getY(),
                                       pathfinder, entityGrid);

            // Movement is now allowed regardless of immobilization status (traps now just deal damage)
            if (move != null) {
                int targetX = enemy.getX() + move[0];
                int targetY = enemy.getY() + move[1];

                // Validate and execute move
                if (isValidPosition(targetX, targetY)) {
                    int targetType = entityGrid[targetX][targetY];

                    // Prevent enemies from moving into the player's tile
                    if (targetX == player.getX() && targetY == player.getY()) {
                        continue; // skip this move
                    }

                    // Move to empty, book, or trap tile (books and traps are passable)
                    if (targetType == 0 || targetType == 5 || targetType == 6) {
                        moveEnemy(enemy, targetX, targetY);
                    }
                }

            }
        }

        // Remove dead enemies and handle explosions
        for (Enemy enemy : toRemove) {
            handleEnemyDeath(enemy);
        }
        enemies.removeAll(toRemove);
    }

    /**
     * Handles enemy death and special effects (explosions).
     */
    private void handleEnemyDeath(Enemy enemy) {
        int x = enemy.getX();
        int y = enemy.getY();

        // Clear from grid
        entityGrid[x][y] = 0;

        // Play generic mob death sound for all enemies
        AudioManager.getInstance().playMobSound("MOB_DEATH");

        // Handle Boomer Goblin explosion special case
        if (enemy instanceof BoomerGoblin) {
            BoomerGoblin boomer = (BoomerGoblin) enemy;
            handleExplosion(boomer);
            // Play explosion sound as well
            AudioManager.getInstance().playMobSound("EXPLODE");
        }

        spawnSystem.onEnemyDefeated();
        addEvent(new GameUpdateEvent(GameUpdateEvent.Type.REMOVE_ENTITY, x, y));
    }

    /**
     * Handles Boomer Goblin explosion damage.
     */
    private void handleExplosion(BoomerGoblin boomer) {
        int ex = boomer.getX();
        int ey = boomer.getY();
        int explosionDamage = boomer.getDamage();

        addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, ex, ey));

        // Damage adjacent entities (cardinal directions only - no diagonals)
        int[] dxMoves = {0, 0, -1, 1};  // up, down, left, right
        int[] dyMoves = {-1, 1, 0, 0};  // up, down, left, right

        for (int i = 0; i < 4; i++) {
            int dx = dxMoves[i];
            int dy = dyMoves[i];

            int tx = ex + dx;
            int ty = ey + dy;

            if (!isValidPosition(tx, ty)) continue;

            // Damage player
            if (player.getX() == tx && player.getY() == ty) {
                player.takeDamage(explosionDamage);
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.PLAYER_DAMAGE,
                    tx, ty, player.getHealth()
                ));
                System.out.println("[EXPLOSION] Player hit by explosion!");
            }

            // Damage other enemies
            Enemy targetEnemy = findEnemyAt(tx, ty);
            if (targetEnemy != null) {
                targetEnemy.takeDamage(explosionDamage);
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE,
                    tx, ty, targetEnemy.getHp()
                ));
                System.out.println("[EXPLOSION] Enemy hit by explosion!");
            }
        }
    }

    /**
     * Moves enemy to new position.
     */
    private void moveEnemy(Enemy enemy, int newX, int newY) {
        int oldX = enemy.getX();
        int oldY = enemy.getY();

        entityGrid[oldX][oldY] = 0;
        String dir = (newX > enemy.getX()) ? "RIGHT" :
            (newX < enemy.getX()) ? "LEFT" :
            (newY > enemy.getY()) ? "DOWN" : "UP";

        enemy.moveTo(newX, newY, dir);

        entityGrid[newX][newY] = 2;

        addEvent(new GameUpdateEvent(
            GameUpdateEvent.Type.ENEMY_MOVE,
            oldX, oldY, newX, newY, enemy.getHp()
        ));

        // Check campfire damage
        Obstacle obs = obstacleGrid[newX][newY];
        if (obs != null && obs.isPassable() && obs.getPassDamage() > 0) {
            enemy.takeDamage(obs.getPassDamage());
            addEvent(new GameUpdateEvent(
                GameUpdateEvent.Type.DAMAGE,
                newX, newY, enemy.getHp()
            ));
        }

        // Check trap collision
        checkTrapCollision(enemy, newX, newY);
    }

    /**
     * Checks trap collision for enemy.
     */
    private void checkTrapCollision(Enemy enemy, int x, int y) {
        for (Trap trap : traps) {
            if (trap.getX() == x && trap.getY() == y && trap.isActive()) {
                enemy.takeDamage(trap.getDamage());
                trap.trigger(); // This will deactivate the trap, and it will be removed in the next update
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE,
                    x, y, enemy.getHp()
                ));
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.TRAP_TRIGGER,
                    x, y
                ));
                System.out.println("[TRAP] Enemy triggered trap at (" + x + "," + y + ")");
            }
        }
    }

    /**
     * Checks player environment damage.
     */
    private void checkEnvironmentDamage() {
        int px = player.getX();
        int py = player.getY();

        Obstacle obs = obstacleGrid[px][py];
        if (obs != null && obs.isPassable() && obs.getPassDamage() > 0) {
            player.takeDamage(obs.getPassDamage());
            addEvent(new GameUpdateEvent(
                GameUpdateEvent.Type.PLAYER_DAMAGE,
                px, py, player.getHealth()
            ));
        }
    }

    /**
     * Player attempts move or push.
     */
    public boolean attemptMove(int dirX, int dirY) {
        if (!player.isAlive()) return false;


        if (dirX > 0) player.setDirection("RIGHT");
	    else if (dirX < 0) player.setDirection("LEFT");
	    else if (dirY > 0) player.setDirection("DOWN");
	    else if (dirY < 0) player.setDirection("UP");

        int targetX = player.getX() + dirX;
        int targetY = player.getY() + dirY;

        if (!isValidPosition(targetX, targetY)) return false;

        int targetType = entityGrid[targetX][targetY];

        // Empty tile or passable obstacle or book or trap
        if (targetType == 0 || targetType == 5 || targetType == 6 || (isObstacleType(targetType) &&
            obstacleGrid[targetX][targetY].isPassable())) {
            movePlayer(targetX, targetY);
            return true;
        }

        // Impassable obstacle
        if (isObstacleType(targetType) && !obstacleGrid[targetX][targetY].isPassable()) {
            return false;
        }

        // Enemy - push logic (only adjacent)
        if (targetType == 2) {
            return handlePush(targetX, targetY, dirX, dirY);
        }

        return false;
    }

    /**
     * Handles telekinetic push (pushing from 2+ tiles away).
     */
    private boolean handleTelekineticPush(int enemyX, int enemyY, int dirX, int dirY) {
        Enemy enemy = findEnemyAt(enemyX, enemyY);
        if (enemy == null) return false;

        // Calculate push destination based on direction and push strength upgrade
        // Base distance is 1, but Push Strength upgrade increases it
        int pushDistance = 1 + upgradeManager.getPushStrengthLevel();

        // For brutes, always push 1 tile (from the upgrade description)
        if (enemy.getType() == Enemy.EnemyType.BRUTE) {
            pushDistance = 1;
        }

        int pushX = enemyX + (dirX * pushDistance);
        int pushY = enemyY + (dirY * pushDistance);

        if (!isValidPosition(pushX, pushY)) return false;

        player.playAttackAnimation(); // Show player push animation
        int pushType = entityGrid[pushX][pushY];

        // Push into empty or campfire
        if (pushType == 0 || pushType == 4) {
            moveEnemy(enemy, pushX, pushY);
            enemy.playPushAnimation(player.getDirection());
            AudioManager.getInstance().playPlayerSound("PUSH");
            return true;
        }

        // Push into obstacle
        if (isObstacleType(pushType)) {
            Obstacle obs = obstacleGrid[pushX][pushY];
            if (obs.isPassable()) {
                moveEnemy(enemy, pushX, pushY);
                enemy.playHitAnimation(player.getDirection());
                AudioManager.getInstance().playPlayerSound("PUSH");
            } else {
                // Collision damage
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));
                enemy.takeDamage(obs.getCollisionDamage());
                enemy.playHitAnimation(player.getDirection());
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE,
                    enemyX, enemyY, enemy.getHp()
                ));
                System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() +
                                 " collision damage!");
            }
            return true;
        }

        // Push into another enemy (chain push)
        if (pushType == 2) {
            return attemptChainPush(pushX, pushY, dirX, dirY);
        }

        return false;
    }

    /**
     * Player performs push action in all adjacent directions.
     */
    public boolean attemptPushAction() {
        if (!player.isAlive() || !player.canPush()) return false;

        boolean pushedAny = false;
        int px = player.getX();
        int py = player.getY();

        // Determine the range of the push based on upgrades
        int range = 1; // default
        if (upgradeManager.hasRangePlus()) {
            range = 2; // With Push Range II, can push from 2+ tiles away
        } else if (upgradeManager.hasPushRange()) {
            range = 1; // Push Range I allows adjacent pushes
        }

        // Check if using Push Area upgrade (wave push - 3 tiles in front)
        if (upgradeManager.hasPushAreaUpgrade()) {
            pushedAny = attemptPushArea();
        } else {
            // Use the standard push with range upgrade consideration
            // For normal push, check adjacent cells (or cells within range with upgrades)
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    // Skip the player's position
                    if (dx == 0 && dy == 0) continue;

                    int distance = Math.abs(dx) + Math.abs(dy); // Manhattan distance
                    if (distance > range) continue; // Only push within the upgrade range

                    int enemyX = px + dx;
                    int enemyY = py + dy;

                    if (!isValidPosition(enemyX, enemyY)) continue;

                    if (entityGrid[enemyX][enemyY] == 2) {
                        // Calculate push direction and distance based on upgrade
                        int pushDirectionX = dx;
                        int pushDirectionY = dy;

                        // Normalize direction for multi-tile push
                        if (pushDirectionX != 0) pushDirectionX = pushDirectionX / Math.abs(pushDirectionX);
                        if (pushDirectionY != 0) pushDirectionY = pushDirectionY / Math.abs(pushDirectionY);

                        // Calculate push distance based on push strength upgrade
                        // Base distance is 1, but Push Strength upgrade increases it
                        int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
                        if (findEnemyAt(enemyX, enemyY).getType() == Enemy.EnemyType.BRUTE) {
                            pushDistance = 1; // Brutes always pushed 1 tile
                        }

                        int pushX = enemyX + (pushDirectionX * pushDistance);
                        int pushY = enemyY + (pushDirectionY * pushDistance);

                        if (isValidPosition(pushX, pushY)) {
                            Enemy enemy = findEnemyAt(enemyX, enemyY);
                            if (enemy != null) {
                                int pushType = entityGrid[pushX][pushY];

                                // Push into empty or campfire or trap
                                if (pushType == 0 || pushType == 4 || pushType == 6) {
                                    moveEnemy(enemy, pushX, pushY);
                                    pushedAny = true;
                                }
                                // Push into obstacle
                                else if (isObstacleType(pushType)) {
                                    Obstacle obs = obstacleGrid[pushX][pushY];
                                    if (obs.isPassable()) {
                                        moveEnemy(enemy, pushX, pushY);
                                    } else {
                                        // Collision damage
                                        addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));
                                        enemy.takeDamage(obs.getCollisionDamage());
                                        addEvent(new GameUpdateEvent(
                                            GameUpdateEvent.Type.DAMAGE,
                                            enemyX, enemyY, enemy.getHp()
                                        ));
                                        System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() +
                                                         " collision damage!");
                                    }
                                    pushedAny = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (pushedAny) {
            player.activatePushCooldown();
            System.out.println("[PUSH] Player used push action!");

            // Play push sound effect
            AudioManager.getInstance().playPlayerSound("PUSH");
        }

        return pushedAny;
    }

    /**
     * Attempts to perform a wave push (Push Area upgrade) - 3 tiles in front
     */
    private boolean attemptPushArea() {
        int px = player.getX();
        int py = player.getY();

        // Determine direction player is facing
        String direction = player.getDirection();

        // Set up the push direction based on player facing
        int dirX = 0, dirY = 0;
        if ("UP".equals(direction)) {
            dirX = 0;
            dirY = -1;
        } else if ("DOWN".equals(direction)) {
            dirX = 0;
            dirY = 1;
        } else if ("LEFT".equals(direction)) {
            dirX = -1;
            dirY = 0;
        } else if ("RIGHT".equals(direction)) {
            dirX = 1;
            dirY = 0;
        }

        boolean pushedAny = false;

        // Wave push in a "fan" pattern based on facing direction
        // For each tile in front (1, 2, 3), also check the adjacent tiles to the sides
        for (int i = 1; i <= 3; i++) {
            int centerWaveX = px + (dirX * i);
            int centerWaveY = py + (dirY * i);

            // Check center line (directly in front)
            if (isValidPosition(centerWaveX, centerWaveY) && entityGrid[centerWaveX][centerWaveY] == 2) {
                Enemy enemy = findEnemyAt(centerWaveX, centerWaveY);
                if (enemy != null) {
                    // Calculate push destination in the same direction
                    // Base distance is 1, but Push Strength upgrade increases it
                    int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
                    if (enemy.getType() == Enemy.EnemyType.BRUTE) {
                        pushDistance = 1; // Brutes always pushed 1 tile
                    }

                    int pushX = centerWaveX + (dirX * pushDistance);
                    int pushY = centerWaveY + (dirY * pushDistance);

                    if (isValidPosition(pushX, pushY)) {
                        int pushType = entityGrid[pushX][pushY];

                        // Push into empty or campfire or trap
                        if (pushType == 0 || pushType == 4 || pushType == 6) {
                            moveEnemy(enemy, pushX, pushY);
                            pushedAny = true;
                        }
                        // Push into obstacle
                        else if (isObstacleType(pushType)) {
                            Obstacle obs = obstacleGrid[pushX][pushY];
                            if (obs.isPassable()) {
                                moveEnemy(enemy, pushX, pushY);
                            } else {
                                // Collision damage
                                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));
                                enemy.takeDamage(obs.getCollisionDamage());
                                addEvent(new GameUpdateEvent(
                                    GameUpdateEvent.Type.DAMAGE,
                                    centerWaveX, centerWaveY, enemy.getHp()
                                ));
                                System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() +
                                                 " collision damage!");
                            }
                            pushedAny = true;
                        }
                    }
                }
            }

            // Calculate perpendicular directions for the wave spread (relative to facing direction)
            int leftPerpX = 0, leftPerpY = 0;
            int rightPerpX = 0, rightPerpY = 0;

            // Determine left and right directions relative to the facing direction
            if (dirX == 0 && dirY == -1) { // Facing North
                leftPerpX = -1; leftPerpY = 0;  // Left is West
                rightPerpX = 1; rightPerpY = 0; // Right is East
            } else if (dirX == 0 && dirY == 1) { // Facing South
                leftPerpX = -1; leftPerpY = 0;  // Left is West
                rightPerpX = 1; rightPerpY = 0; // Right is East
            } else if (dirX == 1 && dirY == 0) { // Facing East
                leftPerpX = 0; leftPerpY = -1;  // Left is North
                rightPerpX = 0; rightPerpY = 1; // Right is South
            } else if (dirX == -1 && dirY == 0) { // Facing West
                leftPerpX = 0; leftPerpY = -1;  // Left is North
                rightPerpX = 0; rightPerpY = 1; // Right is South
            }

            // Check positions to the left and right of the main direction
            int leftX = centerWaveX + leftPerpX;
            int leftY = centerWaveY + leftPerpY;

            int rightX = centerWaveX + rightPerpX;
            int rightY = centerWaveY + rightPerpY;

            // Check left side
            if (isValidPosition(leftX, leftY) && entityGrid[leftX][leftY] == 2) {
                Enemy enemy = findEnemyAt(leftX, leftY);
                if (enemy != null) {
                    // Calculate push destination in the same direction as main push
                    // Base distance is 1, but Push Strength upgrade increases it
                    int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
                    if (enemy.getType() == Enemy.EnemyType.BRUTE) {
                        pushDistance = 1; // Brutes always pushed 1 tile
                    }

                    int pushX = leftX + (dirX * pushDistance);
                    int pushY = leftY + (dirY * pushDistance);

                    if (isValidPosition(pushX, pushY)) {
                        int pushType = entityGrid[pushX][pushY];

                        // Push into empty or campfire or trap
                        if (pushType == 0 || pushType == 4 || pushType == 6) {
                            moveEnemy(enemy, pushX, pushY);
                            pushedAny = true;
                        }
                        // Push into obstacle
                        else if (isObstacleType(pushType)) {
                            Obstacle obs = obstacleGrid[pushX][pushY];
                            if (obs.isPassable()) {
                                moveEnemy(enemy, pushX, pushY);
                            } else {
                                // Collision damage
                                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));
                                enemy.takeDamage(obs.getCollisionDamage());
                                addEvent(new GameUpdateEvent(
                                    GameUpdateEvent.Type.DAMAGE,
                                    leftX, leftY, enemy.getHp()
                                ));
                                System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() +
                                                 " collision damage!");
                            }
                            pushedAny = true;
                        }
                    }
                }
            }

            // Check right side
            if (isValidPosition(rightX, rightY) && entityGrid[rightX][rightY] == 2) {
                Enemy enemy = findEnemyAt(rightX, rightY);
                if (enemy != null) {
                    // Calculate push destination in the same direction as main push
                    // Base distance is 1, but Push Strength upgrade increases it
                    int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
                    if (enemy.getType() == Enemy.EnemyType.BRUTE) {
                        pushDistance = 1; // Brutes always pushed 1 tile
                    }

                    int pushX = rightX + (dirX * pushDistance);
                    int pushY = rightY + (dirY * pushDistance);

                    if (isValidPosition(pushX, pushY)) {
                        int pushType = entityGrid[pushX][pushY];

                        // Push into empty or campfire or trap
                        if (pushType == 0 || pushType == 4 || pushType == 6) {
                            moveEnemy(enemy, pushX, pushY);
                            pushedAny = true;
                        }
                        // Push into obstacle
                        else if (isObstacleType(pushType)) {
                            Obstacle obs = obstacleGrid[pushX][pushY];
                            if (obs.isPassable()) {
                                moveEnemy(enemy, pushX, pushY);
                            } else {
                                // Collision damage
                                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));
                                enemy.takeDamage(obs.getCollisionDamage());
                                addEvent(new GameUpdateEvent(
                                    GameUpdateEvent.Type.DAMAGE,
                                    rightX, rightY, enemy.getHp()
                                ));
                                System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() +
                                                 " collision damage!");
                            }
                            pushedAny = true;
                        }
                    }
                }
            }
        }

        return pushedAny;
    }

    /**
     * Handles push mechanics.
     */
    private boolean handlePush(int enemyX, int enemyY, int dirX, int dirY) {
        // Calculate push distance based on upgrade
        // Base distance is 1, but Push Strength upgrade increases it
        int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
        Enemy enemy = findEnemyAt(enemyX, enemyY);
        if (enemy == null) return false;

        if (enemy.getType() == Enemy.EnemyType.BRUTE) {
            pushDistance = 1; // Brutes always pushed 1 tile
        }

        int pushX = enemyX + (dirX * pushDistance);
        int pushY = enemyY + (dirY * pushDistance);

        if (!isValidPosition(pushX, pushY)) return false;
        player.playAttackAnimation();

        int pushType = entityGrid[pushX][pushY];

        // Push into empty or campfire or trap
        if (pushType == 0 || pushType == 4 || pushType == 6) {
            moveEnemy(enemy, pushX, pushY);

            enemy.playPushAnimation(player.getDirection());
            // Play the push SFX for single-direction push
            AudioManager.getInstance().playPlayerSound("PUSH");
            return true;
        }

        // Push into obstacle
        if (isObstacleType(pushType)) {
            Obstacle obs = obstacleGrid[pushX][pushY];
            if (obs.isPassable()) {
                moveEnemy(enemy, pushX, pushY);
                enemy.playHitAnimation(player.getDirection());
                AudioManager.getInstance().playPlayerSound("PUSH");
            } else {
                // Collision damage
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));
                enemy.takeDamage(obs.getCollisionDamage());
                enemy.playHitAnimation(player.getDirection());
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE,
                    enemyX, enemyY, enemy.getHp()
                ));
                System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() +
                                 " collision damage!");
            }
            return true;
        }

        // Push into another enemy (chain push)
        if (pushType == 2) {
            return attemptChainPush(pushX, pushY, dirX, dirY);
        }

        return false;
    }

    /**
     * Handles chain pushing.
     */
    private boolean attemptChainPush(int entityX, int entityY, int dirX, int dirY) {
        // Calculate push distance based on upgrade
        // Base distance is 1, but Push Strength upgrade increases it
        int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
        Enemy enemy = findEnemyAt(entityX, entityY);
        if (enemy == null) return false;

        if (enemy.getType() == Enemy.EnemyType.BRUTE) {
            pushDistance = 1; // Brutes always pushed 1 tile
        }

        int nextX = entityX + (dirX * pushDistance);
        int nextY = entityY + (dirY * pushDistance);

        if (!isValidPosition(nextX, nextY)) return false;

        int nextType = entityGrid[nextX][nextY];

        if (nextType == 0 || nextType == 4 || nextType == 6) {
            moveEnemy(enemy, nextX, nextY);
            enemy.playPushAnimation(player.getDirection());
            AudioManager.getInstance().playPlayerSound("PUSH");
            return true;
        } else if (isObstacleType(nextType)) {
            Obstacle obs = obstacleGrid[nextX][nextY];
            if (obs.isPassable()) {
                moveEnemy(enemy, nextX, nextY);
                enemy.playHitAnimation(player.getDirection());
                return true;
            } else {
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
                enemy.takeDamage(obs.getCollisionDamage());
                enemy.playHitAnimation(player.getDirection());
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE,
                    entityX, entityY, enemy.getHp()
                ));
                return false;
            }
        } else if (nextType == 2) {
            return attemptChainPush(nextX, nextY, dirX, dirY);
        }

        return false;
    }

    /**
     * Moves player.
     */
    private void movePlayer(int newX, int newY) {
        int oldX = player.getX();
        int oldY = player.getY();

        player.moveTo(newX, newY);
        addEvent(new GameUpdateEvent(
            GameUpdateEvent.Type.PLAYER_MOVE,
            oldX, oldY, newX, newY
        ));
    }

    /**
     * Finds enemy at position.
     */
    public Enemy findEnemyAt(int x, int y) {
        for (Enemy enemy : enemies) {
            if (enemy.getX() == x && enemy.getY() == y && !enemy.isDead()) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Checks if position is valid.
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT;
    }

    /**
     * Checks if entity type is obstacle.
     */
    private boolean isObstacleType(int type) {
        return type == 1 || type == 3 || type == 4;
    }

    /**
     * Adds event to queue.
     */
    private void addEvent(GameUpdateEvent event) {
        String key = event.type.name() + event.newX + "," + event.newY;

        if (event.type == GameUpdateEvent.Type.IMPACT) {
            key += "_" + System.nanoTime();
        }


        pendingEvents.put(key, event);
    }

    /**
     * Flushes and returns pending events.
     */
    public List<GameUpdateEvent> flushEvents() {
        List<GameUpdateEvent> events = new ArrayList<>(pendingEvents.values());
        pendingEvents.clear();

        events.sort((a, b) -> Integer.compare(getPriority(a.type), getPriority(b.type)));
        return events;
    }

    private int getPriority(GameUpdateEvent.Type type) {
        switch (type) {
            case ENEMY_SPAWN: return 0;
            case ENEMY_MOVE: return 1;
            case DAMAGE: return 2;
            case REMOVE_ENTITY: return 3;
            case IMPACT: return 4;
            case PLAYER_DAMAGE: return 5;
            case PLAYER_MOVE: return 6;
            case BOOK_SPAWN: return 0;
            case BOOK_COLLECTED: return 9;
            case TRAP_SPAWN: return 0;
            case TRAP_TRIGGER: return 8;
            default: return 99;
        }
    }

    // Getters
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public SpawnSystem getSpawnSystem() { return spawnSystem; }
    public UpgradeManager getUpgradeManager() { return upgradeManager; }
    public int getEntityAt(int x, int y) { return entityGrid[x][y]; }
    public int getEnemyHealthAt(int x, int y) {
        Enemy enemy = findEnemyAt(x, y);
        return enemy != null ? enemy.getHp() : 0;
    }
    public boolean isLevelComplete() {
        return spawnSystem.isLevelComplete();
    }

    public boolean isWaitingForUpgrade() { return waitingForUpgrade; }

    public Book getCurrentBook() { return currentBook; }

    public void clearBook() {
        if (currentBook != null) {
            entityGrid[currentBook.getX()][currentBook.getY()] = 0;
            currentBook = null;
        }
    }
}
```

## The Player class:

```java
package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Player character with health, movement, and push abilities.
 */
public class Player {
    private int x, y;
    private int health;
    private int maxHealth;
    private boolean alive;
    private String direction; // Direction player is facing ("UP", "DOWN", "LEFT", "RIGHT")

    // Push mechanics
    private boolean canPush;
    private double pushCooldown;
    private double pushCooldownMax;

    // Invulnerability frames after taking damage
    private boolean invulnerable;
    private double invulnTimer;
    private double invulnDuration;

    // Animator
    private SpriteAnimator animator;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = 3;
        this.maxHealth = 3;
        this.alive = true;
        this.direction = "RIGHT";

        // Push mechanics
        this.pushCooldownMax = 0.3; // 0.3 second cooldown
        this.pushCooldown = 0;
        this.canPush = true;

        // Invulnerability frames
        this.invulnDuration = 0.8; // 0.8 seconds of invulnerability
        this.invulnTimer = 0;
        this.invulnerable = false;

        // Initialize animator with player sprite sheet
        try {
            Image spriteSheet = new Image(getClass().getResource("/assets/player/PlayerSpritesheet.png").toExternalForm());
            this.animator = new SpriteAnimator(spriteSheet, 72, 72);
        } catch (Exception e) {
            System.out.println("[ERROR] Could not load player sprite sheet: " + e.getMessage());
            this.animator = null;
        }
    }

    public void update(double deltaTime) {
        // Update push cooldown
        if (!canPush) {
            pushCooldown -= deltaTime;
            if (pushCooldown <= 0) {
                canPush = true;
                pushCooldown = 0;
            }
        }

        // Update invulnerability frames
        if (invulnerable) {
            invulnTimer -= deltaTime;
            if (invulnTimer <= 0) {
                invulnerable = false;
                invulnTimer = 0;
            }
        }
    }

    public void takeDamage(int damage) {
        if (!alive || invulnerable) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
            System.out.println("[PLAYER] Player died at (" + x + "," + y + ")");
        } else {
            // Apply invulnerability frames
            invulnerable = true;
            invulnTimer = invulnDuration;
            System.out.println("[PLAYER] Player took " + damage + " damage, HP: " + health + "/" + maxHealth);
        }
    }

    public void heal(int healAmount) {
        health = Math.min(maxHealth, health + healAmount);
        System.out.println("[PLAYER] Player healed " + healAmount + " HP, HP: " + health + "/" + maxHealth);
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
        playMoveAnimation();
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void activatePushCooldown() {
        canPush = false;
        pushCooldown = pushCooldownMax;
    }

    // --- Animation Hooks ---
    public void playIdleAnimation() {
        if (animator != null) {
            animator.playAnimation(0, 0, 1, () -> {}, "idle_" + direction.toLowerCase());
        }
    }

    public void playMoveAnimation() {
        if (animator != null) {
            animator.playAnimation(1, 0, 4, () -> {}, "move_" + direction.toLowerCase());
        }
    }

    public void playAttackAnimation() {
        if (animator != null) {
            animator.playAnimation(2, 0, 1, () -> {
                // Animation complete callback - could add special effects here
            }, "attack_" + direction.toLowerCase());
        }
    }

    // --- Accessors ---
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return alive; }
    public boolean canPush() { return canPush; }
    public double getPushCooldown() { return pushCooldown; }
    public String getDirection() { return direction; }
    public boolean isInvulnerable() { return invulnerable; }
    public boolean shouldFlicker() { return invulnerable && (invulnTimer % 0.2) < 0.1; } // Flashing effect
    public ImageView getImageView() { return animator.getImageView(); }
}
```

## The base Enemy class:

```java
package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Enemy {
    protected int x, y;
    protected int hp;
    protected int maxHp;
    protected int damage;
    protected boolean dead;
    protected double movementCooldown;
    protected double movementCooldownMax;
    protected EnemyType type;

    // Attack cooldown system
    protected double attackCooldown;
    protected double attackCooldownMax;

    // Trap immobilization - removed in latest update since traps now just do damage
    protected double trapImmobilizationTimer;
    protected double trapImmobilizationDuration;
    protected boolean isTrapped;

    // Animator
    protected SpriteAnimator animator;

    // Direction (e.g., "UP", "DOWN", "LEFT", "RIGHT")
    protected String direction;

    public enum EnemyType {
        GOBLIN,
        SKELETON,
        BRUTE,
        BOOMER
    }

    public Enemy(int x, int y, EnemyType type, Image spriteSheet) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.dead = false;
        this.attackCooldownMax = 1.2; // 1 second between attacks by default
        this.attackCooldown = attackCooldownMax;

        // Initialize trap immobilization (no longer used since traps now just do damage)
        this.trapImmobilizationDuration = 1.5; // 1.5 seconds of immobilization
        this.trapImmobilizationTimer = 0;
        this.isTrapped = false;

        // Initialize animator with enemy sprite sheet
        this.animator = new SpriteAnimator(spriteSheet, 72, 72);

        // Default facing direction
        this.direction = "RIGHT";
    }

    // --- AI ---
    public abstract int[] updateAI(double deltaTime, int playerX, int playerY,
                                   Pathfinder pathfinder, int[][] grid);

    public void updateCooldowns(double deltaTime, int playerX, int playerY) {
        // Update trap immobilization timer (no longer used since traps now just do damage)
        if (isTrapped) {
            trapImmobilizationTimer -= deltaTime;
            if (trapImmobilizationTimer <= 0) {
                isTrapped = false;
                trapImmobilizationTimer = 0;
            }
        }

        int dx = Math.abs(x - playerX);
        int dy = Math.abs(y - playerY);
        boolean adjacent = (dx == 1 && dy == 0) || (dx == 0 && dy == 1);

        if (adjacent) {
            // Tick down cooldown while player is in range
            if (attackCooldown > 0) {
                attackCooldown -= deltaTime;
                if (attackCooldown < 0) attackCooldown = 0;
            }
        } else {
            // Reset cooldown if player leaves range
            attackCooldown = attackCooldownMax/2;
        }
    }

    public boolean canAttackPlayer(int playerX, int playerY) {
        int dx = Math.abs(x - playerX);
        int dy = Math.abs(y - playerY);
        boolean adjacent = (dx == 1 && dy == 0) || (dx == 0 && dy == 1);

        // Attack only if adjacent AND cooldown expired
        return adjacent && attackCooldown <= 0;
    }

    public void tryAttackPlayer(int playerX, int playerY) {
        if (canAttackPlayer(playerX, playerY)) {
            activateAttackCooldown(); // reset after attack
            facePlayer(playerX, playerY);
            playAttackAnimation();
            System.out.println("[COMBAT] " + type + " attacks player at (" + x + "," + y + ")");
        }
    }


    public void activateAttackCooldown() {
        attackCooldown = attackCooldownMax;
    }

    public void takeDamage(int damage) {
        if (dead) return;

        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            dead = true;
            playDeathAnimation();
            System.out.println("[COMBAT] " + type + " defeated at (" + x + "," + y + ")");
        }
    }

    public void moveTo(int newX, int newY, String direction) {
        this.x = newX;
        this.y = newY;
        this.direction = direction; // update facing direction
        playMoveAnimation();
    }

    public void facePlayer(int playerX, int playerY) {
        int dx = playerX - this.x;
        int dy = playerY - this.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            this.direction = (dx > 0) ? "RIGHT" : "LEFT";
        } else {
            this.direction = (dy > 0) ? "DOWN" : "UP";
        }
    }

    public void trap(int trapDuration) {
        isTrapped = true;
        trapImmobilizationTimer = trapDuration;
        System.out.println("[TRAP] " + type + " is immobilized for " + trapDuration + " seconds");
    }

    public boolean isImmobilized() {
        return isTrapped && trapImmobilizationTimer > 0;
    }


    // --- Abstract Animation Hooks ---
    public abstract void playIdleAnimation();
    public abstract void playMoveAnimation();
    public abstract void playAttackAnimation();
    public abstract void playDeathAnimation();
    public abstract void playPushAnimation(String direction);
    public abstract void playHitAnimation(String direction);

    // --- Accessors ---
    public ImageView getImageView() { return animator.getImageView(); }
    public SpriteAnimator getAnimator() { return animator; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public boolean isDead() { return dead; }
    public EnemyType getType() { return type; }
    public double getMovementCooldown() { return movementCooldown; }
    public double getAttackCooldown() { return attackCooldown; }
    public String getDirection() { return direction; }
    public void setDirection(String dir) { this.direction = dir; }
}
```

## One specific unique enemy class (BoomerGoblin):

```java
package application;

import javafx.scene.image.Image;

public class BoomerGoblin extends Enemy {
    private static Image spriteSheet;

    static {
        try {
            spriteSheet = new Image(BoomerGoblin.class.getResource("/assets/enemies/BoomerGoblin.png").toExternalForm());
        } catch (Exception e) {
            System.out.println("[ERROR] Could not load BoomerGoblin sprite sheet: " + e.getMessage());
        }
    }

    public BoomerGoblin(int x, int y) {
        super(x, y, EnemyType.BOOMER, spriteSheet);
        this.hp = 2;
        this.maxHp = 2;
        this.damage = 2; // High damage on explosion
        this.movementCooldownMax = 1.0; // Speed: medium
        this.movementCooldown = movementCooldownMax;
        playIdleAnimation();
    }

    @Override
    public int[] updateAI(double deltaTime, int playerX, int playerY, Pathfinder pathfinder, int[][] grid) {
        // Update movement cooldown
        if (movementCooldown > 0) {
            movementCooldown -= deltaTime;
            if (movementCooldown < 0) movementCooldown = 0;
        }

        // Only move if cooldown is ready
        if (movementCooldown > 0) {
            return null;
        }

        // BoomerGoblins prioritize getting close to the player for maximum explosion damage
        // They move toward the player using pathfinding
        int[] move = pathfinder.findNextStep(x, y, playerX, playerY, grid);
        
        if (move != null) {
            movementCooldown = movementCooldownMax;
            return move;
        }

        return null;
    }

    @Override
    public void playIdleAnimation() {
        if (getAnimator() != null) {
            getAnimator().playAnimation(0, 0, 1, () -> {}, "idle_" + getDirection().toLowerCase());
        }
    }

    @Override
    public void playMoveAnimation() {
        if (getAnimator() != null) {
            getAnimator().playAnimation(1, 0, 4, () -> {}, "move_" + getDirection().toLowerCase());
        }
    }

    @Override
    public void playAttackAnimation() {
        if (getAnimator() != null) {
            getAnimator().playAnimation(2, 0, 1, () -> {
                // Explosion animation could trigger here
            }, "attack_" + getDirection().toLowerCase());
        }
    }

    @Override
    public void playDeathAnimation() {
        if (getAnimator() != null) {
            getAnimator().playAnimation(3, 0, 1, () -> {
                // Death animation complete callback
            }, "death_" + getDirection().toLowerCase());
        }
    }

    @Override
    public void playPushAnimation(String direction) {
        if (getAnimator() != null) {
            getAnimator().playAnimation(4, 0, 1, () -> {
                // Push animation complete callback
            }, "push_" + direction.toLowerCase());
        }
    }

    @Override
    public void playHitAnimation(String direction) {
        if (getAnimator() != null) {
            getAnimator().playAnimation(5, 0, 1, () -> {
                // Hit animation complete callback
            }, "hit_" + direction.toLowerCase());
        }
    }
}
```

## The Upgrade/Book Manager class:

```java
package application;

import java.util.*;

public class UpgradeManager {
    private int pushRangeLevel;
    private int pushStrengthLevel;
    private int pushAreaLevel;

    // Track active upgrades
    private boolean hasPushRange = false;
    private boolean hasRangePlus = false;
    private boolean hasPushStrengthUpgrade = false;
    private boolean hasPushAreaUpgrade = false;

    public enum UpgradeType {
        PUSH_RANGE,
        PUSH_RANGE_PLUS,
        PUSH_STRENGTH,
        PUSH_AREA,
        SPAWN_TRAPS,
        HEAL
    }

    public static class Upgrade {
        public final UpgradeType type;
        public final String name;
        public final String description;

        public Upgrade(UpgradeType type, String name, String description) {
            this.type = type;
            this.name = name;
            this.description = description;
        }
    }

    public UpgradeManager() {
        this.pushRangeLevel = 0;
        this.pushStrengthLevel = 0;
        this.pushAreaLevel = 0;
    }

    /**
     * Gets 3 random upgrade cards including heal option
     */
    public List<Upgrade> getRandomUpgrades() {
        List<Upgrade> available = new ArrayList<>();

        // Push Range upgrades
        if (!hasPushRange) {
            available.add(new Upgrade(
                UpgradeType.PUSH_RANGE,
                "Push Range I",
                "Touch enemy to push (default)"
            ));
        }

        if (hasPushRange && !hasRangePlus) {
            available.add(new Upgrade(
                UpgradeType.PUSH_RANGE_PLUS,
                "Push Range II",
                "Push enemies from 2+ tiles away"
            ));
        }

        // Push Strength upgrade
        if (!hasPushStrengthUpgrade) {
            available.add(new Upgrade(
                UpgradeType.PUSH_STRENGTH,
                "Push Strength",
                "Enemy flies back 2+ tiles (Brutes always 1 tile)"
            ));
        }

        // Push Area upgrade
        if (!hasPushAreaUpgrade) {
            available.add(new Upgrade(
                UpgradeType.PUSH_AREA,
                "Push Area",
                "Wave Push - 3 tiles in front"
            ));
        }

        // Spawn Traps - always available
        available.add(new Upgrade(
            UpgradeType.SPAWN_TRAPS,
            "Spawn Traps",
            "Instantly spawn 2-3 Bear Traps"
        ));

        // Heal - always available
        available.add(new Upgrade(
            UpgradeType.HEAL,
            "Heal",
            "Restore 1 HP"
        ));

        // Shuffle and pick 3
        Collections.shuffle(available);
        return available.subList(0, Math.min(3, available.size()));
    }

    /**
     * Apply selected upgrade
     */
    public void applyUpgrade(UpgradeType type, Player player, GameLogic logic) {
        switch (type) {
            case PUSH_RANGE:
                hasPushRange = true;
                pushRangeLevel = 1;
                System.out.println("[UPGRADE] Push Range I activated");
                break;

            case PUSH_RANGE_PLUS:
                hasRangePlus = true;
                pushRangeLevel = 2;
                System.out.println("[UPGRADE] Push Range II activated - telekinetic push!");
                break;

            case PUSH_STRENGTH:
                hasPushStrengthUpgrade = true;
                pushStrengthLevel = 1;
                System.out.println("[UPGRADE] Push Strength activated - enemies fly 2+ tiles!");
                break;

            case PUSH_AREA:
                hasPushAreaUpgrade = true;
                pushAreaLevel = 1;
                System.out.println("[UPGRADE] Push Area activated - Wave Push!");
                break;

            case SPAWN_TRAPS:
                logic.spawnBearTraps();
                System.out.println("[UPGRADE] Spawning Bear Traps!");
                break;

            case HEAL:
                player.heal(1);
                System.out.println("[UPGRADE] Player healed 1 HP!");
                break;
        }
    }

    // Getters
    public int getPushRangeLevel() { return pushRangeLevel; }
    public int getPushStrengthLevel() { return pushStrengthLevel; }
    public int getPushAreaLevel() { return pushAreaLevel; }
    public boolean hasPushRange() { return hasPushRange; }
    public boolean hasRangePlus() { return hasRangePlus; }
    public boolean hasPushStrengthUpgrade() { return hasPushStrengthUpgrade; }
    public boolean hasPushAreaUpgrade() { return hasPushAreaUpgrade; }
}
```

# 4. Project Constraints

The project uses JavaFX for the user interface and graphics rendering. The game is built using core Java libraries with no additional external dependencies beyond what's included in a standard JavaFX installation. The grid-based system uses JavaFX's GridPane for layout and rendering, with custom image assets for sprites and icons. The game is designed to run in real-time with frame-based updates and uses JavaFX's event system for input handling.