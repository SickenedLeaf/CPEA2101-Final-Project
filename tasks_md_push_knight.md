# TASKS.md - Push Knight Peril

**Instructions**: Check off tasks as you complete them. Add new tasks as they're discovered. Update regularly.

**Status Legend**:
- [ ] Not Started
- [x] Completed
- [~] In Progress (change to [ ] or [x] when status changes)

---

## Milestone 1: Project Foundation & Setup

### Project Structure
- [ ] Initialize Maven project with correct structure
- [ ] Create pom.xml with JavaFX dependencies
- [ ] Set up package structure (com.pushknight.*)
- [ ] Create all main package folders (entities, systems, controllers, views, utils)
- [ ] Set up resources folder structure (images, sounds, css, fxml)
- [ ] Create .gitignore file for Java/Maven project
- [ ] Initialize Git repository
- [ ] Create README.md with build instructions

### Core Utilities & Constants
- [ ] Create Constants.java with game configuration
  - [ ] Define window dimensions (WIDTH, HEIGHT)
  - [ ] Define game timing constants (FPS, FRAME_TIME)
  - [ ] Define movement speeds and cooldowns
  - [ ] Define spawn rates and wave timing
  - [ ] Define enemy properties (health, damage, speeds)
- [ ] Create GameState enum (MENU, PLAYING, PAUSED, GAME_OVER, UPGRADE_SELECTION)
- [ ] Create Direction enum (UP, DOWN, LEFT, RIGHT, NONE)
- [ ] Create EnemyType enum (SKELETON, GOBLIN, SKELETON_BRUTE, BOOMER_GOBLIN)
- [ ] Create Vector2D utility class
  - [ ] Implement x, y fields
  - [ ] Implement add, subtract, multiply methods
  - [ ] Implement normalize, distance, magnitude methods

### Base Entity Classes
- [ ] Create Entity abstract class
  - [ ] Add protected fields: x, y, width, height
  - [ ] Add abstract update(long now) method
  - [ ] Add abstract render(GraphicsContext gc) method
  - [ ] Add getBounds() method returning Rectangle2D
  - [ ] Add getX(), getY(), setX(), setY() methods
- [ ] Create Updatable interface
- [ ] Create Renderable interface
- [ ] Create Collidable interface
  - [ ] Add collidesWith(Collidable other) method
  - [ ] Add getBounds() method
- [ ] Create Damageable interface
  - [ ] Add takeDamage(int amount) method
  - [ ] Add isAlive() method
  - [ ] Add getHealth() method

### Main Application Setup
- [ ] Create Main.java extending Application
- [ ] Set up primary Stage with title "Push Knight Peril"
- [ ] Create main Scene with specified dimensions
- [ ] Add window close handler
- [ ] Set up basic Canvas or Scene Graph rendering
- [ ] Implement AnimationTimer for game loop
- [ ] Test window opens and game loop runs

---

## Milestone 2: Player Character & Movement

### Player Entity
- [ ] Create Character abstract class extending Entity
  - [ ] Add health, maxHealth fields
  - [ ] Implement Damageable interface
  - [ ] Add takeDamage() method
  - [ ] Add heal() method
  - [ ] Add isAlive() method
- [ ] Create Player class extending Character
  - [ ] Initialize player sprite/shape
  - [ ] Set starting position (center of screen)
  - [ ] Set player dimensions
  - [ ] Set initial health (100)
  - [ ] Implement update() method
  - [ ] Implement render() method (draw rectangle or load sprite)
  - [ ] Add score field and methods

### Movement System
- [ ] Create MovementComponent class
  - [ ] Add speed field
  - [ ] Add cooldownTime field
  - [ ] Add lastMoveTime field
  - [ ] Implement canMove() method (check cooldown)
  - [ ] Implement move(Entity entity, Direction dir) method
  - [ ] Implement movement validation (stay in bounds)
- [ ] Integrate MovementComponent into Player
- [ ] Add movementCooldown from PRD requirements
- [ ] Test player stays within screen bounds

### Input Handling
- [ ] Set up keyboard input listeners in GameController
  - [ ] Handle WASD keys for movement
  - [ ] Handle Arrow keys for movement
  - [ ] Handle ESC for pause
  - [ ] Handle SPACE for push ability (placeholder)
- [ ] Test input responsiveness
- [ ] Test movement cooldown feels responsive

### Camera/View Setup
- [ ] Ensure player is always visible
- [ ] Add basic UI overlay for health display
- [ ] Test rendering performance with player movement

