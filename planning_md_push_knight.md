# PLANNING.md - Push Knight Peril

## Project Vision

### Game Concept
Push Knight Peril is a 2D survival game where players control a knight who must survive waves of enemies using tactical positioning, timing, and strategic upgrades. The player pushes enemies into traps while managing movement cooldowns and navigating around obstacle-type enemies.

### Core Gameplay Loop
1. **Survive** - Avoid or defeat incoming enemy waves
2. **Push** - Use knight's push ability to move enemies into traps
3. **Upgrade** - Select power-ups between waves to become stronger
4. **Progress** - Face increasingly difficult waves with new enemy types

### Learning Objectives (Educational Focus)
This project demonstrates advanced Java and OOP concepts:
- **Polymorphism**: Different enemy types with shared interface
- **Inheritance**: Enemy hierarchy (Entity → Enemy → NormalEnemy → Specific types)
- **Encapsulation**: Private fields with public getters/setters
- **Abstraction**: Abstract classes and interfaces for game entities
- **Design Patterns**: Factory, Observer, Strategy, Singleton, Object Pool
- **JavaFX**: Event handling, animations, scene management
- **Data Structures**: Lists, Maps for entity management
- **Algorithms**: Pathfinding, collision detection, spawning algorithms

### Success Criteria
- [ ] Smooth gameplay at 60 FPS with multiple enemies on screen
- [ ] At least 4 distinct enemy types with unique behaviors
- [ ] Functional upgrade system with meaningful choices
- [ ] Working trap mechanics
- [ ] Wave-based difficulty progression
- [ ] Polished UI/UX for menus and gameplay
- [ ] Sound effects and visual feedback

---

## Architecture Overview

### Design Philosophy
The game follows **Model-View-Controller (MVC)** pattern with **Entity-Component** influences:
- **Model**: Game entities, state, and logic
- **View**: JavaFX rendering and UI
- **Controller**: Input handling and game flow coordination

### High-Level Architecture Diagram
```
┌─────────────────────────────────────────────────────────┐
│                     Main Application                     │
│                   (JavaFX Application)                   │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼────────┐       ┌───────▼────────┐
│  GameController │       │   MenuController│
│                │       │                 │
│ - Game Loop    │       │ - UI Navigation │
│ - State Mgmt   │       │ - Settings      │
└───────┬────────┘       └─────────────────┘
        │
        │ manages
        │
┌───────▼──────────────────────────────────────────┐
│              Game Systems Layer                   │
├──────────────────┬───────────────┬───────────────┤
│  GameDirector    │ UpgradeManager│ CollisionSys  │
│  (Spawning)      │ (Powerups)    │ (Physics)     │
└──────────────────┴───────────────┴───────────────┘
        │
        │ operates on
        │
┌───────▼──────────────────────────────────────────┐
│              Entities Layer                       │
├──────────────┬──────────────┬────────────────────┤
│   Player     │   Enemies    │   GameObjects      │
│              │ (polymorphic)│   (Traps, etc)     │
└──────────────┴──────────────┴────────────────────┘
        │
        │ rendered by
        │
┌───────▼──────────────────────────────────────────┐
│           Rendering Layer (JavaFX)                │
│  - Canvas/GraphicsContext                         │
│  - Scene Graph (UI elements)                      │
└───────────────────────────────────────────────────┘
```

### Core Systems

#### 1. Entity Management System
**Purpose**: Manage all game objects (player, enemies, traps)

**OOP Concepts Applied**:
- **Inheritance Hierarchy**:
  ```
  Entity (abstract)
    ├── Character (abstract)
    │   ├── Player
    │   └── Enemy (abstract)
    │       └── NormalEnemy (abstract)
    │           ├── Skeleton
    │           ├── Goblin
    │           ├── SkeletonBrute
    │           └── BoomerGoblin
    └── GameObject (abstract)
        └── Trap
  ```

