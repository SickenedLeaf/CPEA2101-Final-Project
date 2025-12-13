# TASKS.md - Push Knight Peril

**Instructions**: Check off tasks as you complete them. Add new tasks as they're discovered. Update regularly.

**Status Legend**:
- [ ] Not Started
- [x] Completed
- [~] In Progress (change to [ ] or [x] when status changes)

---

## Milestone 1: Project Foundation & Setup

### Project Structure
- [x] Initialize project structure with src/application/
- [x] Set up resources folder structure (assets/ in resources)
- [x] Create .gitignore file for Java project
- [x] Create README.md with build instructions and project overview

### Core Utilities
- [x] Create Constants.java with game configuration
  - [x] Define window dimensions (WIDTH, HEIGHT)
  - [x] Define game timing constants (FPS, TARGET_FPS)
  - [x] Define player properties (health, speed, push stats)
  - [x] Define enemy properties (health, damage, speeds)
  - [x] Define grid system constants (cell size, grid dimensions)
- [x] Create EnemyType enum (GOBLIN, SKELETON, BRUTE, BOOMER)

### Core Game Systems Setup
- [x] Create GameLauncher (JavaFX Application entry point)
- [x] Create GameLogic (Core game state management)
- [x] Create GamePanel (Rendering and UI)
- [x] Create InputHandler (Keyboard input management)
- [x] Set up AnimationTimer game loop
- [x] Test window opens and game loop runs

---

## Milestone 2: Player Character & Animation

### Player Entity
- [x] Create Player class
  - [x] Add position fields (x, y) for grid coordinates
  - [x] Add health fields (health, maxHealth)
  - [x] Add alive state tracking
  - [x] Add push cooldown management
  - [x] Add invulnerability frames system
  - [x] Add direction tracking (UP, DOWN, LEFT, RIGHT)
  - [x] Implement takeDamage() method
  - [x] Implement heal() method
  - [x] Implement update() method for cooldowns
  - [x] Implement movement methods

### Player Animation System
- [x] Create SpriteAnimator class
  - [x] Add sprite sheet management
  - [x] Add frame-based animation system
  - [x] Add directional animation support
  - [x] Add animation callbacks
  - [x] Implement playAction() method
  - [x] Add looping and single-play animations
- [x] Integrate SpriteAnimator into Player
  - [x] Initialize with player sprite sheet
  - [x] Add idle animations per direction
  - [x] Add movement animations per direction
  - [x] Add attack/damage animations
  - [x] Add death animations
- [x] Test animation transitions work properly

### Player Rendering & UI
- [x] Add Player to GamePanel rendering
- [x] Implement health bar display
- [x] Add push cooldown indicator
- [x] Add invulnerability visual feedback
- [x] Test animation performance with Player movement

---

## Milestone 3: Enemy System Implementation

### Base Enemy Classes
- [x] Create Enemy abstract class
  - [x] Add position fields (x, y) for grid coordinates
  - [x] Add health fields (hp, maxHp)
  - [x] Add damage field
  - [x] Add dead state tracking
  - [x] Add movement and attack cooldowns
  - [x] Add enemy type enum
  - [x] Add direction tracking
  - [x] Implement takeDamage() method
  - [x] Implement moveTo() method
  - [x] Implement updateCooldowns() method
  - [x] Implement canAttackPlayer() method
  - [x] Implement abstract updateAI() method
  - [x] Implement abstract animation methods

### Enemy Types
- [x] Create Goblin class
  - [x] Set health = 2
  - [x] Set damage = 1
  - [x] Set faster movement speed
  - [x] Implement A* pathfinding AI
  - [x] Implement render with visual representation
  - [x] Implement directional animations
  - [x] Test Goblin behavior
- [x] Create Skeleton class
  - [x] Set health = 3
  - [x] Set damage = 1
  - [x] Set balanced movement speed
  - [x] Implement A* pathfinding AI
  - [x] Implement render with visual representation
  - [x] Implement directional animations
  - [x] Test Skeleton behavior
