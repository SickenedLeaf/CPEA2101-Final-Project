package application;

import java.util.*;

/**
 * GameLogic with level-based spawn system and wave progression.
 */
public class GameLogic {
    // Grid dimensions
    public static final int GRID_WIDTH = 20;
    public static final int GRID_HEIGHT = 14;
    
    // Game state
    private Player player;
    private List<Enemy> enemies;
    private int[][] entityGrid = new int[GRID_WIDTH][GRID_HEIGHT];
    private Obstacle[][] obstacleGrid = new Obstacle[GRID_WIDTH][GRID_HEIGHT];
    
    // AI and Spawning systems
    private Pathfinder pathfinder;
    private SpawnSystem spawnSystem;
    private int selectedLevel;
    
    // Event handling
    private Map<String, GameUpdateEvent> pendingEvents = new HashMap<>();
    private Random random = new Random();
    
    // Timing for AI updates
    private long lastUpdateTime;
    
    /**
     * Creates game logic for the specified level.
     */
    public GameLogic(int levelNumber) {
        this.selectedLevel = levelNumber;
        lastUpdateTime = System.currentTimeMillis();
        enemies = new ArrayList<>();
        pathfinder = new Pathfinder();
        
        initializeGameGrid();
        initializeSpawnSystem();
    }
    
