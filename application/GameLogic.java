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
            entityGrid[newEnemy.getX()][newEnemy.getY()] = 2;
            addEvent(new GameUpdateEvent(
                GameUpdateEvent.Type.ENEMY_SPAWN,
                newEnemy.getX(), newEnemy.getY(), newEnemy.getHp()
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

            // Don't move if immobilized by trap
            if (move != null && !enemy.isImmobilized()) {
                int targetX = enemy.getX() + move[0];
                int targetY = enemy.getY() + move[1];

                // Validate and execute move
                if (isValidPosition(targetX, targetY)) {
                    int targetType = entityGrid[targetX][targetY];

                    // Prevent enemies from moving into the player’s tile
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
            range += 2; // With Push Range II, can push from 2 extra  tiles away
        } else if (upgradeManager.hasPushRange()) {
            range += 1; // Push Range I allows push from 1 extra tile away
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
                        
                        int borderPushX = enemyX + pushDirectionX;
                        int borderPushY = enemyY + pushDirectionY;

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
                        } else if (isValidPosition(borderPushX, borderPushY)){
                        	Enemy enemy = findEnemyAt(enemyX, enemyY);
                            if (enemy != null) {
                                int pushType = entityGrid[borderPushX][borderPushX];

                                // Push into obstacle
                                if (isObstacleType(pushType)) {
                                    Obstacle obs = obstacleGrid[borderPushX][borderPushX];
                                    
                                        // Collision damage
                                        addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX-1, pushY-1));
                                        enemy.takeDamage(obs.getCollisionDamage());
                                        addEvent(new GameUpdateEvent(
                                            GameUpdateEvent.Type.DAMAGE,
                                            enemyX, enemyY, enemy.getHp()
                                        ));
                                        System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() +
                                                         " collision damage!");
                                    
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
        // ... existing initialization ...
        Enemy enemy = findEnemyAt(enemyX, enemyY);
        if (enemy == null) return false;

        int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
        // Brutes only push 1 tile regardless of upgrade
        if (enemy.getType() == Enemy.EnemyType.BRUTE) {
            pushDistance = 1; 
        }
        
        int finalPushX = enemyX;
        int finalPushY = enemyY;
        
        player.playAttackAnimation();
        
        // Iterate through the path one tile at a time
        for (int i = 1; i <= pushDistance; i++) {
            int nextX = enemyX + (dirX * i);
            int nextY = enemyY + (dirY * i);

            if (!isValidPosition(nextX, nextY)) break; // Stop at grid edge

            int nextType = entityGrid[nextX][nextY];
            
            // Impassable obstacle (Wall) or Player
            if (nextType == 1 || nextType == 3 || (nextX == player.getX() && nextY == player.getY())) {
                
                // Check if the collision is a Wall
                if (isObstacleType(nextType) && !obstacleGrid[nextX][nextY].isPassable()) {
                    // Collision damage and STOP.
                    addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
                    enemy.takeDamage(obstacleGrid[nextX][nextY].getCollisionDamage());
                    enemy.playHitAnimation(player.getDirection());
                    
                    
                    // If finalPushX/Y are the enemy's starting position, no move is needed.
                    if (finalPushX != enemyX || finalPushY != enemyY) {
                        moveEnemy(enemy, finalPushX, finalPushY);
                    }

                    addEvent(new GameUpdateEvent(GameUpdateEvent.Type.DAMAGE, enemyX, enemyY, enemy.getHp()));
                    return true;
                }
                
                break; 
            }
            if (nextType == 2) {
                boolean chainPushed = attemptChainPush(nextX, nextY, dirX, dirY);
                
                if (chainPushed) {
                    // The target enemy was successfully moved.
                    // The current enemy (enemyX/Y) should now move onto the target enemy's old tile (nextX/Y)
                    finalPushX = nextX;
                    finalPushY = nextY;
                    break; // Stop the push here and perform the move below
                } else {

                    break; // Stop the push here
                }
            }

            // If the spot is clear (Empty, Campfire/Passable Obstacle, Trap, or Book),
            // we record it as the potential final destination.
            finalPushX = nextX;
            finalPushY = nextY;
        }

        // Only move if the enemy has a clear path and a final destination greater than its start position.
        if (finalPushX != enemyX || finalPushY != enemyY) {
            moveEnemy(enemy, finalPushX, finalPushY);
            enemy.playPushAnimation(player.getDirection());
            AudioManager.getInstance().playPlayerSound("PUSH");
            return true;
        }
        
        // If no collision and no movement, return false
        return false;
    }
    
    /**
     * Handles chain pushing, checking path step-by-step.
     */
    private boolean attemptChainPush(int entityX, int entityY, int dirX, int dirY) {
        // Calculate push distance based on upgrade
        int pushDistance = 1 + upgradeManager.getPushStrengthLevel();
        Enemy enemy = findEnemyAt(entityX, entityY);
        if (enemy == null) return false;

        if (enemy.getType() == Enemy.EnemyType.BRUTE) {
            pushDistance = 1; // Brutes always pushed 1 tile
        }

        int finalPushX = entityX;
        int finalPushY = entityY;
        
        // Iterate through the path one tile at a time, up to the max push distance
        for (int i = 1; i <= pushDistance; i++) {
            int nextX = entityX + (dirX * i);
            int nextY = entityY + (dirY * i);
            
            if (!isValidPosition(nextX, nextY)) break; 

            int nextType = entityGrid[nextX][nextY];
            
            // --- Collision Check (Impassable Obstacle) ---
            if (isObstacleType(nextType) && !obstacleGrid[nextX][nextY].isPassable()) {
                // Hit a Wall: apply collision damage and stop the chain
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
                enemy.takeDamage(obstacleGrid[nextX][nextY].getCollisionDamage());
                enemy.playHitAnimation(player.getDirection());
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE, entityX, entityY, enemy.getHp()
                ));
                
                // The enemy moves to the last valid spot (finalPushX/Y) if it moved at all.
                if (finalPushX != entityX || finalPushY != entityY) {
                    moveEnemy(enemy, finalPushX, finalPushY);
                }
                return false; // Chain push failed (ended in collision)
            } 
            
            // --- Enemy-to-Enemy Chain Check ---
            if (nextType == 2) {
                // Hit another enemy: recursively attempt to push it
                boolean chainPushed = attemptChainPush(nextX, nextY, dirX, dirY);

                if (chainPushed) {
                    finalPushX = nextX;
                    finalPushY = nextY;
                } else {
                    break; // Stop the push here
                }
            }
            
            // --- Passable Tile (Empty, Campfire, Trap, Book) ---
            if (nextType == 0 || nextType == 4 || nextType == 6 || (isObstacleType(nextType) && obstacleGrid[nextX][nextY].isPassable())) {
                finalPushX = nextX;
                finalPushY = nextY;
            } 
        }

        // Move the enemy to the final calculated clear spot.
        if (finalPushX != entityX || finalPushY != entityY) {
            moveEnemy(enemy, finalPushX, finalPushY);
            enemy.playPushAnimation(player.getDirection());
            AudioManager.getInstance().playPlayerSound("PUSH");
            return true; // Push successful
        }

        return false; // Push failed (didn't move or collision stopped it)
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