- **Polymorphism**: All entities can be updated/rendered through base class reference
  ```java
  List<Entity> allEntities = new ArrayList<>();
  for (Entity entity : allEntities) {
      entity.update(deltaTime); // Polymorphic call
      entity.render(gc);        // Polymorphic call
  }
  ```

- **Encapsulation**: Entity properties (position, health) are private with controlled access

**Key Interfaces**:
```java
interface Updatable {
    void update(long now);
}

interface Renderable {
    void render(GraphicsContext gc);
}

interface Collidable {
    boolean collidesWith(Collidable other);
    Rectangle2D getBounds();
}

interface Damageable {
    void takeDamage(int amount);
    boolean isAlive();
}
```

#### 2. Game Director System
**Purpose**: Control enemy spawning, wave progression, difficulty scaling

**Design Pattern**: **Strategy Pattern** for spawn strategies
```java
interface SpawnStrategy {
    List<Enemy> generateWave(int waveNumber);
}

class EarlyGameStrategy implements SpawnStrategy { ... }
class MidGameStrategy implements SpawnStrategy { ... }
class LateGameStrategy implements SpawnStrategy { ... }
```

**Responsibilities**:
- Calculate spawn rates based on wave number
- Select appropriate enemy types for current difficulty
- Trigger wave start/end events
- Manage spawn positions

#### 3. Movement System
**Purpose**: Handle player and enemy movement with cooldowns

**OOP Concepts**:
- **Encapsulation**: Movement logic encapsulated in dedicated class
- **Composition**: Entities "have-a" MovementComponent
  ```java
  class MovementComponent {
      private double speed;
      private long cooldownTime;
      private long lastMoveTime;
      
      public boolean canMove() { ... }
      public void move(Entity entity, Direction dir) { ... }
  }
  ```

#### 4. Upgrade System
**Purpose**: Manage player power-ups and progression

**Design Pattern**: **Factory Pattern** for creating upgrades
```java
abstract class Upgrade {
    protected String name;
    protected String description;
    public abstract void apply(Player player);
}

class UpgradeFactory {
    public static Upgrade createUpgrade(UpgradeType type) {
        // Factory creates appropriate upgrade instance
    }
}
```

**Upgrade Types**:
- Movement speed increase
- Damage boost
- Health increase
- Special abilities
- Trap enhancements

#### 5. Collision System
**Purpose**: Detect and resolve collisions between entities

**Algorithm**: 
- Broad phase: Spatial grid or simple AABB checks
- Narrow phase: Precise collision for overlapping bounds
- Response: Push enemies, trigger traps, damage player

**OOP**: Uses polymorphism through Collidable interface

#### 6. AI System
**Purpose**: Control enemy behavior and pathfinding

**Design Pattern**: **Strategy Pattern** for AI behaviors
```java
interface AIBehavior {
    void execute(Enemy enemy, Player target);
}

class ChasePlayerBehavior implements AIBehavior { ... }
class PatrolBehavior implements AIBehavior { ... }
class ExplodeOnContactBehavior implements AIBehavior { ... }
```

---

## Technology Stack

### Core Technologies

#### Java
- **Version**: Java 17 or higher (LTS)
- **Why**: Modern features, excellent OOP support, cross-platform
- **Key Features Used**:
  - Generics for type-safe collections
  - Lambda expressions for callbacks
  - Streams for data processing
  - Enums for game states and types

#### JavaFX
- **Version**: JavaFX 17 or higher
- **Why**: Built-in 2D graphics, animation, UI components
- **Modules Used**:
  - `javafx.controls` - UI components
  - `javafx.graphics` - Canvas, shapes, rendering
  - `javafx.media` - Audio playback
  - `javafx.fxml` - UI layout (optional)

### Build System

#### Maven (Recommended)
```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-media</artifactId>
        <version>17.0.2</version>
    </dependency>
</dependencies>
```

**Alternative**: Gradle with JavaFX plugin