    /**
     * Creates a random obstacle.
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
     * Places an obstacle in both grids.
     */
    private void placeObstacle(int x, int y, Obstacle obs) {
        if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
            entityGrid[x][y] = obs.getEntityType();
            obstacleGrid[x][y] = obs;
        }
    }
    
    /**
     * Initializes the game grid.
     */
    private void initializeGameGrid() {
        // Create border walls
        for (int x = 0; x < GRID_WIDTH; x++) {
            placeObstacle(x, 0, new Wall());
            placeObstacle(x, GRID_HEIGHT - 1, new Wall());
        }
        for (int y = 0; y < GRID_HEIGHT; y++) {
            placeObstacle(0, y, new Wall());
            placeObstacle(GRID_WIDTH - 1, y, new Wall());
        }
        
        // Random internal obstacles
        for (int x = 1; x < GRID_WIDTH - 1; x++) {
            for (int y = 1; y < GRID_HEIGHT - 1; y++) {
                if (entityGrid[x][y] == 0 && (x < 8 || x > 11) && (y < 5 || y > 8)) {
                    if (random.nextInt(100) < 12) { // 12% chance
                        placeObstacle(x, y, createRandomObstacle());
                    }
                }
            }
        }
        
        // Initialize player
        player = new Player(9, 6);
        entityGrid[player.getX()][player.getY()] = 0;
        
        System.out.println("[DEBUG] Game grid initialized. Player at (" + player.getX() + "," + player.getY() + ")");
    }
    
    /**
     * Initializes the spawn system.
     */
    private void initializeSpawnSystem() {
        List<SpawnSystem.SpawnPoint> spawnPoints = new ArrayList<>();
        
        // Add spawn points
        spawnPoints.add(new SpawnSystem.SpawnPoint(5, 2));
        spawnPoints.add(new SpawnSystem.SpawnPoint(15, 2));
        spawnPoints.add(new SpawnSystem.SpawnPoint(5, 11));
        spawnPoints.add(new SpawnSystem.SpawnPoint(15, 11));
        spawnPoints.add(new SpawnSystem.SpawnPoint(2, 7));
        spawnPoints.add(new SpawnSystem.SpawnPoint(17, 7));
        spawnPoints.add(new SpawnSystem.SpawnPoint(10, 2));
        spawnPoints.add(new SpawnSystem.SpawnPoint(10, 11));
        
        spawnSystem = new SpawnSystem(spawnPoints, selectedLevel);
        System.out.println("[DEBUG] Spawn system initialized with " + spawnPoints.size() + " spawn points");
    }
    
    /**
     * Main update loop - called every frame.
     */
    public void updateGame() {
        // Calculate delta time
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;
        
        // Cap delta time
        deltaTime = Math.min(deltaTime, 0.1);
        
        // Update player
        player.update(deltaTime);
        
        // Update enemy AI
        updateEnemyAI(deltaTime);
        
        // Update spawn system
        Enemy newEnemy = spawnSystem.update(deltaTime, enemies, entityGrid);
        if (newEnemy != null) {
            enemies.add(newEnemy);
            entityGrid[newEnemy.getX()][newEnemy.getY()] = 2;
            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.ENEMY_SPAWN, 
                    newEnemy.getX(), newEnemy.getY(), newEnemy.getHp()));
        }
        
        // Check player environment damage
        checkPlayerEnvironmentDamage();
    }
    
    /**
     * Updates all enemy AI behavior.
     */
    private void updateEnemyAI(double deltaTime) {
        List<Enemy> enemiesToRemove = new ArrayList<>();
        
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                enemiesToRemove.add(enemy);
                continue;
            }
            
            // Check if adjacent to player (attack)
            if (enemy.canAttackPlayer(player.getX(), player.getY())) {
                player.takeDamage(enemy.getDamage());
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.PLAYER_DAMAGE, 
                        player.getX(), player.getY(), player.getHealth()));
                System.out.println("[COMBAT] " + enemy.getType() + " attacked player!");
                continue;
            }
            
            // Get next move from AI
            int[] move = enemy.updateAI(deltaTime, player.getX(), player.getY(), 
                                       pathfinder, entityGrid);
            
            if (move != null) {
                int targetX = enemy.getX() + move[0];
                int targetY = enemy.getY() + move[1];
                
                // Validate move
                if (targetX >= 0 && targetX < GRID_WIDTH && 
                    targetY >= 0 && targetY < GRID_HEIGHT) {
                    
                    int targetType = entityGrid[targetX][targetY];
                    
                    // Can move to empty space or campfire
                    if (targetType == 0 || targetType == 4) {
                        moveEnemy(enemy, targetX, targetY);
                    }
                }
            }
        }
        
        // Remove dead enemies
        for (Enemy enemy : enemiesToRemove) {
            int x = enemy.getX();
            int y = enemy.getY();
            entityGrid[x][y] = 0;
            spawnSystem.onEnemyDefeated(); // Notify spawn system
            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.REMOVE_ENTITY, x, y));
        }
        enemies.removeAll(enemiesToRemove);
    }
    
    /**
     * Checks if player is on damage tile.
     */
    private void checkPlayerEnvironmentDamage() {
        int px = player.getX();
        int py = player.getY();
        
        Obstacle obstacle = obstacleGrid[px][py];
        if (obstacle != null && obstacle.isPassable() && obstacle.getPassDamage() > 0) {
            player.takeDamage(obstacle.getPassDamage());
            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.PLAYER_DAMAGE, px, py, player.getHealth()));
            System.out.println("[HAZARD] Player took " + obstacle.getPassDamage() + 
                             " damage from " + obstacle.getClass().getSimpleName());
        }
    }
    
    /**
     * Moves an enemy to a new position.
     */
    private void moveEnemy(Enemy enemy, int newX, int newY) {
        int oldX = enemy.getX();
        int oldY = enemy.getY();
        
        // Clear old position
        entityGrid[oldX][oldY] = 0;
        
        // Move enemy
        enemy.moveTo(newX, newY);
        entityGrid[newX][newY] = 2;
        
        addEvent(new GameUpdateEvent(GameUpdateEvent.Type.ENEMY_MOVE, 
                oldX, oldY, newX, newY, enemy.getHp()));
        
        // Check campfire damage
        Obstacle obs = obstacleGrid[newX][newY];
        if (obs != null && obs.isPassable() && obs.getPassDamage() > 0) {
            enemy.takeDamage(obs.getPassDamage());
            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.DAMAGE, newX, newY, enemy.getHp()));
            System.out.println("[HAZARD] Enemy took " + obs.getPassDamage() + " damage from campfire");
        }
    }
    
    /**
     * Checks if a tile is an obstacle.
     */
    private boolean isObstacle(int type) {
        return type == 1 || type == 3 || type == 4;
    }
    
    /**
     * Player attempts to move or push.
     */
    public boolean attemptMove(int dirX, int dirY) {
        if (!player.isAlive()) {
            System.out.println("[DEBUG] Player is dead, cannot move");
            return false;
        }
        
        int targetX = player.getX() + dirX;
        int targetY = player.getY() + dirY;
        
        // Check bounds
        if (targetX < 0 || targetX >= GRID_WIDTH || targetY < 0 || targetY >= GRID_HEIGHT) {
            return false;
        }
        
        int targetType = entityGrid[targetX][targetY];
        boolean actionTaken = false;
        
        // Empty tile or passable obstacle
        if (targetType == 0 || (isObstacle(targetType) && obstacleGrid[targetX][targetY].isPassable())) {
            movePlayer(targetX, targetY);
            actionTaken = true;
        }
        // Impassable obstacle
        else if (isObstacle(targetType) && !obstacleGrid[targetX][targetY].isPassable()) {
            System.out.println("[DEBUG] Player blocked by obstacle");
            actionTaken = false;
        }
        // Enemy - push logic
        else if (targetType == 2) {
            int pushX = targetX + dirX;
            int pushY = targetY + dirY;
            
            if (pushX >= 0 && pushX < GRID_WIDTH && pushY >= 0 && pushY < GRID_HEIGHT) {
                int pushTargetType = entityGrid[pushX][pushY];
                
                // Push into empty or campfire
                if (pushTargetType == 0 || pushTargetType == 4) {
                    Enemy enemyToPush = findEnemyAt(targetX, targetY);
                    if (enemyToPush != null) {
                        moveEnemy(enemyToPush, pushX, pushY);
                        actionTaken = true;
                    }
                }
                // Push into obstacle
                else if (isObstacle(pushTargetType)) {
                    Obstacle obstacle = obstacleGrid[pushX][pushY];
                    Enemy enemyToPush = findEnemyAt(targetX, targetY);
                    
                    if (enemyToPush != null) {
                        if (obstacle.isPassable()) {
                            moveEnemy(enemyToPush, pushX, pushY);
                        } else {
                            // Collision damage
                            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, pushX, pushY));
                            enemyToPush.takeDamage(obstacle.getCollisionDamage());
                            addEvent(new GameUpdateEvent(GameUpdateEvent.Type.DAMAGE, 
                                    targetX, targetY, enemyToPush.getHp()));
                        }
                        actionTaken = true;
                    }
                }
                // Push into another enemy
                else if (pushTargetType == 2) {
                    attemptEntityPush(pushX, pushY, dirX, dirY);
                    actionTaken = true;
                }
            }
        }
        
        return actionTaken;
    }
    
    /**
     * Recursive push for chain pushing.
     */
    private boolean attemptEntityPush(int entityX, int entityY, int dirX, int dirY) {
        int nextX = entityX + dirX;
        int nextY = entityY + dirY;
        
        if (nextX < 0 || nextX >= GRID_WIDTH || nextY < 0 || nextY >= GRID_HEIGHT) {
            return false;
        }
        
        int nextType = entityGrid[nextX][nextY];
        Enemy enemy = findEnemyAt(entityX, entityY);
        if (enemy == null) return false;
        
        if (nextType == 0 || nextType == 4) {
            moveEnemy(enemy, nextX, nextY);
            return true;
        } else if (isObstacle(nextType)) {
            Obstacle obstacle = obstacleGrid[nextX][nextY];
            if (obstacle.isPassable()) {
                moveEnemy(enemy, nextX, nextY);
                return true;
            } else {
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.IMPACT, nextX, nextY));
                enemy.takeDamage(obstacle.getCollisionDamage());
                addEvent(new GameUpdateEvent(GameUpdateEvent.Type.DAMAGE, entityX, entityY, enemy.getHp()));
                return false;
            }
        } else if (nextType == 2) {
            return attemptEntityPush(nextX, nextY, dirX, dirY);
        }
        
        return false;
    }
    
    /**
     * Finds an enemy at the specified position (public for health bar updates).
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
     * Moves the player.
     */
    private void movePlayer(int newX, int newY) {
        int oldX = player.getX();
        int oldY = player.getY();
        
        player.moveTo(newX, newY);
        addEvent(new GameUpdateEvent(GameUpdateEvent.Type.PLAYER_MOVE, oldX, oldY, newX, newY));
    }
    
    /**
     * Adds an event.
     */
    private void addEvent(GameUpdateEvent event) {
        String key = event.type.name() + event.newX + "," + event.newY;
        
        if (event.type == GameUpdateEvent.Type.IMPACT) {
            key += "_" + System.nanoTime();
        }
        
        pendingEvents.put(key, event);
    }
    
    /**
     * Flushes pending events.
     */
    public List<GameUpdateEvent> flushEvents() {
        List<GameUpdateEvent> events = new ArrayList<>(pendingEvents.values());
        pendingEvents.clear();
        
        events.sort((a, b) -> Integer.compare(priority(a.type), priority(b.type)));
        return events;
    }
    
    private int priority(GameUpdateEvent.Type type) {
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
    public int getEntityAt(int x, int y) { return entityGrid[x][y]; }
    public int getEnemyHealthAt(int x, int y) {
        Enemy enemy = findEnemyAt(x, y);
        return (enemy != null) ? enemy.getHp() : 0;
    }
    public int getPlayerX() { return player.getX(); }
    public int getPlayerY() { return player.getY(); }
    public Player getPlayer() { return player; }
    public List<Enemy> getEnemies() { return enemies; }
    public SpawnSystem getSpawnSystem() { return spawnSystem; }
    
    public boolean isLevelComplete() {
        return spawnSystem.isLevelComplete();
    }
    
    /**
     * Game update event structure.
     */
    public static class GameUpdateEvent {
        public enum Type {
            PLAYER_MOVE, PLAYER_DAMAGE, ENEMY_MOVE, ENEMY_SPAWN, 
            REMOVE_ENTITY, DAMAGE, IMPACT
        }
        
        public final Type type;
        public final int oldX, oldY;
        public final int newX, newY;
        public final int value;
        
        public GameUpdateEvent(Type type, int oldX, int oldY, int newX, int newY) {
            this.type = type;
            this.oldX = oldX;
            this.oldY = oldY;
            this.newX = newX;
            this.newY = newY;
            this.value = 0;
        }
        
        public GameUpdateEvent(Type type, int x, int y) {
            this(type, x, y, x, y);
        }
        
        public GameUpdateEvent(Type type, int x, int y, int health) {
            this.type = type;
            this.oldX = x;
            this.oldY = y;
            this.newX = x;
            this.newY = y;
            this.value = health;
        }
        
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