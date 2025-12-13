# PLANNING.md - Push Knight Peril

## Project Vision

### Game Concept
Push Knight Peril is a 2D survival game where players control a knight who must survive waves of enemies using tactical positioning, timing, and strategic pushes. The player pushes enemies into traps while managing movement cooldowns and navigating around obstacle-type enemies using a grid-based movement system (14x9 grid).

### Core Gameplay Loop
1. **Survive** - Avoid or defeat incoming enemy waves
2. **Push** - Use knight's push ability to move enemies into traps
3. **Navigate** - Use tactical positioning to outmaneuver enemies
4. **Progress** - Face increasingly difficult waves with new enemy types

### Learning Objectives (Educational Focus)
This project demonstrates advanced Java and OOP concepts:
- **Polymorphism**: Different enemy types with shared interface
- **Inheritance**: Enemy hierarchy (Enemy → Specific types with shared behavior)
- **Encapsulation**: Private fields with public getters/setters
- **Abstraction**: Abstract classes and enums for game entities
- **Design Patterns**: Animation system, A* pathfinding, Singleton for management systems
- **JavaFX**: Event handling, animations, scene management
- **Data Structures**: Lists, Maps for entity management
- **Algorithms**: A* pathfinding, grid-based collision detection

### Success Criteria
- [x] Smooth gameplay with animated sprites and visual feedback
- [x] At least 4 distinct enemy types with unique behaviors
- [x] Functional upgrade system with meaningful choices
- [x] Working trap mechanics
- [x] Wave-based difficulty progression
- [x] Polished UI/UX for menus and gameplay
- [x] Sound effects and visual feedback

---

## Architecture Overview

### Design Philosophy
The game follows a **Grid-Based MVC** pattern with **Event-Driven** communication:
- **Model**: Game entities, state, and grid logic (GameLogic)
- **View**: JavaFX GridPane rendering with sprite animations (GamePanel)
- **Controller**: Input handling and game flow coordination (GameLauncher, InputHandler)

### High-Level Architecture Diagram
```
┌─────────────────────────────────────────────────────────┐
│                     GameLauncher                        │
│                 (JavaFX Application)                    │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼────────┐       ┌───────▼────────┐
│  InputHandler  │       │  GamePanel     │
│                │       │ (Rendering)    │
│ - Key Events   │       │ - Grid-based   │
└───────┬────────┘       │ - Animation    │
        │                │ - Event UI     │
        │ manages        └─────────────────┘
        │
┌───────▼──────────────────────────────────────────┐
│              Game Logic Layer                     │
├──────────────────┬───────────────┬───────────────┤
│  GameLogic       │ SpawnSystem   │ Pathfinder    │
│  (Core State)    │ (Waves)       │ (AI)          │
└──────────────────┴───────────────┴───────────────┘
        │
        │ operates on
        │
┌───────▼──────────────────────────────────────────┐
│              Entities Layer                       │
├──────────────┬──────────────┬────────────────────┤
│   Player     │   Enemies    │   Obstacles/Traps  │
│              │ (polymorphic)│   (Walls, Spikes)  │
└──────────────┴──────────────┴────────────────────┘
        │
        │ rendered by
        │
┌───────▼──────────────────────────────────────────┐
│           Rendering Layer (JavaFX)                │
│  - GridPane layout                               │
│  - SpriteAnimator with 72x72 frames             │
└───────────────────────────────────────────────────┘
```

### Core Systems

#### 1. Grid-Based Entity Management System
**Purpose**: Manage all game objects on a 14x9 grid with efficient collision detection

**OOP Concepts Applied**:
- **Inheritance Hierarchy**:
  ```
  Enemy (abstract)
    ├── Goblin
    ├── Skeleton
    ├── SkeletonBrute
    └── BoomerGoblin
  ```

- **Polymorphism**: All enemies can be updated through base class reference
  ```java
  List<Enemy> enemies = new ArrayList<>();
  for (Enemy enemy : enemies) {
      enemy.updateAI(deltaTime, playerX, playerY, pathfinder, grid);
      enemy.updateCooldowns(deltaTime, playerX, playerY);
  }
  ```

- **Encapsulation**: Entity properties (position, health, animations) are private with controlled access

#### 2. Spawn System
**Purpose**: Control enemy spawning, wave progression, difficulty scaling

**Responsibilities**:
- Calculate spawn rates based on wave number
- Select appropriate enemy types for current difficulty
- Trigger wave start/end events
- Manage fixed spawn positions around grid edges