### Project Structure
```
push-knight-peril/
├── pom.xml (or build.gradle)
├── README.md
├── PLANNING.md (this file)
├── CLAUDE.md
├── TASKS.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pushknight/
│   │   │           ├── Main.java
│   │   │           ├── entities/
│   │   │           │   ├── Entity.java (abstract)
│   │   │           │   ├── Character.java (abstract)
│   │   │           │   ├── Enemy.java (abstract)
│   │   │           │   ├── NormalEnemy.java (abstract)
│   │   │           │   ├── Player.java
│   │   │           │   ├── Skeleton.java
│   │   │           │   ├── Goblin.java
│   │   │           │   ├── SkeletonBrute.java
│   │   │           │   ├── BoomerGoblin.java
│   │   │           │   └── GameObject.java (abstract)
│   │   │           ├── objects/
│   │   │           │   └── Trap.java
│   │   │           ├── systems/
│   │   │           │   ├── GameDirector.java
│   │   │           │   ├── UpgradeManager.java
│   │   │           │   ├── CollisionSystem.java
│   │   │           │   └── MovementSystem.java
│   │   │           ├── controllers/
│   │   │           │   ├── GameController.java
│   │   │           │   ├── MenuController.java
│   │   │           │   └── UpgradeController.java
│   │   │           ├── views/
│   │   │           │   ├── GameView.java
│   │   │           │   └── UpgradeView.java
│   │   │           ├── ai/
│   │   │           │   ├── AIBehavior.java (interface)
│   │   │           │   └── implementations...
│   │   │           ├── upgrades/
│   │   │           │   ├── Upgrade.java (abstract)
│   │   │           │   ├── UpgradeFactory.java
│   │   │           │   └── specific upgrades...
│   │   │           └── utils/
│   │   │               ├── Constants.java
│   │   │               ├── Vector2D.java
│   │   │               ├── GameState.java (enum)
│   │   │               └── Direction.java (enum)
│   │   └── resources/
│   │       ├── images/
│   │       │   ├── player.png
│   │       │   ├── skeleton.png
│   │       │   ├── goblin.png
│   │       │   └── ...
│   │       ├── sounds/
│   │       │   ├── hit.wav
│   │       │   ├── death.wav
│   │       │   └── ...
│   │       ├── fxml/
│   │       │   ├── menu.fxml
│   │       │   └── upgrade.fxml
│   │       └── css/
│   │           └── style.css
│   └── test/
│       └── java/
│           └── com/
│               └── pushknight/
│                   └── tests...
└── target/ (or build/ for Gradle)
```

---

## Required Tools & Setup

### Development Environment