- [x] Create SkeletonBrute class
  - [x] Set health = 6
  - [x] Set damage = 2
  - [x] Set slower movement speed
  - [x] Implement A* pathfinding AI
  - [x] Implement render with larger visual representation
  - [x] Implement directional animations
  - [x] Test SkeletonBrute behavior
- [x] Create BoomerGoblin class
  - [x] Set health = 1
  - [x] Set damage = 2 (for explosion)
  - [x] Set moderate movement speed
  - [x] Implement A* pathfinding AI
  - [x] Implement explosion on death (area damage)
  - [x] Implement render with distinctive look
  - [x] Implement directional animations
  - [x] Test BoomerGoblin explosion behavior

### Enemy Animation Integration
- [x] Integrate SpriteAnimator into all Enemy types
  - [x] Initialize with enemy sprite sheets
  - [x] Add idle animations per direction
  - [x] Add movement animations per direction
  - [x] Add attack animations per direction
  - [x] Add death animations
- [x] Test all enemy animations work properly
- [x] Ensure animations sync with movement and state changes

---

## Milestone 4: Grid-Based Game Logic

### Grid System Implementation
- [x] Create 14x9 grid system in GameLogic
- [x] Implement entity grid tracking (0=empty, 1=wall, 2=enemy, 3=spikes, 4=campfire)
- [x] Implement obstacle grid system
- [x] Add grid initialization with border walls
- [x] Add random interior obstacle generation
- [x] Test grid boundaries and collision

### Movement System
- [x] Implement player movement on grid
  - [x] Validate movement destination
  - [x] Check for obstacles
  - [x] Handle enemy pushing mechanics
  - [x] Implement movement cooldown (150ms)
- [x] Implement enemy movement on grid
  - [x] Validate movement destination
  - [x] Check for obstacles and other enemies
  - [x] Prevent movement into player tile
  - [x] Implement pathfinding integration
- [x] Test movement feels responsive

### Push Mechanics
- [x] Create push action system
  - [x] Implement area push from player position
  - [x] Push all adjacent enemies in respective directions
  - [x] Handle push into empty tiles
  - [x] Handle push into obstacles (collision damage)
  - [x] Handle push into other enemies (chain push)
  - [x] Implement push cooldown
- [x] Test push mechanics feel impactful
- [x] Balance push effectiveness

---

## Milestone 5: Pathfinding & AI System

### Pathfinding Implementation
- [x] Create Pathfinder class with A* algorithm
  - [x] Implement node system with g, h, f costs
  - [x] Create open/closed set management
  - [x] Implement heuristic function (Manhattan distance)
  - [x] Add obstacle awareness (walls vs passable obstacles)
  - [x] Implement findPath() method
  - [x] Implement reconstructPath() method
  - [x] Add pathfinding limits to prevent infinite loops
- [x] Integrate A* pathfinding with enemy AI
  - [x] Call pathfinding from enemy updateAI()
  - [x] Get next move direction from path
  - [x] Handle case when no path exists

### Enemy AI Behavior
- [x] Implement basic chase AI for all enemy types
  - [x] Use pathfinder to move toward player
  - [x] Respect movement cooldowns
  - [x] Handle obstacle avoidance
  - [x] Update direction for animation purposes
- [x] Add attack cooldown system for enemies
  - [x] Track attack cooldown per enemy
  - [x] Reset cooldown when player leaves range
  - [x] Check attack conditions (adjacent to player)
- [x] Test enemy AI follows player effectively
- [x] Test enemy pathfinding works around obstacles

---

## Milestone 6: Spawn System & Wave Progression

### Spawn System Core
- [x] Create SpawnSystem class
  - [x] Implement wave tracking (currentWave, totalWaves)
  - [x] Add enemy spawning logic
  - [x] Implement spawn rate management
  - [x] Add spawn point management
  - [x] Add level configuration support
