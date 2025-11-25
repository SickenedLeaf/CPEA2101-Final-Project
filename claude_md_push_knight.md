# Push Knight Peril - Development Guide

## Session Startup Protocol
**Always read PLANNING.md at the start of every new conversation, check TASKS.md before starting your work, mark completed tasks to TASKS.md immediately, and add newly discovered tasks to TASKS.md when found.**

## CRITICAL: Development Environment Requirements

**IMPORTANT: This project uses Maven with Java 21 and JavaFX 21. Follow these requirements for ALL code generation:**

1. **Use Maven-based configuration**
   - Use JavaFX imports as specified in Maven dependencies
   - Follow Maven project structure: `src/main/java` for source files
   - Target Java 21 features and syntax

2. **Use standard JavaFX structure**
   - Standard imports: `javafx.application.Application`, `javafx.stage.Stage`, `javafx.scene.Scene`
   - Standard JavaFX components and APIs
   - Code must work with JavaFX 21 dependencies from Maven

3. **Resource file locations**
   - Resource files (images, CSS) are located in **"src/main/resources" folder**
   - Follow Maven standard structure: `src/main/resources/images/`, `src/main/resources/sounds/`, `src/main/resources/css/`, `src/main/resources/fxml/`

4. **Target Java version**
   - Target Java version is **Java 21**
   - Use Java 21 features and syntax

5. **Code compatibility**
   - Code must work with Maven-managed JavaFX dependencies
   - Follow standard Java and JavaFX practices
   - Ensure compatibility with Java 21 and JavaFX 21

6. **Follow project structure**
   - Follow structure and naming conventions from TASKS.md and CLAUDE.md
   - Use package structure: `com.pushknight.*`
   - Source files in: `src/main/java/com/pushknight/` (Maven structure)

**When generating code, always ensure it follows these Maven and Java 21 requirements!**

## Project Overview
Push Knight Peril is a Java/JavaFX game featuring combat mechanics, enemy waves, and upgrade systems. The player must survive against various enemy types while managing cooldowns and utilizing traps.

## Technology Stack
- **Language**: Java
- **GUI Framework**: JavaFX
- **Game Loop**: JavaFX AnimationTimer
- **Graphics**: JavaFX Canvas or Scene Graph (Nodes)
- **Build System**: Maven or Gradle (recommended)

## Core Systems

### Movement System
- **movementCooldown**: Controls player movement timing and rate limiting
- Implement frame-rate independent movement
- Handle input buffering during cooldown periods

### Enemy System
The game features multiple enemy types with distinct behaviors:

#### Enemy Types
- **Skeleton**: Basic enemy type (NormalEnemy)
- **Goblin**: Standard enemy with unique behavior (NormalEnemy)
- **SkeletonBrute**: Heavy enemy with `isObstacle = true` property
- **BoomerGoblin**: Specialized goblin variant with explosive/special mechanics

#### Enemy Properties
- Standard enemies inherit from `NormalEnemy` base class
- Some enemies (SkeletonBrute) act as obstacles that block movement
- Each enemy type should have distinct AI patterns

### Spawning System
- **GameDirector**: Manages enemy spawning, wave progression, and difficulty scaling
- **spawnRate**: Controls frequency of enemy appearances
- Implement wave-based spawning with increasing difficulty
- Balance spawn rates based on current game state

### Upgrade System
- **UpgradeManager**: Handles player progression and power-ups
- Manage upgrade unlocks and availability
- Track player choices and applied upgrades
- Implement upgrade UI and selection flow

### Environmental System
- **Trap**: Interactive hazards that affect enemies
- Should integrate with obstacle system
- Consider trap placement strategy and cooldowns

## Development Guidelines

### Code Organization (Eclipse Structure)
```
src/com/pushknight/
  /entities
    - Player.java
    - Enemy.java (abstract base class)
    - NormalEnemy.java (base for standard enemies)
    - Skeleton.java
    - Goblin.java
    - SkeletonBrute.java
    - BoomerGoblin.java
  /systems
    - GameDirector.java
    - UpgradeManager.java
    - MovementSystem.java
  /objects
    - Trap.java
  /controllers
    - GameController.java
    - MenuController.java
    - UpgradeController.java
  /views
    - GameView.java
    - MenuView.java
    - UpgradeView.java
  /utils
    - Constants.java
    - Vector2D.java
    - GameState.java
    - Direction.java
    - EnemyType.java
  /Main.java (JavaFX Application entry point)

resources/ (at project root, NOT in src/)
  /images
  /sounds
  /fxml (if using FXML for UI)
  /css
```

### Key Implementation Considerations

#### Enemy AI
- Implement pathfinding for navigation
- Handle collision with obstacles (especially SkeletonBrute)
- Create distinct behavior patterns per enemy type
- Optimize for multiple enemies on screen

#### Performance
- Use object pooling for enemies (reuse defeated enemies)
- Optimize collision detection with spatial partitioning or simple bounds checking
- Limit drawing calls - use Canvas with batch rendering if possible
- Consider using `CachedCanvas` for static background elements
- Profile with VisualVM if performance issues arise

#### Game Balance
- Tune `movementCooldown` for responsive but not overpowered movement
- Balance `spawnRate` to create challenging but fair difficulty curves
- Ensure BoomerGoblin special mechanics are balanced
- Make trap placement strategically meaningful