#### 3. Pathfinding System
**Purpose**: Enable enemy AI to navigate around obstacles toward player

**Algorithm**: A* pathfinding with grid-based obstacle awareness
- Handles passable (spikes, campfire) and impassable (walls) obstacles
- Provides next move direction for enemy AI

#### 4. Upgrade System
**Purpose**: Manage player progression with upgrade levels

**Upgrade Types**:
- Health upgrades
- Push range improvements
- Push cooldown reduction

#### 5. Animation System
**Purpose**: Handle character and visual effect animations

**Design**: SpriteAnimator with 72x72 pixel frames and directional animations
- Idle, move, attack, and death animations per character
- State-based animation management per entity

#### 6. Event System
**Purpose**: Communicate between game logic and rendering

**Implementation**: GameUpdateEvent for rendering updates:
```java
public class GameUpdateEvent {
    public enum Type {
        PLAYER_MOVE, ENEMY_MOVE, ENEMY_SPAWN, DAMAGE,
        REMOVE_ENTITY, IMPACT, PLAYER_DAMAGE
    }
}
```

---

## Technology Stack

### Core Technologies

#### Java
- **Version**: Java 21
- **Why**: Latest LTS version with modern features, excellent OOP support, cross-platform
- **Key Features Used**:
  - Generics for type-safe collections
  - Lambda expressions for callbacks
  - Enums for game states and types
  - Enhanced switch expressions

#### JavaFX
- **Version**: JavaFX 21
- **Why**: Built-in 2D graphics, animation, UI components
- **Modules Used**:
  - `javafx.controls` - UI components
  - `javafx.graphics` - Canvas, shapes, rendering
  - `javafx.animation` - AnimationTimer for game loop

### Project Structure
```
push-knight-peril/
├── README.md
├── PLANNING.md (this file)
├── CLAUDE.md
├── TASKS.md
├── src/
│   └── application/
│       ├── Main.java
│       ├── GameLauncher.java (JavaFX Application)
│       ├── GameLogic.java (Core game state)
│       ├── GamePanel.java (Rendering and UI)
│       ├── InputHandler.java (Keyboard input)
│       ├── Player.java
│       ├── Enemy.java (Abstract enemy base class)
│       ├── {EnemyType}.java (Goblin, Skeleton, etc.)
│       ├── {GameSystem}.java (SpawnSystem, Pathfinder, UpgradeManager, etc.)
│       ├── SpriteAnimator.java (Animation system)
│       └── Constants.java
└── assets/ (in resources)
    ├── tiles/
    ├── obstacles/
    ├── enemies/
    └── player/
```

---

## Required Tools & Setup

### Development Environment