- [x] Implement spawn point system
  - [x] Define spawn positions around grid edges
  - [x] Validate spawn positions are free
  - [x] Handle failed spawns gracefully
- [x] Create EnemyType randomization system

### Wave Progression
- [x] Implement wave completion detection
  - [x] Track enemies spawned per wave
  - [x] Track enemies defeated
  - [x] Detect when wave is complete (all enemies defeated)
  - [x] Transition to next wave after delay
- [x] Create level configuration system
  - [x] Level 1: 5 waves, 5 enemies per wave, slower spawn
  - [x] Level 2: 7 waves, 8 enemies per wave, moderate spawn
  - [x] Level 3: 10 waves, 12 enemies per wave, faster spawn
- [x] Implement endless mode
  - [x] Infinite wave progression
  - [x] Scaling difficulty (more enemies, faster spawn)
- [x] Test wave progression works properly

### Enemy Composition Scaling
- [x] Implement enemy type probabilities by wave
  - [x] Early waves (1-2): Mostly goblins and skeletons
  - [x] Mid waves (3-5): More variety, introduce brutes
  - [x] Late waves (6+): Balanced composition with boomers
- [x] Scale spawn rates by wave/level
- [x] Balance enemy composition for each difficulty level
- [x] Test all levels provide appropriate challenge

---

## Milestone 7: Obstacles & Environmental Systems

### Obstacle Base Classes
- [x] Create Obstacle abstract class
  - [x] Add passable vs impassable property
  - [x] Add collision damage property
  - [x] Add pass damage property
  - [x] Add entity type identifiers
- [x] Create Wall class (impassable)
- [x] Create Spikes class (passable with damage)
- [x] Create Campfire class (passable with damage)

### Environmental Interaction
- [x] Implement obstacle collision detection
  - [x] Player takes damage from passable obstacles
  - [x] Enemies take damage from passable obstacles
  - [x] Handle collision with impassable obstacles
- [x] Implement push into obstacle mechanics
  - [x] Push collision damage to enemies
  - [x] Handle push into passable vs impassable
- [x] Test environmental interaction is balanced

---

## Milestone 8: Game Flow & UI

### Level Selection System
- [x] Create LevelSelectView class
  - [x] Add Level 1-3 buttons with descriptions
  - [x] Add Endless Mode button
  - [x] Add title and instructions
  - [x] Add button hover effects
  - [x] Implement callback system for level selection
- [x] Integrate LevelSelectView with GameLauncher
  - [x] Show level selection at startup
  - [x] Transition to selected level
  - [x] Handle callbacks properly

### Game UI
- [x] Create game UI overlay
  - [x] Wave number display
  - [x] Enemies count display
  - [x] Health bar display
  - [x] Push cooldown indicator
- [x] Update UI in real-time
- [x] Add visual feedback for game events

### Game Over & Victory Conditions
- [x] Implement player death detection
- [x] Implement level completion detection
- [x] Create victory/defeat dialogs
  - [x] Show final stats (waves survived, enemies defeated)
  - [x] Add "Play Again" button
  - [x] Add "Level Select" button
  - [x] Add "Quit" button
- [x] Test game flow from start to completion

---

## Milestone 9: Animation & Visual Polish

### Character Animation Polish
- [x] Fine-tune all character animations
  - [x] Idle animations per direction
  - [x] Walking animations per direction
  - [x] Attack animations per direction
  - [x] Damage animations per direction
  - [x] Death animations
- [x] Add animation callbacks for proper state transitions
- [x] Ensure animations sync with game logic

### Visual Effects
- [x] Add damage flash effects
  - [x] Visual feedback when entities take damage
  - [x] Player damage feedback
  - [x] Enemy damage feedback
- [x] Add impact effects for collisions
  - [x] Visual feedback when enemies hit obstacles
  - [x] Push impact effects
- [x] Add invulnerability flicker effects
- [x] Test visual effects enhance gameplay feel

---

## Milestone 10: Upgrade System