### Testing Priorities
1. Movement feel and responsiveness
2. Enemy spawn pacing and difficulty scaling
3. Collision detection accuracy (especially with isObstacle enemies)
4. Upgrade system balance and progression
5. Trap effectiveness and placement strategy

## Architecture Patterns

### Entity Component System
Consider using composition pattern for scalable entity management:
- **Abstract Entity class** with position, velocity, health
- **Interfaces**: Collidable, Renderable, Updatable, Movable
- **Components**: Sprite, Hitbox, AI behavior
- Update and render through polymorphism

### State Management (Enum-based)
```java
enum GameState {
    MENU, PLAYING, PAUSED, GAME_OVER, UPGRADE_SELECTION
}
```
- Track player stats, current wave, upgrade choices
- Use JavaFX Properties for reactive UI updates
- Consider serialization for save/load functionality

### Event System
Use Observer pattern or JavaFX Events:
```java
// Custom events
class EnemyDefeatedEvent extends Event { ... }
class WaveCompleteEvent extends Event { ... }

// Or simple Observer pattern
interface GameEventListener {
    void onEnemyDefeated(Enemy enemy);
    void onWaveComplete(int waveNumber);
}
```

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
**Option 1: Canvas-based (Recommended for many moving entities)**
- Use `GraphicsContext` for 2D drawing
- Better performance for particle effects and many enemies
- More traditional game development approach

**Option 2: Scene Graph (Node-based)**
- Use ImageView, Rectangle, Circle for entities
- Easier collision detection with built-in Bounds
- Better for UI-heavy games
- May have performance limits with 50+ entities

#### Input Handling
```java
scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
```

### JavaFX-Specific Considerations
- Use `Timeline` or `AnimationTimer` for smooth animations
- Leverage `TranslateTransition` for movement animations
- Use `FadeTransition` for UI effects
- Implement sprite animation with `ImageView` and timer-based frame switching
- Consider `Canvas` for performance-critical rendering

### Asset Loading (Eclipse Resources)
**Note: Resources folder is at project root, not in src/**
```java
// For resources folder at project root (Eclipse structure)
Image playerSprite = new Image(getClass()
    .getResourceAsStream("/images/player.png"));
// Or if resources is configured as source folder:
Image playerSprite = new Image(getClass()
    .getResourceAsStream("/resources/images/player.png"));

AudioClip soundEffect = new AudioClip(getClass()
    .getResource("/sounds/hit.wav").toString());
```

## Documentation Standards
- Comment complex AI logic
- Document upgrade effects and calculations
- Maintain changelog for balance adjustments
- Keep API documentation for key systems

## Common Tasks
- **Adding new enemy types**: Extend `NormalEnemy` base class, implement `update()` and custom AI
- **Creating upgrades**: Add to UpgradeManager enum/list, implement effect application
- **Adjusting difficulty**: Modify GameDirector spawn curves and timing
- **Adding traps**: Implement Trap interface, add collision logic
- **UI updates**: Use FXML + CSS or programmatic JavaFX layouts

## JavaFX Best Practices
- Keep game logic separate from JavaFX UI thread when possible
- Use `Platform.runLater()` for UI updates from background threads
- Leverage CSS for styling UI elements
- Use FXML for complex UI layouts (upgrade screens, menus)
- Bind properties for reactive UI (health bars, score displays)

## Example Core Classes Structure

### Main.java
```java
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Setup game scene
    }
}
```

### Entity Base Classes
```java
public abstract class Entity {
    protected double x, y;
    protected double width, height;
    public abstract void update(long now);
    public abstract void render(GraphicsContext gc);
}

public abstract class Enemy extends Entity {
    protected int health;
    protected double speed;
    public abstract void onDeath();
}
```

### Game Controller
```java
public class GameController {
    private AnimationTimer gameLoop;
    private List<Enemy> enemies;
    private Player player;
    private GameDirector director;
    
    public void startGame() { /* ... */ }
    private void update(long now) { /* ... */ }
    private void checkCollisions() { /* ... */ }
}
```

## Current Implementation Status
As of January 25, 2025:

### Completed Components
- **Project Infrastructure**: Maven project structure with Java 21 and JavaFX 21
- **Core Framework**: Main application with JavaFX game loop (AnimationTimer) and Canvas rendering
- **Utilities**: Complete Constants, Vector2D, GameState, Direction, and EnemyType implementations
- **Entity System**: Abstract Entity class with interfaces (Updatable, Renderable, Collidable, Damageable)
- **Package Structure**: All required packages (entities, systems, controllers, views, utils) created

### In Progress / Next Steps
- **Character System**: Create Character abstract class as base for Player and Enemy
- **Player Implementation**: Player class with movement, input handling, and health
- **Enemy Classes**: Implementation of Skeleton, Goblin, SkeletonBrute, and BoomerGoblin
- **Game Systems**: GameDirector, CollisionSystem, UpgradeManager, and other core systems
- **UI Framework**: MenuController, GameController, and UI components

---

## Debugging Tips
- Log spawn rates and wave progression for balance testing
- Visualize collision boxes, especially for isObstacle entities
- Track movement cooldown timing for responsiveness testing
- Monitor enemy pool usage for performance optimization

---

*Last Updated: 2025-01-25*
*Version: 1.1*