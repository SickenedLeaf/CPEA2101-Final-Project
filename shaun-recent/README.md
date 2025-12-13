# Push Knight Peril

## Project Overview
Push Knight Peril is a Java/JavaFX grid-based survival game where players control a knight who must survive waves of enemies using tactical positioning and push mechanics. The player pushes enemies into traps and obstacles while managing movement cooldowns and navigating through levels with different challenges.

## Technology Stack
- **Language**: Java 21
- **GUI Framework**: JavaFX 21
- **Game Loop**: JavaFX AnimationTimer
- **Graphics**: JavaFX Scene Graph with GridPane and Sprite Animation
- **Game Logic**: Grid-based movement (14x9 grid) with push mechanics
- **Assets**: Sprite sheets and tile images with animation system

## Core Systems

### Grid-Based Movement System
- **Grid Dimensions**: 14x9 grid with 72x72 pixel tiles
- **Player Movement**: WASD or arrow keys to move and push adjacent enemies
- **Push Mechanics**: Spacebar triggers area push that affects all adjacent enemies
- **Cooldown System**: Movement has 150ms minimum interval, push has configurable cooldown

### Enemy System
The game features multiple enemy types with distinct behaviors:

#### Enemy Types
- **Goblin**: Fast enemy with 2 HP, deals 1 damage
- **Skeleton**: Balanced enemy with 3 HP, deals 1 damage
- **SkeletonBrute**: Tanky enemy with 6 HP, slower movement, deals 2 damage
- **BoomerGoblin**: Explosive enemy with 1 HP, deals 2 damage in explosion radius

#### Enemy Properties
- All enemies inherit from `Enemy` base class with health, damage and AI systems
- Each enemy type has distinct AI patterns and animation states
- Enemies use A* pathfinding to pursue player
- Attack cooldown system prevents constant damage

### Spawning System
- **SpawnSystem**: Manages wave-based enemy spawning with configurable difficulty
- **Wave Progression**: Multiple levels with different wave counts and spawn rates
- **Endless Mode**: Infinite survival mode with increasing difficulty
- **Spawn Points**: Fixed positions around grid edges for enemy spawning

### Upgrade System
- **UpgradeManager**: Handles player progression with upgrade levels
- **Upgrade Types**: Health, push range, and push cooldown improvements
- **Currency-based**: Upgrades require in-game currency collected from defeated enemies
- **Level Cap**: Each upgrade has maximum level limits

### Animation System
- **SpriteAnimator**: Frame-based animation system using sprite sheets
- **Directional Animations**: Different animations for movement directions (up, down, left, right)
- **State-based Animations**: Idle, move, attack, and death animations for all characters
- **Visual Effects**: Damage flashes, impact effects, and flickering effects

## Development Guidelines

### Code Organization
```
src/application/
  - Main.java (Application entry point)
  - GameLauncher.java (JavaFX application controller)
  - GameLogic.java (Core game state management)
  - GamePanel.java (Rendering and UI)
  - InputHandler.java (Keyboard input management)
  - Player.java (Player entity)
  - Enemy.java (Abstract enemy class)
  - {EnemyType}.java (Specific enemy implementations)
  - {GameSystem}.java (SpawnSystem, Pathfinder, UpgradeManager, etc.)
  - SpriteAnimator.java (Animation system)
  - Constants.java (Game configuration values)

assets/ (in resources)
  /tiles (Background tiles)
  /obstacles (Obstacle sprites)
  /enemies (Enemy sprite sheets)
  /player (Player sprite sheet)
```

### Key Implementation Features

#### Grid-Based Pathfinding
- A* algorithm implementation for enemy AI navigation
- Obstacle awareness with passable (spikes, campfire) and impassable (walls) obstacles
- Direction tracking for proper animation orientation

#### Visual Presentation
- Grid-based layout using JavaFX GridPane
- Animated sprites with 72x72 pixel frames
- Visual feedback for damage, impacts, and invulnerability frames
- Top and bottom UI bars with wave information and health display

#### Wave-Based Gameplay
- Multiple level options with different difficulty curves
- Endless survival mode with scaling difficulty
- Wave completion detection and progression
- Spawn system with configurable enemy composition

### Testing Priorities
1. Movement feel and push mechanics responsiveness
2. Enemy AI pathfinding and attack systems
3. Animation system performance and visual quality
4. Wave progression and difficulty scaling
5. UI feedback and player experience

## Technical Requirements

### JavaFX Implementation Approach

#### Game Loop
Use `AnimationTimer` for the main game loop:
```java
new AnimationTimer() {
    @Override
    public void handle(long now) {
        update(now);
        render();
    }
}.start();
```

