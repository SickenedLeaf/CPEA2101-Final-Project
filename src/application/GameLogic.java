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
                if (x >= 8 && x <= 11 && y >= 5 && y <= 8) continue;
                
                if (entityGrid[x][y] == 0 && random.nextInt(100) < 12) {
                    placeObstacle(x, y, createRandomObstacle());
                }
            }
        }
        
        // Initialize player at center
        player = new Player(3, 5);
        
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
        
        // Add spawn points around edges
        spawnPoints.add(new SpawnSystem.SpawnPoint(5, 2));
        spawnPoints.add(new SpawnSystem.SpawnPoint(7, 2));
        spawnPoints.add(new SpawnSystem.SpawnPoint(5, 3));
        spawnPoints.add(new SpawnSystem.SpawnPoint(7, 2));
        spawnPoints.add(new SpawnSystem.SpawnPoint(2, 4));
        spawnPoints.add(new SpawnSystem.SpawnPoint(5, 4));
        spawnPoints.add(new SpawnSystem.SpawnPoint(7, 2));
        spawnPoints.add(new SpawnSystem.SpawnPoint(7, 3));
        
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
        
        // Update player
        player.update(deltaTime);
        
        // Update traps
        for (Trap trap : traps) {
            trap.update(deltaTime);
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
        
        // Check player environment damage
        checkEnvironmentDamage();
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
            enemy.updateCooldowns(deltaTime);
            
            // Check if can attack player (with cooldown check)
            if (enemy.canAttackPlayer(player.getX(), player.getY())) {
                player.takeDamage(enemy.getDamage());
                enemy.activateAttackCooldown(); // Set cooldown regardless of hit
                
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
                    
                    // Move to empty or passable obstacle
                    if (targetType == 0 || targetType == 4) {
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
        int explosionDamage = boomer.getExplosionDamage();
        
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
                trap.trigger();
                addEvent(new GameUpdateEvent(
                    GameUpdateEvent.Type.DAMAGE,
                    x, y, enemy.getHp()
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
        
        // Empty tile or passable obstacle
        if (targetType == 0 || (isObstacleType(targetType) && 
            obstacleGrid[targetX][targetY].isPassable())) {
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
     * Player performs push action in all adjacent directions.
     */
    public boolean attemptPushAction() {
        if (!player.isAlive() || !player.canPush()) return false;
        
        boolean pushedAny = false;
        int px = player.getX();
        int py = player.getY();
        
        // Check all 8 directions around player
        int[][] directions = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1, 0},           {1, 0},
            {-1, 1},  {0, 1},  {1, 1}
        };
        
        for (int[] dir : directions) {
            int enemyX = px + dir[0];
            int enemyY = py + dir[1];
            
            if (!isValidPosition(enemyX, enemyY)) continue;
            
            if (entityGrid[enemyX][enemyY] == 2) {
                int pushX = enemyX + dir[0];
                int pushY = enemyY + dir[1];
                
                if (isValidPosition(pushX, pushY)) {
                    Enemy enemy = findEnemyAt(enemyX, enemyY);
                    if (enemy != null) {
                        int pushType = entityGrid[pushX][pushY];
                        
                        // Push into empty or campfire
                        if (pushType == 0 || pushType == 4) {
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
        
        if (pushedAny) {
            player.activatePushCooldown();
            System.out.println("[PUSH] Player used push action!");
        }
        
        return pushedAny;
    }
    
    /**
     * Handles push mechanics.
     */
    private boolean handlePush(int enemyX, int enemyY, int dirX, int dirY) {
        int pushX = enemyX + dirX;
        int pushY = enemyY + dirY;
        
        if (!isValidPosition(pushX, pushY)) return false;
        
        Enemy enemy = findEnemyAt(enemyX, enemyY);
        if (enemy == null) return false;
        
        int pushType = entityGrid[pushX][pushY];
        
        // Push into empty or campfire
        if (pushType == 0 || pushType == 4) {
            moveEnemy(enemy, pushX, pushY);
            return true;
        }
        
        // Push into obstacle
        if (isObstacleType(pushType)) {
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
        int nextX = entityX + dirX;
        int nextY = entityY + dirY;
        
        if (!isValidPosition(nextX, nextY)) return false;
        
        Enemy enemy = findEnemyAt(entityX, entityY);
        if (enemy == null) return false;
        
        int nextType = entityGrid[nextX][nextY];
        
        if (nextType == 0 || nextType == 4) {
            moveEnemy(enemy, nextX, nextY);
            return true;
        } else if (isObstacleType(nextType)) {
            Obstacle obs = obstacleGrid[nextX][nextY];
            if (obs.isPassable()) {
                moveEnemy(enemy, nextX, nextY);
                return true;
            } else {
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
                enemy.takeDamage(obs.getCollisionDamage());
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
}