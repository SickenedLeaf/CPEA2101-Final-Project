package application;

import java.util.*;

/**
 * Core game logic with integrated spawn system, enemy AI, push mechanics, and upgrade system.
 */
public class GameLogic {
    // Grid dimensions
    public static final int GRID_WIDTH = 14;
    public static final int GRID_HEIGHT = 9;
    
    // Game entities
    private Player player;
    private List<Enemy> enemies;
    private List<Trap> traps;
    private Book currentBook;
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
        
        // Calculate center position
        int centerX = GRID_WIDTH / 2;
        int centerY = GRID_HEIGHT / 2;
        int centerClearance = 2;
        
        // Random interior obstacles
        for (int x = 1; x < GRID_WIDTH - 1; x++) {
            for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                // Skip center area for player spawn
                int dx = Math.abs(x - centerX);
                int dy = Math.abs(y - centerY);
                if (dx <= centerClearance && dy <= centerClearance) continue;
                
                if (entityGrid[x][y] == 0 && random.nextInt(100) < 12) {
                    placeObstacle(x, y, createRandomObstacle());
                }
            }
        }
        
        // Initialize player at center
        player = new Player(centerX, centerY);
        
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
     * Initializes spawn system with edge spawn points.
     */
    private void initializeSpawnSystem(int levelNumber) {
        List<SpawnSystem.SpawnPoint> spawnPoints = new ArrayList<>();
        
        // Top edge (excluding corners)
        for (int x = 2; x < GRID_WIDTH - 2; x++) {
            spawnPoints.add(new SpawnSystem.SpawnPoint(x, 1));
        }
        
        // Bottom edge (excluding corners)
        for (int x = 2; x < GRID_WIDTH - 2; x++) {
            spawnPoints.add(new SpawnSystem.SpawnPoint(x, GRID_HEIGHT - 2));
        }
        
        // Left edge (excluding corners and top/bottom)
        for (int y = 2; y < GRID_HEIGHT - 2; y++) {
            spawnPoints.add(new SpawnSystem.SpawnPoint(1, y));
        }
        
        // Right edge (excluding corners and top/bottom)
        for (int y = 2; y < GRID_HEIGHT - 2; y++) {
            spawnPoints.add(new SpawnSystem.SpawnPoint(GRID_WIDTH - 2, y));
        }
        
        System.out.println("[INIT] Created " + spawnPoints.size() + " spawn points around edges");
        
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
        if (isPaused || waitingForUpgrade) return;
        
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;
        deltaTime = Math.min(deltaTime, 0.1);
        
        // Update player
        player.update(deltaTime);
        
        // Update traps
        for (Trap trap : traps) {
            trap.update(deltaTime);
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
        List<int[]> validPositions = new ArrayList<>();
        
        // Find valid positions (empty tiles within reasonable distance)
        for (int x = 1; x < GRID_WIDTH - 1; x++) {
            for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                if (entityGrid[x][y] == 0) {
                    int dist = Math.abs(x - player.getX()) + Math.abs(y - player.getY());
                    if (dist >= 2 && dist <= 6) {
                        validPositions.add(new int[]{x, y});
                    }
                }
            }
        }
        
        if (!validPositions.isEmpty()) {
            int[] pos = validPositions.get(random.nextInt(validPositions.size()));
            currentBook = new Book(pos[0], pos[1]);
            entityGrid[pos[0]][pos[1]] = 5; // Book entity type
            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.BOOK_SPAWN, pos[0], pos[1]));
            System.out.println("[BOOK] Book spawned at (" + pos[0] + "," + pos[1] + ")");
        }
    }

    /**
     * Spawns 2-3 bear traps on random unoccupied tiles.
     */
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
            if (enemy.canAttackPlayer(player.getX(), player.getY())) {
                player.takeDamage(enemy.getDamage());
                enemy.activateAttackCooldown();
                
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
            
            if (move != null) {
                int targetX = enemy.getX() + move[0];
                int targetY = enemy.getY() + move[1];
                
                // Validate and execute move
                if (isValidPosition(targetX, targetY)) {
                    int targetType = entityGrid[targetX][targetY];

                    // Prevent enemies from moving into the player's tile
                    if (targetX == player.getX() && targetY == player.getY()) {
                        continue;
                    }

                    // Move to empty tile
                    if (targetType == 0) {
                        moveEnemy(enemy, targetX, targetY);
                    }
                    // Check for trap collision
                    else if (targetType == 6) {
                        // Move into trap and trigger it
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
        
        // Handle Boomer Goblin explosion
        if (enemy instanceof BoomerGoblin) {
            BoomerGoblin boomer = (BoomerGoblin) enemy;
            handleExplosion(boomer);
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
        
        // Damage adjacent entities
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                
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

        // Check what's at the new position before marking as enemy
        int existingType = entityGrid[newX][newY];
        
        // If moving into trap, trigger it
        if (existingType == 6) {
            checkTrapCollision(enemy, newX, newY);
        }
        
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
    }
    
    /**
     * Checks trap collision for enemy.
     */
    private void checkTrapCollision(Enemy enemy, int x, int y) {
        for (Trap trap : traps) {
            if (trap.getX() == x && trap.getY() == y && trap.isActive()) {
                enemy.takeDamage(trap.getDamage());
                trap.trigger();
                entityGrid[x][y] = 0; // Remove trap from grid
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE,
                    x, y, enemy.getHp()
                ));
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.TRAP_TRIGGER, x, y));
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
        
        // Empty tile or passable obstacle or book
        if (targetType == 0 || targetType == 5 || 
            (isObstacleType(targetType) && obstacleGrid[targetX][targetY].isPassable())) {
            movePlayer(targetX, targetY);
            return true;
        }
        
        // Impassable obstacle
        if (isObstacleType(targetType) && !obstacleGrid[targetX][targetY].isPassable()) {
            return false;
        }
        
        // Enemy - push logic
        if (targetType == 2) {
            return handlePush(targetX, targetY, dirX, dirY);
        }
        
        return false;
    }

    /**
     * Player performs push action based on upgrades.
     */
    public boolean attemptPushAction() {
        if (!player.isAlive() || !player.canPush()) return false;
        
        boolean pushedAny = false;
        int px = player.getX();
        int py = player.getY();
        
        // Determine push targets based on upgrades
        List<int[]> pushTargets = new ArrayList<>();
        
        if (upgradeManager.hasPushAreaUpgrade()) {
            // Wave Push - 3 tiles in front based on direction
            pushTargets = getWavePushTargets(px, py, player.getDirection());
        } else {
            // Standard 8-direction push
            pushTargets = getAdjacentTargets(px, py);
        }
        
        // Push each target
        for (int[] target : pushTargets) {
            int enemyX = target[0];
            int enemyY = target[1];
            
            if (!isValidPosition(enemyX, enemyY)) continue;
            
            if (entityGrid[enemyX][enemyY] == 2) {
                Enemy enemy = findEnemyAt(enemyX, enemyY);
                if (enemy != null) {
                    pushedAny |= pushEnemy(enemy, enemyX, enemyY, px, py);
                }
            }
        }
        
        if (pushedAny) {
            player.activatePushCooldown();
            System.out.println("[PUSH] Player used push action!");
        }
        
        return pushedAny;
    }
    
    /**
     * Gets wave push targets (3 tiles in front).
     */
    private List<int[]> getWavePushTargets(int px, int py, String direction) {
        List<int[]> targets = new ArrayList<>();
        
        if ("RIGHT".equals(direction)) {
            targets.add(new int[]{px + 1, py - 1});
            targets.add(new int[]{px + 1, py});
            targets.add(new int[]{px + 1, py + 1});
        } else if ("LEFT".equals(direction)) {
            targets.add(new int[]{px - 1, py - 1});
            targets.add(new int[]{px - 1, py});
            targets.add(new int[]{px - 1, py + 1});
        } else if ("DOWN".equals(direction)) {
            targets.add(new int[]{px - 1, py + 1});
            targets.add(new int[]{px, py + 1});
            targets.add(new int[]{px + 1, py + 1});
        } else if ("UP".equals(direction)) {
            targets.add(new int[]{px - 1, py - 1});
            targets.add(new int[]{px, py - 1});
            targets.add(new int[]{px + 1, py - 1});
        }
        
        return targets;
    }
    
    /**
     * Gets adjacent targets (8 directions).
     */
    private List<int[]> getAdjacentTargets(int px, int py) {
        List<int[]> targets = new ArrayList<>();
        int[][] directions = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1, 0},           {1, 0},
            {-1, 1},  {0, 1},  {1, 1}
        };
        
        for (int[] dir : directions) {
            targets.add(new int[]{px + dir[0], py + dir[1]});
        }
        
        return targets;
    }
    
    /**
     * Pushes an enemy based on push strength upgrades.
     */
    private boolean pushEnemy(Enemy enemy, int enemyX, int enemyY, int playerX, int playerY) {
        int dirX = Integer.compare(enemyX, playerX);
        int dirY = Integer.compare(enemyY, playerY);
        
        // Determine push distance
        int pushDistance = 1;
        if (upgradeManager.hasPushStrengthUpgrade()) {
            // Brutes always pushed only 1 tile
            if (enemy.getType() != Enemy.EnemyType.BRUTE) {
                pushDistance = 2;
            }
        }
        
        // Calculate final push position
        int finalX = enemyX;
        int finalY = enemyY;
        
        for (int dist = 1; dist <= pushDistance; dist++) {
            int nextX = enemyX + (dirX * dist);
            int nextY = enemyY + (dirY * dist);
            
            if (!isValidPosition(nextX, nextY)) break;
            
            int targetType = entityGrid[nextX][nextY];
            
            if (targetType == 0 || targetType == 4 || targetType == 6) {
                finalX = nextX;
                finalY = nextY;
            } else if (isObstacleType(targetType)) {
                Obstacle obs = obstacleGrid[nextX][nextY];
                if (!obs.isPassable()) {
                    // Hit obstacle - collision damage
                    addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
                    enemy.takeDamage(obs.getCollisionDamage());
                    addEvent(new GameUpdateEvent(
                        GameUpdateEvent.Type.DAMAGE,
                        enemyX, enemyY, enemy.getHp()
                    ));
                    System.out.println("[COLLISION] Enemy took " + obs.getCollisionDamage() + 
                                     " collision damage!");
                    return true;
                }
            } else if (targetType == 2) {
                // Hit another enemy
                break;
            }
        }
        
        // Move enemy to final position
        if (finalX != enemyX || finalY != enemyY) {
            moveEnemy(enemy, finalX, finalY);
            return true;
        }
        
        return false;
    }
    
    /**
     * Handles push mechanics when walking into enemy.
     */
    private boolean handlePush(int enemyX, int enemyY, int dirX, int dirY) {
        Enemy enemy = findEnemyAt(enemyX, enemyY);
        if (enemy == null) return false;
        
        return pushEnemy(enemy, enemyX, enemyY, player.getX(), player.getY());
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
            case BOOK_SPAWN: return 0;
            case TRAP_SPAWN: return 0;
            case ENEMY_SPAWN: return 1;
            case ENEMY_MOVE: return 2;
            case DAMAGE: return 3;
            case TRAP_TRIGGER: return 4;
            case REMOVE_ENTITY: return 5;
            case IMPACT: return 6;
            case PLAYER_DAMAGE: return 7;
            case PLAYER_MOVE: return 8;
            case BOOK_COLLECTED: return 9;
            default: return 99;
        }
    }
    
    // Pause/Resume
    public void pauseGame() { isPaused = true; }
    public void resumeGame() { 
        isPaused = false; 
        waitingForUpgrade = false;
        lastUpdateTime = System.currentTimeMillis();
    }
    
    // Getters
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public SpawnSystem getSpawnSystem() { return spawnSystem; }
    public UpgradeManager getUpgradeManager() { return upgradeManager; }
    public Book getCurrentBook() { return currentBook; }
    public int getEntityAt(int x, int y) { return entityGrid[x][y]; }
    public int getEnemyHealthAt(int x, int y) {
        Enemy enemy = findEnemyAt(x, y);
        return enemy != null ? enemy.getHp() : 0;
    }
    public boolean isLevelComplete() {
        return spawnSystem.isLevelComplete();
    }
    public boolean isWaitingForUpgrade() { return waitingForUpgrade; }
    
    public void clearBook() {
        if (currentBook != null) {
            entityGrid[currentBook.getX()][currentBook.getY()] = 0;
            currentBook = null;
        }
    }
}