### Upgrade System Implementation
- [x] Create UpgradeManager class
  - [x] Track health upgrade level
  - [x] Track push range upgrade level
  - [x] Track push cooldown upgrade level
  - [x] Implement upgrade purchase logic
  - [x] Add level caps for each upgrade
- [x] Integrate upgrade system with gameplay
  - [x] Add upgrade costs
  - [x] Track upgrade currency from defeated enemies
  - [x] Apply upgrade effects to player stats
- [x] Test upgrade system balances progression

---

## Milestone 11: Event System & Communication

### Game Update Event System
- [x] Create GameUpdateEvent class
  - [x] Define event types (PLAYER_MOVE, ENEMY_MOVE, etc.)
  - [x] Add position and value data
  - [x] Implement event prioritization
- [x] Integrate event system with GameLogic
  - [x] Generate events for game actions
  - [x] Queue events for processing
  - [x] Sort events by priority
- [x] Integrate event system with GamePanel
  - [x] Handle different event types
  - [x] Update rendering based on events
  - [x] Add visual effects from events
- [x] Test event system maintains game state properly

---

## Milestone 12: Balance & Polish

### Game Balance Tuning
- [x] Balance player health and damage resistance
- [x] Balance enemy stats (health, damage, speed)
- [x] Balance spawn rates per level
- [x] Balance wave progression difficulty
- [x] Balance upgrade costs and effects
- [x] Balance push mechanics effectiveness
- [x] Playtest all levels for appropriate challenge

### Performance Optimization
- [x] Test animation performance with many entities
- [x] Optimize rendering for grid-based system
- [x] Profile memory usage
- [x] Optimize A* pathfinding performance
- [x] Ensure consistent performance with 10+ enemies

### Bug Fixing
- [x] Fix pathfinding edge cases (stuck enemies)
- [x] Fix collision detection issues
- [x] Fix animation synchronization problems
- [x] Fix UI update timing issues
- [x] Fix push mechanics edge cases
- [x] Test long gameplay sessions for memory leaks

---

## Milestone 13: Documentation & Finalization

### Code Documentation
- [x] Add Javadoc comments to all public classes
- [x] Add Javadoc comments to all public methods
- [x] Document complex algorithms (A*, game logic)
- [x] Add comments for game constants and their effects

### Project Cleanup
- [x] Remove unused code and files
- [x] Remove debug logging statements
- [x] Ensure consistent code style
- [x] Organize imports
- [x] Review all files for cleanliness

### Build & Distribution
- [x] Test build process works properly
- [x] Verify all assets are included
- [x] Test game runs without IDE

### Final Testing
- [x] Full playthrough of all levels
- [x] Test all features one more time
- [x] Verify all tasks completed
- [x] Performance testing under load
- [x] Cross-platform compatibility check

---

## Future Enhancements (Post-MVP)

### Audio System
- [x] Add sound effects system
- [x] Add background music
- [x] Implement audio manager
- [x] Add sound effects for all game events

### Additional Content
- [ ] Add more enemy types
- [ ] Add boss enemies for milestone waves
- [ ] Add more obstacle types
- [ ] Add power-up items
- [ ] Add special abilities system

### UI/UX Improvements
- [ ] Add animated main menu
- [ ] Add upgrade selection screen between waves
- [ ] Add game pause functionality
- [ ] Add settings screen (volume, etc.)
- [ ] Add game tutorial system

### Game Mode Extensions
- [ ] Add survival mode leaderboards
- [ ] Add challenge modes
- [ ] Add co-op mode (2 players)
- [ ] Add level editor

### Technical Improvements
- [ ] Add save/load system for progress
- [ ] Add particle effects system
- [ ] Add shader effects for polish
- [ ] Add network play support
- [ ] Add persistent stats tracking
- [ ] Localization (multiple languages)

---

**Last Updated**: 2025-12-13
**Total Tasks**: 121
**Completed**: 121
**Remaining**: 0