---

## Milestone 3: Enemy System Implementation

### Base Enemy Classes
- [ ] Create Enemy abstract class extending Character
  - [ ] Add damage field
  - [ ] Add speed field
  - [ ] Add target reference (Player)
  - [ ] Add abstract onDeath() method
  - [ ] Implement basic render() method
- [ ] Create NormalEnemy abstract class extending Enemy
  - [ ] Add AI behavior field
  - [ ] Implement basic chase behavior
  - [ ] Add collision detection with player
  - [ ] Add damage-to-player on collision

### Skeleton Enemy (Basic)
- [ ] Create Skeleton class extending NormalEnemy
  - [ ] Set health = 30
  - [ ] Set damage = 10
  - [ ] Set speed = 1.5
  - [ ] Implement simple chase AI
  - [ ] Implement render() with visual representation
  - [ ] Implement onDeath() (just remove from list)
- [ ] Test Skeleton spawning manually
- [ ] Test Skeleton chasing player
- [ ] Test Skeleton collision and damage

### Goblin Enemy (Variant)
- [ ] Create Goblin class extending NormalEnemy
  - [ ] Set health = 20
  - [ ] Set damage = 15
  - [ ] Set speed = 2.0 (faster than Skeleton)
  - [ ] Implement aggressive chase AI
  - [ ] Implement render() with different appearance
  - [ ] Implement onDeath()
- [ ] Test Goblin behavior differs from Skeleton
- [ ] Test both enemy types can exist simultaneously

### SkeletonBrute Enemy (Obstacle)
- [ ] Create SkeletonBrute class extending NormalEnemy
  - [ ] Set health = 80
  - [ ] Set damage = 20
  - [ ] Set speed = 0.8 (slower but tankier)
  - [ ] Add isObstacle = true field (from PRD)
  - [ ] Implement obstacle behavior (blocks other enemies)
  - [ ] Implement render() with larger size
  - [ ] Implement onDeath()