#### 1. Java Development Kit (JDK)
- **Required**: JDK 21 or higher
- **Download**: [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
- **Verify Installation**: `java -version` and `javac -version`

#### 2. Integrated Development Environment (IDE)

**Option A: IntelliJ IDEA (Recommended)**
- **Edition**: Community (free) or Ultimate
- **Why**: Best Java/JavaFX support, Maven integration, code completion

**Option B: Eclipse**
- **With**: e(fx)clipse plugin for JavaFX
- **Download**: [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/)

**Option C: VS Code**
- **Extensions**:
  - Extension Pack for Java
  - JavaFX Support
- **Note**: More manual setup required

#### 3. JavaFX Runtime
- **Required**: JavaFX 21 runtime for running the game
- **Installation**: Included with IDE or separate JavaFX SDK

#### 4. Version Control
- **Git**: For code versioning
- **GitHub/GitLab**: For remote repository
- **Installation**: [git-scm.com](https://git-scm.com/)

### Asset Resources (Free)

#### Graphics
- **Sprite sheets** 72x72 pixels for animations
- **Tile images** for grid backgrounds
- **Asset packs**: Kenney.nl, OpenGameArt.org

---

## OOP Concepts Implementation

### 1. Inheritance
**Implementation**: Enemy hierarchy
```java
// Base class
public abstract class Enemy {
    protected int x, y;
    protected int hp;
    protected int maxHp;
    protected int damage;

    public abstract int[] updateAI(double deltaTime, int playerX, int playerY,
                                   Pathfinder pathfinder, int[][] grid);
    // Other shared methods...
}

// Derived classes inherit common properties and methods
public class Goblin extends Enemy { ... }
public class Skeleton extends Enemy { ... }
```

**Benefits**:
- Code reuse (position, health, damage logic shared)
- Establishes "is-a" relationships
- Natural hierarchy for game entities

### 2. Polymorphism
**Implementation**: Collections of base type, runtime method resolution
```java
// Different enemy types, same interface
List<Enemy> enemies = new ArrayList<>();
enemies.add(new Goblin(5, 5));
enemies.add(new Skeleton(6, 6));
enemies.add(new SkeletonBrute(7, 7));

// Polymorphic behavior
for (Enemy enemy : enemies) {
    int[] move = enemy.updateAI(deltaTime, playerX, playerY, pathfinder, grid);
    enemy.updateCooldowns(deltaTime, playerX, playerY);
    if (enemy.canAttackPlayer(playerX, playerY)) {
        enemy.tryAttackPlayer(playerX, playerY);
    }
}
```

**Benefits**:
- Write code that works with base types
- Easy to add new enemy types
- Clean, maintainable code

### 3. Encapsulation
**Implementation**: Private fields, public methods
```java
public class Player {
    private int x, y;
    private int health;
    private int maxHealth;
    private boolean alive;
    private double pushCooldown;
    private SpriteAnimator animator;

    public void takeDamage(int damage) {  // Controlled modification
        if (!alive || isInvulnerable) return;
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    // Getters for rendering and game logic
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public boolean isAlive() { return alive; }
}
```

**Benefits**:
- Data integrity (health can't be negative)
- Internal implementation can change
- Clear interface for interacting with objects

### 4. Abstraction
**Implementation**: Abstract classes and enums
```java
// Abstract class - partial implementation
public abstract class Enemy {
    protected int x, y;
    protected int hp;
    protected int maxHp;
    protected int damage;
    protected EnemyType type;

    // Must be implemented by subclasses
    public abstract int[] updateAI(double deltaTime, int playerX, int playerY,
                                   Pathfinder pathfinder, int[][] grid);
    public abstract void playIdleAnimation();
    public abstract void playMoveAnimation();
    public abstract void playAttackAnimation();
    public abstract void playDeathAnimation();

    // Shared implementation
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            dead = true;
        }
    }
}

public enum EnemyType {
    GOBLIN, SKELETON, BRUTE, BOOMER
}
```

**Benefits**:
- Hide complex implementation details
- Define contracts for classes
- Enable multiple inheritance (via interfaces)

### 5. Composition
**Implementation**: "Has-a" relationships
```java
public class Player {
    private SpriteAnimator animator;  // Player HAS-A animation system

    public Player(int startX, int startY) {
        // Initialize animator with sprite sheet
        this.animator = new SpriteAnimator(
            new Image(getClass().getResource("/assets/player/PushKnight.png").toExternalForm()),
            72, 72);
    }

    public void moveTo(int newX, int newY) {
        // Delegate to animation system
        if ("RIGHT".equals(this.direction)) {
            animator.playAction(1, 0, 6, 50_000_000, false, () -> playIdleAnimation());
        }
        // ... other directions
    }
}
```

**Benefits**:
- More flexible than inheritance
- Enables runtime behavior changes
- Better code reuse

### 6. Design Patterns Applied

#### A* Pathfinding Algorithm
```java
public class Pathfinder {
    // A* implementation for enemy navigation
    public int[] getNextMove(int startX, int startY, int targetX, int targetY, int[][] grid) {
        // Find optimal path around obstacles
    }
}
```

#### Singleton Pattern (for management systems)
```java
public class UpgradeManager {
    private int healthUpgradeLevel;
    private int pushRangeLevel;
    private int pushCooldownLevel;
    // Management methods...
}
```

#### Observer Pattern (for UI updates)
```java
// GameUpdateEvent for communication between logic and rendering
public class GameUpdateEvent {
    public enum Type { PLAYER_MOVE, ENEMY_MOVE, ENEMY_SPAWN, DAMAGE,
                       REMOVE_ENTITY, IMPACT, PLAYER_DAMAGE }
    // Event properties...
}
```

---

## Development Phases

### Phase 1: Foundation (Week 1)
- [x] Set up project structure
- [x] Create base Enemy class and Player
- [x] Implement basic GameLogic with grid
- [x] Set up JavaFX window and game loop with AnimationTimer

### Phase 2: Enemy System (Week 1-2)
- [x] Implement Goblin (fast enemy)
- [x] Implement Skeleton (balanced enemy)
- [x] Implement SkeletonBrute (tanky enemy)
- [x] Implement BoomerGoblin (explosive enemy)
- [x] Basic A* pathfinding for navigation
- [x] Enemy AI with attack cooldowns

### Phase 3: Game Systems (Week 2)
- [x] SpawnSystem with wave progression
- [x] SpriteAnimator with directional animations
- [x] Health and damage systems
- [x] Grid-based collision detection

### Phase 4: Progression (Week 3)
- [x] Upgrade system architecture
- [x] Multiple level modes (1-3 and endless)
- [x] Wave difficulty scaling
- [x] Level selection interface

### Phase 5: Polish (Week 3-4)
- [x] Animated sprites with directional movement
- [x] Game over and level completion screens
- [ ] Sound effects and music
- [x] Visual effects (damage flashes, impacts)

---

## Quality Standards

### Code Quality
- Follow Java naming conventions (PascalCase for classes, camelCase for methods)
- Document all public methods with Javadoc
- Keep methods under 30 lines when possible
- Use meaningful variable names
- Use Constants class for all configuration values

### Performance Targets
- Maintain smooth gameplay with multiple entities on screen
- Grid-based collision detection for performance
- Efficient sprite animation system
- Memory usage stable (no memory leaks)

### Testing
- Manual testing of all enemy types
- Balance testing for difficulty progression
- UI testing for level selection flow
- Animation system performance testing

---

## Current Implementation Status

As of December 13, 2025, the following components have been successfully implemented:

### Foundation Components
- [x] **Project Structure**: Single package structure (src/application/)
- [x] **Basic Game Framework**: JavaFX Application with game loop (AnimationTimer) and Grid-based rendering
- [x] **Constants**: Complete Constants.java with all game parameters defined
- [x] **Entity Framework**: Enemy abstract class with full animation support

### Architecture Progress
- [x] **Game Logic**: 14x9 grid system with entity management
- [x] **Animation System**: SpriteAnimator with 72x72 pixel frames
- [x] **Rendering**: GamePanel with GridPane-based layout
- [x] **Game Loop**: AnimationTimer framework in place
- [x] **Audio System**: Complete AudioManager with sound effects and background music

### Core Gameplay Elements
- [x] **Player class** with movement and input handling
- [x] **All Enemy classes** (Goblin, Skeleton, SkeletonBrute, BoomerGoblin)
- [x] **Game systems** (SpawnSystem, Pathfinder, UpgradeManager)
- [x] **Level selection** with multiple modes
- [x] **Grid-based movement system** with push mechanics
- [x] **Animation system** with directional sprites
- [x] **Event system** with GameUpdateEvent for UI updates
- [x] **Advanced Push Mechanics**: Telekinetic pushes, wave pushes (area upgrade), and chain pushing
- [x] **Upgrade System**: Health, push range, push strength, and area push upgrades
- [x] **Trap System**: Bear traps that activate when enemies or player step on them
- [x] **Book Collection**: Upgrade opportunity system between waves
- [x] **Special Mechanics**: Explosion mechanics for BoomerGoblins, invulnerability frames

### Current Game Logic Approach
- [x] **GameLogic** - Grid-based implementation with 14x9 grid (72x72 tile size)
- [x] **GameLauncher** - Orchestration system with level selection
- [x] **InputHandler** - WASD/arrow key movement with spacebar push
- [x] **GamePanel** - Grid-based rendering with animated sprites
- [x] **SpriteAnimator** - Frame-based animation system
- [x] **AudioManager** - Complete audio system with per-enemy sound effects and background music

### Notable Implementation Details
The project uses a grid-based approach with:
- 14x9 grid (72x72 pixel tiles) for efficient collision detection
- A* pathfinding for enemy navigation
- SpriteAnimator system for character animations
- Fixed spawn points around grid edges
- Wave-based progression with multiple levels and endless mode
- Push mechanics that affect adjacent enemies with upgrade options
- Advanced mechanics like telekinetic pushes (with range upgrades), wave pushes (area upgrade), and chain pushing
- Book collection system for upgrade opportunities between waves
- Trap mechanics with bear traps that activate when stepped on
- Complete audio integration with per-enemy sound effects and background music

---

## Notes for Future Development

When continuing development on this project:
1. **Additional game modes** - Create new challenge variations
2. **Enhanced UI/UX** - Add more visual polish and effects
3. **Balance gameplay** - Test and adjust difficulty curves further
4. **Optimize performance** - Profile and improve rendering efficiency
5. **Save/load system** - Add progress persistence
6. **Particle effects** - Add enhanced visual polish

---

*Last Updated: 2025-12-13*
*Version: 1.0*