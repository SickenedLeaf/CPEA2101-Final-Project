# Push Knight Peril

A 2D survival game built with Java and JavaFX where players control a knight who must survive waves of enemies using tactical positioning, timing, and strategic upgrades.

## Game Overview

Push Knight Peril is a survival game where you:
- **Survive** - Avoid or defeat incoming enemy waves
- **Push** - Use the knight's push ability to move enemies into traps
- **Upgrade** - Select power-ups between waves to become stronger
- **Progress** - Face increasingly difficult waves with new enemy types

## Features

- Multiple enemy types with unique behaviors
- Wave-based difficulty progression
- Upgrade system with meaningful choices
- Trap mechanics for strategic gameplay
- Smooth 60 FPS gameplay

## Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher
- **JavaFX**: Included via Maven dependencies

## Building the Project

### Using Maven

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd CPEA2101-Final-Project
   ```

2. Build the project:
   ```bash
   mvn clean compile
   ```

3. Run the game:
   ```bash
   mvn javafx:run
   ```

   Or alternatively:
   ```bash
   mvn clean package
   java -jar target/push-knight-peril-1.0.0.jar
   ```

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── pushknight/
│   │           ├── Main.java
│   │           ├── entities/      # Game entities (Player, Enemies, etc.)
│   │           ├── systems/       # Game systems (GameDirector, etc.)
│   │           ├── controllers/   # Input and game flow controllers
│   │           ├── views/         # UI and rendering views
│   │           └── utils/         # Utilities (Constants, Vector2D, etc.)
│   └── resources/
│       ├── images/    # Sprite images
│       ├── sounds/    # Sound effects
│       ├── css/       # Stylesheets
│       └── fxml/      # FXML layouts (if used)
└── test/
    └── java/          # Unit tests
```

## Controls

- **WASD** or **Arrow Keys**: Move player
- **SPACE**: Push ability
- **ESC**: Pause game

## Development Status

This project is currently in active development. See `TASKS.md` for detailed progress tracking.

## License

[Add your license here]

## Credits

[Add credits here]