- [ ] Implement obstacle collision (other enemies can't pass through)
- [ ] Test SkeletonBrute blocks paths
- [ ] Test player can push SkeletonBrute

### BoomerGoblin Enemy (Special)
- [ ] Create BoomerGoblin class extending NormalEnemy
  - [ ] Set health = 25
  - [ ] Set damage = 30 (explodes on death)
  - [ ] Set speed = 1.8
  - [ ] Implement suicide-rush behavior
  - [ ] Implement explosion on death (area damage)
  - [ ] Implement render() with distinctive look
  - [ ] Add visual warning before explosion
- [ ] Test BoomerGoblin explosion damages player in radius
- [ ] Test explosion can damage other enemies
- [ ] Balance explosion radius and damage

### Enemy Factory
- [ ] Create EnemyFactory class
  - [ ] Implement createEnemy(EnemyType, x, y) method
  - [ ] Use switch statement for enemy types
  - [ ] Return appropriate enemy instance
- [ ] Test factory creates all enemy types correctly

### Enemy Pool (Optimization)
- [ ] Create EnemyPool class
  - [ ] Create Queue<Enemy> for available enemies
  - [ ] Implement acquire(EnemyType) method
  - [ ] Implement release(Enemy) method
  - [ ] Implement reset() for reused enemies
- [ ] Integrate pool into spawning system
- [ ] Test performance improvement with pooling

---

## Milestone 4: Game Director & Spawning

### Game Director Core
- [ ] Create GameDirector class
  - [ ] Implement Singleton pattern
  - [ ] Add currentWave field
  - [ ] Add enemiesAlive counter
  - [ ] Add spawnRate field (from PRD)
  - [ ] Add waveActive boolean
  - [ ] Add timeSinceLastSpawn tracking
- [ ] Implement startWave(int waveNumber) method
- [ ] Implement endWave() method
- [ ] Implement update(long now) method for spawn timing

### Spawn System
- [ ] Implement spawn position calculation (edges of screen)
  - [ ] Random spawn from top edge
  - [ ] Random spawn from bottom edge
  - [ ] Random spawn from left edge
  - [ ] Random spawn from right edge
- [ ] Implement spawnEnemy(EnemyType) method
  - [ ] Use EnemyFactory to create enemy
  - [ ] Set spawn position
  - [ ] Add to active enemies list
  - [ ] Increment enemiesAlive counter
- [ ] Implement despawnEnemy(Enemy) method
  - [ ] Remove from active enemies
  - [ ] Return to pool if using pooling
  - [ ] Decrement enemiesAlive counter

### Wave Progression
- [ ] Create SpawnStrategy interface
  - [ ] Add generateWave(int waveNumber) method
- [ ] Create EarlyGameStrategy (waves 1-3)
  - [ ] Spawn mostly Skeletons
  - [ ] Low spawn rate
  - [ ] Few total enemies
- [ ] Create MidGameStrategy (waves 4-7)
  - [ ] Mix of Skeletons and Goblins
  - [ ] Introduce SkeletonBrute
  - [ ] Increase spawn rate
- [ ] Create LateGameStrategy (waves 8+)
  - [ ] All enemy types including BoomerGoblin
  - [ ] High spawn rate
  - [ ] Multiple enemies simultaneously
- [ ] Implement strategy selection based on wave number
- [ ] Test wave difficulty increases appropriately

### Wave Completion
- [ ] Detect when all enemies defeated
- [ ] Trigger wave complete event
- [ ] Display wave completion UI
- [ ] Transition to upgrade selection screen
- [ ] Test wave cycle (spawn → defeat → upgrade → next wave)

---

## Milestone 5: Collision & Combat System

### Collision Detection
- [ ] Create CollisionSystem class
  - [ ] Implement checkCollision(Entity a, Entity b) method
  - [ ] Use Rectangle2D.intersects() for AABB collision
  - [ ] Optimize with spatial partitioning if needed
- [ ] Implement player-enemy collision detection
  - [ ] Player takes damage when hit
  - [ ] Apply damage cooldown to prevent instant death
- [ ] Implement enemy-enemy collision (for obstacles)
- [ ] Implement enemy-trap collision
- [ ] Test collision accuracy

### Combat Mechanics
- [ ] Implement player push ability
  - [ ] Add push force calculation
  - [ ] Apply force to nearby enemies
  - [ ] Add cooldown to push ability
  - [ ] Add visual feedback for push
- [ ] Implement enemy death handling
  - [ ] Call onDeath() method
  - [ ] Trigger death animation
  - [ ] Award score to player
  - [ ] Remove from active enemies
- [ ] Implement player death handling
  - [ ] Transition to game over screen
  - [ ] Display final score and wave reached
- [ ] Test combat feels satisfying

### Damage System
- [ ] Create DamageCalculator utility class
  - [ ] Calculate damage with upgrades applied
  - [ ] Handle critical hits (if implemented)
  - [ ] Apply damage modifiers
- [ ] Add damage numbers popup (visual feedback)
- [ ] Add hit flash effect on entities
- [ ] Test damage calculation with various upgrades

---

## Milestone 6: Trap System

### Trap Entity
- [ ] Create GameObject abstract class extending Entity
- [ ] Create Trap class extending GameObject
  - [ ] Add trap type field
  - [ ] Add damage field
  - [ ] Add cooldown field
  - [ ] Add activated state
  - [ ] Implement update() method (cooldown management)
  - [ ] Implement render() method
  - [ ] Implement trigger(Enemy) method
- [ ] Test trap spawning at specific positions

### Trap Types
- [ ] Create SpikeTrap
  - [ ] Deals instant damage
  - [ ] Has cooldown between activations
  - [ ] Visual indicator when active/cooling down
- [ ] Create SlowTrap (optional)
  - [ ] Reduces enemy speed
  - [ ] Temporary effect
- [ ] Create ExplosiveTrap (optional)
  - [ ] Area of effect damage
  - [ ] Destroys after use

### Trap Placement
- [ ] Implement trap placement system
  - [ ] Player can place traps at current position (or click position)
  - [ ] Limited number of traps available
  - [ ] Traps persist until used
- [ ] Add trap UI showing available traps
- [ ] Test trap placement and activation
- [ ] Test enemy interaction with traps

### Trap Strategy
- [ ] Balance trap damage vs enemy health
- [ ] Ensure traps are useful but not overpowered
- [ ] Test trap placement strategy is meaningful
- [ ] Add upgrade options for traps (more traps, better traps)

---

## Milestone 7: Upgrade System

### Upgrade Base Classes
- [ ] Create Upgrade abstract class
  - [ ] Add name field
  - [ ] Add description field
  - [ ] Add icon/image field
  - [ ] Add rarity field (optional)
  - [ ] Add abstract apply(Player player) method
  - [ ] Add abstract remove(Player player) method (if temporary)
- [ ] Create UpgradeType enum listing all upgrade types

### Specific Upgrades
- [ ] Create SpeedUpgrade
  - [ ] Increases movement speed by 20%
  - [ ] Update player MovementComponent
- [ ] Create HealthUpgrade
  - [ ] Increases max health by 25
  - [ ] Heals player to full
- [ ] Create DamageUpgrade
  - [ ] Increases push damage by 50%
  - [ ] Affects all enemy types
- [ ] Create CooldownReductionUpgrade
  - [ ] Reduces movement cooldown by 15%
- [ ] Create ExtraLifeUpgrade
  - [ ] Adds 1 extra life
  - [ ] Revives player on death
- [ ] Create TrapUpgrade
  - [ ] Increases trap capacity or damage
- [ ] Create AreaPushUpgrade
  - [ ] Increases push ability radius
- [ ] Create MultiPushUpgrade
  - [ ] Push hits multiple times
- [ ] Test each upgrade applies correctly

### Upgrade Factory
- [ ] Create UpgradeFactory class
  - [ ] Implement createUpgrade(UpgradeType) method
  - [ ] Return new upgrade instance
- [ ] Test factory creates all upgrade types

### Upgrade Manager
- [ ] Create UpgradeManager class
  - [ ] Implement Singleton pattern
  - [ ] Track available upgrades
  - [ ] Track applied upgrades to player
  - [ ] Implement getRandomUpgrades(int count) method
  - [ ] Implement applyUpgrade(Upgrade, Player) method
  - [ ] Ensure no duplicate upgrades in same wave (or handle stacking)
- [ ] Test upgrade selection logic
- [ ] Test upgrade effects stack properly

### Upgrade UI
- [ ] Create UpgradeController class
  - [ ] Handle upgrade selection screen
  - [ ] Display 3 random upgrades after wave
  - [ ] Handle player selection
  - [ ] Apply selected upgrade
  - [ ] Return to game
- [ ] Create UpgradeView class
  - [ ] Display upgrade cards/buttons
  - [ ] Show upgrade name and description
  - [ ] Show upgrade icon
  - [ ] Highlight on hover
  - [ ] Animate selection
- [ ] Test upgrade selection flow
- [ ] Ensure upgrades are balanced and interesting

---

## Milestone 8: AI System

### AI Behavior Interface
- [ ] Create AIBehavior interface
  - [ ] Add execute(Enemy enemy, Player target) method

### Basic AI Behaviors
- [ ] Create ChasePlayerBehavior
  - [ ] Calculate direction to player
  - [ ] Move toward player at enemy speed
  - [ ] Handle pathfinding around obstacles (basic)
- [ ] Create PatrolBehavior (optional)
  - [ ] Move in set pattern
  - [ ] Switch to chase when player nearby
- [ ] Create FleePlayerBehavior (optional)
  - [ ] Move away from player
  - [ ] For certain enemy types

### Advanced AI Behaviors
- [ ] Create SurroundPlayerBehavior
  - [ ] Multiple enemies coordinate
  - [ ] Try to encircle player
- [ ] Create KamikazeBehavior (for BoomerGoblin)
  - [ ] Rush directly at player
  - [ ] Explode on contact or death
- [ ] Create GuardPositionBehavior (for SkeletonBrute)
  - [ ] Stay in area as obstacle
  - [ ] Block paths

### Pathfinding
- [ ] Implement basic pathfinding algorithm
  - [ ] A* or simple line-of-sight pathfinding
  - [ ] Handle obstacle avoidance
  - [ ] Optimize for performance
- [ ] Test enemies navigate around obstacles
- [ ] Test enemies don't get stuck on each other

### AI Assignment
- [ ] Assign appropriate AI to each enemy type
  - [ ] Skeleton: ChasePlayerBehavior
  - [ ] Goblin: Aggressive chase (faster)
  - [ ] SkeletonBrute: Guard or slow chase
  - [ ] BoomerGoblin: KamikazeBehavior
- [ ] Test each enemy AI feels distinct
- [ ] Balance AI difficulty

---

## Milestone 9: UI & Menus

### Main Menu
- [ ] Create MenuController class
- [ ] Create main menu screen
  - [ ] "Start Game" button
  - [ ] "How to Play" button
  - [ ] "Settings" button (optional)
  - [ ] "Quit" button
- [ ] Implement button click handlers
- [ ] Add background image or animation
- [ ] Add title logo
- [ ] Test navigation from menu to game

### HUD (Heads-Up Display)
- [ ] Create GameView class for HUD rendering
- [ ] Display player health bar
  - [ ] Current health / max health
  - [ ] Color-coded (green → yellow → red)
- [ ] Display current wave number
- [ ] Display score
- [ ] Display enemies remaining (optional)
- [ ] Display active upgrades icons
- [ ] Display trap count
- [ ] Test HUD updates in real-time

### Pause Menu
- [ ] Create pause menu overlay
  - [ ] "Resume" button
  - [ ] "Restart" button
  - [ ] "Main Menu" button
  - [ ] Display current stats
- [ ] Pause game logic when menu open
- [ ] Test pause/resume functionality

### Game Over Screen
- [ ] Create game over screen
  - [ ] Display "Game Over" message
  - [ ] Show final score
  - [ ] Show wave reached
  - [ ] Show total enemies defeated
  - [ ] Show time survived
  - [ ] "Retry" button
  - [ ] "Main Menu" button
- [ ] Test game over triggers on player death
- [ ] Test retry restarts game properly

### How to Play Screen
- [ ] Create tutorial/instructions screen
  - [ ] Explain movement controls
  - [ ] Explain push ability
  - [ ] Explain trap usage
  - [ ] Explain upgrade system
  - [ ] Show enemy types and behaviors
- [ ] Add "Back" button to return to menu

### Settings Screen (Optional)
- [ ] Create settings screen
  - [ ] Volume sliders (music, SFX)
  - [ ] Difficulty selection
  - [ ] Key binding options
  - [ ] Toggle fullscreen
- [ ] Save settings to file
- [ ] Load settings on startup

---

## Milestone 10: Visual Polish

### Sprites & Graphics
- [ ] Create or find player sprite
  - [ ] Idle animation
  - [ ] Walking animation (4 directions)
  - [ ] Damaged animation
- [ ] Create or find Skeleton sprite
  - [ ] Walking animation
  - [ ] Death animation
- [ ] Create or find Goblin sprite
  - [ ] Walking animation
  - [ ] Death animation
- [ ] Create or find SkeletonBrute sprite
  - [ ] Larger size
  - [ ] Idle/walking animation
- [ ] Create or find BoomerGoblin sprite
  - [ ] Walking animation
  - [ ] Explosion warning animation
  - [ ] Explosion effect
- [ ] Create or find trap sprites
- [ ] Create background image or tileset
- [ ] Create UI button graphics

### Animations
- [ ] Implement sprite sheet animation system
  - [ ] AnimationController class
  - [ ] Frame timing
  - [ ] Loop animations
- [ ] Add movement animations to all entities
- [ ] Add death animations
- [ ] Add damage flash effect
- [ ] Add push ability visual effect
- [ ] Add explosion particle effect (BoomerGoblin)
- [ ] Test animations are smooth

### Particle Effects
- [ ] Create ParticleSystem class (optional)
  - [ ] Particle emitter
  - [ ] Particle lifecycle
  - [ ] Render particles
- [ ] Add blood splatter on enemy death (optional)
- [ ] Add dust clouds on movement
- [ ] Add sparkles on upgrade selection
- [ ] Test particle performance

### Screen Shake & Juice
- [ ] Implement screen shake effect
  - [ ] On enemy death
  - [ ] On player damage
  - [ ] On explosion
- [ ] Add hit pause (brief freeze frame)
- [ ] Add damage numbers floating up
- [ ] Test effects enhance gameplay feel

---

## Milestone 11: Audio & Sound

### Sound Effects
- [ ] Find or create sound effects
  - [ ] Player movement (footsteps)
  - [ ] Player push ability
  - [ ] Enemy hit sounds (per type)
  - [ ] Enemy death sounds (per type)
  - [ ] Player damage sound
  - [ ] Trap activation sound
  - [ ] Explosion sound (BoomerGoblin)
  - [ ] UI click sounds
  - [ ] Upgrade selection sound
  - [ ] Wave complete sound
  - [ ] Game over sound

### Audio System
- [ ] Create AudioManager class (Singleton)
  - [ ] Load all audio files
  - [ ] Play sound effect method
  - [ ] Volume control
  - [ ] Prevent overlapping sounds (limit instances)
- [ ] Integrate sound effects into gameplay
  - [ ] Play on appropriate events
  - [ ] Test timing of sounds
- [ ] Test audio doesn't lag gameplay

### Music
- [ ] Find or create background music
  - [ ] Menu theme
  - [ ] Gameplay theme (can loop)
  - [ ] Boss wave theme (optional, for later waves)
  - [ ] Game over theme
- [ ] Implement music playback
  - [ ] Loop music tracks
  - [ ] Fade in/out between tracks
  - [ ] Volume control separate from SFX
- [ ] Test music transitions smoothly

---

## Milestone 12: Game Balance & Testing

### Balance Tuning
- [ ] Balance player health and damage
  - [ ] Player shouldn't die too quickly
  - [ ] Player should feel challenged
- [ ] Balance enemy stats
  - [ ] Health appropriate for enemy type
  - [ ] Damage feels fair
  - [ ] Speed allows player to react
- [ ] Balance spawn rates
  - [ ] Early waves are learnable
  - [ ] Difficulty ramps smoothly
  - [ ] Late waves are intense but not impossible
- [ ] Balance upgrades
  - [ ] All upgrades are useful
  - [ ] No single upgrade dominates
  - [ ] Upgrades create interesting synergies
- [ ] Balance trap effectiveness
  - [ ] Traps are worth using
  - [ ] Not so powerful they trivialize game
- [ ] Playtest extensively
  - [ ] Record feedback
  - [ ] Adjust based on testing

### Bug Fixing
- [ ] Fix collision detection issues
  - [ ] Entities getting stuck
  - [ ] Incorrect collision responses
- [ ] Fix spawning bugs
  - [ ] Enemies spawning inside walls
  - [ ] Too many/too few enemies
- [ ] Fix UI bugs
  - [ ] Buttons not responding
  - [ ] Text rendering issues
  - [ ] Overlapping UI elements
- [ ] Fix memory leaks
  - [ ] Properly dispose of entities
  - [ ] Clean up listeners
  - [ ] Profile memory usage
- [ ] Fix performance issues
  - [ ] Optimize rendering
  - [ ] Optimize collision detection
  - [ ] Profile and fix bottlenecks

### Testing Checklist
- [ ] Test all enemy types spawn and behave correctly
- [ ] Test all upgrades apply correctly
- [ ] Test all UI screens navigate properly
- [ ] Test game over and restart work
- [ ] Test pause and resume work
- [ ] Test traps work as intended
- [ ] Test long play sessions (memory leaks?)
- [ ] Test edge cases (0 health, max upgrades, etc.)
- [ ] Test on different systems/resolutions
- [ ] Get feedback from others

---

## Milestone 13: Documentation & Finalization

### Code Documentation
- [ ] Add Javadoc comments to all public classes
- [ ] Add Javadoc comments to all public methods
- [ ] Document complex algorithms
- [ ] Add inline comments for tricky code
- [ ] Review code for clarity

### User Documentation
- [ ] Write comprehensive README.md
  - [ ] Project description
  - [ ] Features list
  - [ ] Installation instructions
  - [ ] Build instructions
  - [ ] How to play
  - [ ] Controls reference
  - [ ] Credits
- [ ] Create in-game tutorial (or enhance How to Play)
- [ ] Write changelog documenting versions

### Project Cleanup
- [ ] Remove unused code and files
- [ ] Remove debug logging statements
- [ ] Ensure consistent code style
- [ ] Organize imports
- [ ] Remove TODO comments (or convert to GitHub issues)

### Build & Distribution
- [ ] Create executable JAR file
  - [ ] Configure Maven to include dependencies
  - [ ] Test JAR runs on clean system
- [ ] Create release build
  - [ ] Version number
  - [ ] Release notes
- [ ] Optional: Create installer (jpackage)
- [ ] Optional: Publish to GitHub Releases

### Final Testing
- [ ] Full playthrough from start to game over
- [ ] Test all features one more time
- [ ] Verify all tasks completed
- [ ] Get final feedback
- [ ] Make final adjustments

---

## Future Enhancements (Post-MVP)

### Stretch Goals
- [ ] Add boss enemies for milestone waves (10, 20, 30)
- [ ] Add more enemy types (archer, mage, etc.)
- [ ] Add more trap types
- [ ] Add power-up pickups during gameplay
- [ ] Add combo system (chaining enemy defeats)
- [ ] Add achievements system
- [ ] Add leaderboard (local or online)
- [ ] Add different game modes (endless, timed, etc.)
- [ ] Add multiple playable characters
- [ ] Add procedurally generated arenas
- [ ] Add multiplayer co-op (local or online)
- [ ] Add better pathfinding (A* with dynamic obstacles)
- [ ] Add save/load game progress
- [ ] Add persistent stats tracking
- [ ] Localization (multiple languages)

---

## Discovered Tasks

*Add new tasks here as you discover them during development*

- [ ] 

---

**Last Updated**: [Date]
**Total Tasks**: 200+
**Completed**: 0