#### 1. Java Development Kit (JDK)
- **Required**: JDK 17 or higher
- **Download**: [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
- **Verify Installation**: `java -version` and `javac -version`

#### 2. Integrated Development Environment (IDE)

**Option A: IntelliJ IDEA (Recommended)**
- **Edition**: Community (free) or Ultimate
- **Why**: Best Java/JavaFX support, Maven integration, code completion
- **Plugins**: JavaFX Runtime for better development experience

**Option B: Eclipse**
- **With**: e(fx)clipse plugin for JavaFX
- **Download**: [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/)

**Option C: VS Code**
- **Extensions**: 
  - Extension Pack for Java
  - JavaFX Support
- **Note**: More manual setup required

#### 3. Build Tool
- **Maven** (comes with most IDEs)
- **Or Gradle** (alternative)
- Verify: `mvn -version` or `gradle -version`

#### 4. Version Control
- **Git**: For code versioning
- **GitHub/GitLab**: For remote repository
- **Installation**: [git-scm.com](https://git-scm.com/)

#### 5. JavaFX SDK (if not using Maven)
- **Download**: [openjfx.io](https://openjfx.io/)
- **Note**: Maven handles this automatically

### Optional Tools

#### Graphics Tools
- **Aseprite / LibreSprite**: Pixel art and sprite creation
- **GIMP / Paint.NET**: Image editing
- **Inkscape**: Vector graphics (for UI elements)

#### Audio Tools
- **Audacity**: Sound effect editing
- **BFXR / ChipTone**: 8-bit sound effect generation

#### Design Tools
- **Draw.io / Excalidraw**: Architecture diagrams
- **Figma**: UI mockups

#### Debugging & Profiling
- **VisualVM**: Java profiling (comes with JDK)
- **JConsole**: Monitor Java application performance

### Asset Resources (Free)

#### Graphics
- **OpenGameArt.org**: Free game sprites
- **Kenney.nl**: Free game assets (highly recommended)
- **itch.io**: Many free asset packs

#### Sound Effects
- **Freesound.org**: Large library of free sounds
- **OpenGameArt.org**: Game sound effects
- **Zapsplat.com**: Free sound effects

---

## OOP Concepts Implementation

### 1. Inheritance
**Implementation**: Entity hierarchy
```java
// Base class
public abstract class Entity implements Updatable, Renderable {
    protected double x, y;
    protected double width, height;
    
    public abstract void update(long now);
    public abstract void render(GraphicsContext gc);
}

// Derived classes inherit common properties and methods
public class Player extends Character { ... }
public class Skeleton extends NormalEnemy { ... }
```

**Benefits**:
- Code reuse (position, rendering logic shared)
- Establishes "is-a" relationships
- Natural hierarchy for game entities

### 2. Polymorphism
**Implementation**: Collections of base type, runtime method resolution
```java
// Different enemy types, same interface
List<Enemy> enemies = new ArrayList<>();
enemies.add(new Skeleton());
enemies.add(new Goblin());
enemies.add(new SkeletonBrute());

// Polymorphic behavior
for (Enemy enemy : enemies) {
    enemy.update(now);      // Each calls its own implementation
    enemy.render(gc);       // Runtime polymorphism
    enemy.applyAI(player);  // Different AI for each type
}
```

**Benefits**:
- Write code that works with base types
- Easy to add new enemy types
- Clean, maintainable code

### 3. Encapsulation
**Implementation**: Private fields, public methods
```java
public class Player extends Character {
    private int health;           // Private - controlled access
    private int maxHealth;
    private MovementComponent movement;  // Encapsulated behavior
    
    public int getHealth() { return health; }
    
    public void takeDamage(int amount) {  // Controlled modification
        health = Math.max(0, health - amount);
        if (health == 0) {
            die();
        }
    }
}
```

**Benefits**:
- Data integrity (health can't be negative)
- Internal implementation can change
- Clear interface for interacting with objects

### 4. Abstraction
**Implementation**: Abstract classes and interfaces
```java
// Abstract class - partial implementation
public abstract class Enemy extends Character {
    protected int damage;
    protected AIBehavior ai;
    
    public abstract void onDeath();  // Must be implemented by subclasses
    
    public void attack(Player player) {  // Shared implementation
        player.takeDamage(damage);
    }
}

// Interface - pure abstraction
public interface Collidable {
    boolean collidesWith(Collidable other);
    Rectangle2D getBounds();
}
```

**Benefits**:
- Hide complex implementation details
- Define contracts for classes
- Enable multiple inheritance (via interfaces)

### 5. Composition
**Implementation**: "Has-a" relationships
```java
public class Player extends Character {
    private MovementComponent movement;   // Player HAS-A movement component
    private WeaponComponent weapon;       // Player HAS-A weapon
    private HealthComponent health;       // Player HAS-A health system
    
    public void move(Direction dir) {
        movement.move(this, dir);  // Delegate to component
    }
}
```

**Benefits**:
- More flexible than inheritance
- Enables runtime behavior changes
- Better code reuse

### 6. Design Patterns Applied

#### Factory Pattern
```java
public class EnemyFactory {
    public static Enemy createEnemy(EnemyType type, double x, double y) {
        return switch(type) {
            case SKELETON -> new Skeleton(x, y);
            case GOBLIN -> new Goblin(x, y);
            case SKELETON_BRUTE -> new SkeletonBrute(x, y);
            case BOOMER_GOBLIN -> new BoomerGoblin(x, y);
        };
    }
}
```

#### Singleton Pattern
```java
public class GameDirector {
    private static GameDirector instance;
    
    private GameDirector() {}  // Private constructor
    
    public static GameDirector getInstance() {
        if (instance == null) {
            instance = new GameDirector();
        }
        return instance;
    }
}
```

#### Observer Pattern
```java
interface GameEventListener {
    void onEnemyDefeated(Enemy enemy);
    void onWaveComplete(int waveNumber);
    void onPlayerDamaged(int health);
}

class GameController implements GameEventListener {
    // Responds to game events
}
```

#### Strategy Pattern
```java
interface AIBehavior {
    void execute(Enemy enemy, Player target);
}

class Enemy {
    private AIBehavior behavior;
    
    public void setBehavior(AIBehavior behavior) {
        this.behavior = behavior;  // Change behavior at runtime
    }
}
```

#### Object Pool Pattern
```java
public class EnemyPool {
    private Queue<Enemy> availableEnemies = new LinkedList<>();
    
    public Enemy acquire(EnemyType type) {
        if (availableEnemies.isEmpty()) {
            return EnemyFactory.createEnemy(type, 0, 0);
        }
        Enemy enemy = availableEnemies.poll();
        enemy.reset();
        return enemy;
    }
    
    public void release(Enemy enemy) {
        availableEnemies.offer(enemy);
    }
}
```

---

## Development Phases

### Phase 1: Foundation (Week 1)
- [ ] Set up project structure and build system
- [ ] Create base Entity, Character, Enemy classes
- [ ] Implement basic Player with movement
- [ ] Set up JavaFX window and game loop
- [ ] Basic rendering on Canvas

### Phase 2: Enemy System (Week 1-2)
- [ ] Implement Skeleton (basic enemy)
- [ ] Implement Goblin (variant behavior)
- [ ] Implement SkeletonBrute (obstacle enemy)
- [ ] Implement BoomerGoblin (special ability)
- [ ] Basic AI pathfinding
- [ ] Enemy pool for performance

### Phase 3: Game Systems (Week 2)
- [ ] GameDirector with wave spawning
- [ ] Collision detection system
- [ ] Trap objects and mechanics
- [ ] Health and damage systems
- [ ] Score tracking

### Phase 4: Progression (Week 3)
- [ ] Upgrade system architecture
- [ ] Create 5-10 upgrade types
- [ ] Upgrade selection UI
- [ ] Wave difficulty scaling
- [ ] Save/load player progress

### Phase 5: Polish (Week 3-4)
- [ ] Main menu and pause menu
- [ ] Sound effects and music
- [ ] Visual effects (particles, animations)
- [ ] Game over screen with stats
- [ ] Final balancing and bug fixes

---

## Quality Standards

### Code Quality
- Follow Java naming conventions (PascalCase for classes, camelCase for methods)
- Document all public methods with Javadoc
- Keep methods under 30 lines when possible
- Use meaningful variable names
- Avoid magic numbers (use Constants class)

### Performance Targets
- Maintain 60 FPS with 50+ enemies on screen
- Game loop should complete in < 16ms
- Memory usage stable (no memory leaks)
- Smooth animations and transitions

### Testing
- Unit tests for core game logic (collision, spawning algorithms)
- Manual testing of all enemy types
- Balance testing for difficulty progression
- User testing for gameplay feel

---

## Notes for Claude Code

When working on this project:
1. **Always read this file first** to understand the full context
2. **Respect the architecture** - don't create circular dependencies
3. **Apply OOP principles** - this is an educational project
4. **Add Javadoc comments** for all public methods and classes
5. **Consider performance** - use object pooling for frequently created objects
6. **Test incrementally** - make sure each feature works before moving on
7. **Update TASKS.md** when you complete or discover tasks

---

*Last Updated: 2025-01-23*
*Version: 1.0*