#### Rendering Options
**Grid-based Scene Graph (Current Implementation)**
- Use GridPane with StackPane tiles
- ImageView for sprites and animations
- Visual effects as overlay nodes
- UI elements as separate layout panes

#### Input Handling
```java
scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
```

### JavaFX-Specific Considerations
- Use `AnimationTimer` for game updates and rendering
- Leverage `SpriteAnimator` for frame-based animations
- Use StackPane for layered tile content (floor, obstacles, entities)
- Bind properties for reactive UI updates

### Asset Loading
**Resources folder is in src/main/resources**
```java
// For resources folder at src/main/resources
Image sprite = new Image(getClass().getResourceAsStream("/assets/player/PushKnight.png"));
```

## Documentation Standards
- Comment complex AI logic and pathfinding algorithms
- Document game constants and their impact on gameplay
- Maintain changelog for balance adjustments
- Keep API documentation for game systems

## Common Tasks
- **Adding new enemy types**: Extend `Enemy` base class, implement `updateAI()` with custom behavior
- **Creating animations**: Use SpriteAnimator with proper sprite sheets and frame ranges
- **Adjusting difficulty**: Modify SpawnSystem spawn curves, rates and enemy compositions
- **UI updates**: Update GamePanel with new visual elements
- **Level design**: Configure SpawnSystem with new level parameters

## JavaFX Best Practices
- Keep game logic separate from JavaFX UI elements
- Use `Platform.runLater()` for UI updates from background threads when needed
- Leverage Scene Graph for visual composition rather than Canvas
- Use constants for all magic numbers and configuration values

## Example Core Classes Structure

### Main.java
```java
public class Main {
    public static void main(String[] args) {
        GameLauncher.main(args);
    }
}
```

### Game Logic Structure
```java
public class GameLogic {
    // Grid dimensions
    public static final int GRID_WIDTH = 14;
    public static final int GRID_HEIGHT = 9;

    // Game entities
    private Player player;
    private List<Enemy> enemies;
    private List<Trap> traps;
    private int[][] entityGrid;  // 0=empty, 1=wall, 2=enemy, 3=spikes, 4=campfire
    private Obstacle[][] obstacleGrid;

    // Systems
    private Pathfinder pathfinder;
    private SpawnSystem spawnSystem;
    private UpgradeManager upgradeManager;

    public void updateGame();  // Main game update loop
    public boolean attemptMove(int dirX, int dirY);  // Player movement
    public boolean attemptPushAction();  // Player push action
}
```

### Current Game Logic Structure
The project currently uses a grid-based approach where:
- GameLauncher orchestrates the overall game flow
- GameLogic manages game state and core mechanics
- GamePanel handles rendering and UI
- InputHandler processes keyboard commands
- SpriteAnimator manages character animations

## Current Implementation Status
As of December 13, 2025:

### Completed Components
- **Core Framework**: JavaFX Application with game loop (AnimationTimer) and Grid-based rendering
- **Utilities**: Complete Constants with all game parameters defined
- **Entity System**: Player and Enemy base classes with full animation support
- **Character System**: Complete Player and all enemy type implementations (Goblin, Skeleton, SkeletonBrute, BoomerGoblin)
- **Game Systems**: SpawnSystem, Pathfinder, UpgradeManager, and other core systems
- **UI Framework**: Level selection, game UI with wave information and health display
- **Game Logic**: Grid-based movement system with push mechanics
- **Animation System**: Complete SpriteAnimator with directional animations
- **Visual Assets**: Sprite sheets for all characters and environmental elements

### Current Active Implementation
- **Grid-Based Game Logic**: GameLogic with 14x9 grid system and push mechanics
- **Visual Rendering**: GamePanel with GridPane-based layout and animated sprites
- **Input System**: InputHandler with WASD/arrow key movement and spacebar push
- **Level Structure**: Multiple levels with different wave patterns and endless mode

### Game Features
- **Multiple Levels**: 3 preset levels with different difficulty curves and wave counts
- **Endless Mode**: Infinite survival gameplay with scaling difficulty
- **Animated Characters**: Full animation system with directional sprites
- **Push Mechanics**: Area push ability that affects adjacent enemies
- **Wave Progression**: Clear wave completion and progression system
- **Obstacles**: Different types of environmental obstacles (walls, spikes, campfires)

---

## Debugging Tips
- Monitor wave progression and spawn timing for balance testing
- Check pathfinding behavior for enemies getting stuck
- Track push mechanics effectiveness and cooldowns
- Monitor animation performance with multiple entities on screen

---

*Last Updated: 2025-12-13*
*Version: 1.0*
