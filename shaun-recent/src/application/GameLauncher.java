package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameLauncher extends Application {
    private static final int WINDOW_WIDTH = 1008; // 14 tiles * 72px
    private static final int WINDOW_HEIGHT = 648; // 9 tiles * 72px

    private Stage primaryStage;
    private GameLogic logic;
    private GamePanel panel;
    private InputHandler input;
    private AnimationTimer gameLoop;

    private MainMenuView mainMenuView;
    private LevelSelectView levelSelectView;
    private SettingsView settingsView;

    private long lastPlayerMoveTime = 0;
    private static final double MIN_MOVE_INTERVAL_MS = 150;

    private boolean upgradeWindowShown = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Set minimum size to ensure the game is always visible
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        showMainMenu();

        primaryStage.setTitle("Push Knight Peril - Wave Survival");
        primaryStage.show();
    }
    
    private void showMainMenu() {
        // Stop the game loop and cleanup if in gameplay
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }

        // Clean up input handlers if they exist
        if (input != null) {
            input = null;
        }

        // Get current window dimensions to handle resize
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        // Use current size if window has been resized, otherwise use default
        double width = currentWidth > 0 ? currentWidth : WINDOW_WIDTH;
        double height = currentHeight > 0 ? currentHeight : WINDOW_HEIGHT;

        mainMenuView = new MainMenuView((int)width, (int)height);

        mainMenuView.setOnPlaySelected(this::showLevelSelect);
        mainMenuView.setOnSettingsSelected(this::showSettings);
        mainMenuView.setOnQuitSelected(() -> System.exit(0));

        // Play menu background music
        AudioManager.getInstance().playBackgroundMusic(false);
        // Log audio resource loading status once when showing main menu (diagnostic)
        AudioManager.getInstance().logLoadedClips();

        primaryStage.setScene(mainMenuView.getScene());
    }

    private void showSettings() {
        // Stop the game loop and cleanup if in gameplay
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }

        // Clean up input handlers if they exist
        if (input != null) {
            input = null;
        }

        // Get current window dimensions to handle resize
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        // Use current size if window has been resized, otherwise use default
        double width = currentWidth > 0 ? currentWidth : WINDOW_WIDTH;
        double height = currentHeight > 0 ? currentHeight : WINDOW_HEIGHT;

        settingsView = new SettingsView((int)width, (int)height);

        settingsView.setOnBackSelected(this::showMainMenu);

        // Play menu background music in settings (part of menu area)
        AudioManager.getInstance().playBackgroundMusic(false);

        primaryStage.setScene(settingsView.getScene());
    }

    private void showLevelSelect() {
        // Stop the game loop and cleanup if in gameplay
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }

        // Clean up input handlers if they exist
        if (input != null) {
            input = null;
        }

        // Get current window dimensions to handle resize
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        // Use current size if window has been resized, otherwise use default
        double width = currentWidth > 0 ? currentWidth : WINDOW_WIDTH;
        double height = currentHeight > 0 ? currentHeight : WINDOW_HEIGHT;

        levelSelectView = new LevelSelectView((int)width, (int)height);

        levelSelectView.setOnLevel1Selected(() -> startGame(1));
        levelSelectView.setOnLevel2Selected(() -> startGame(2));
        levelSelectView.setOnLevel3Selected(() -> startGame(3));
        levelSelectView.setOnEndlessModeSelected(() -> startGame(0));
        levelSelectView.setOnBack(() -> showMainMenu()); // Go back to main menu

        // Play menu background music for level selection
        AudioManager.getInstance().playBackgroundMusic(false);

        primaryStage.setScene(levelSelectView.getScene());
    }
    
    private void startGame(int levelNumber) {
        // Clean up any existing game resources
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }

        System.out.println("[GAME] Starting " +
            (levelNumber == 0 ? "Endless Mode" : "Level " + levelNumber));

        logic = new GameLogic(levelNumber);
        panel = new GamePanel(logic);
        input = new InputHandler();

        // Create game root with back button
        BorderPane gameRoot = new BorderPane();
        gameRoot.setCenter(panel.getGridView());

        // Create the back to menu button
        javafx.scene.control.Button backToMenuButton = new javafx.scene.control.Button("Menu");
        backToMenuButton.setStyle(
            "-fx-background-color: #1a1a2e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-border-color: #e94560; " +
            "-fx-border-width: 1px; " +
            "-fx-background-radius: 5; " +
            "-fx-border-radius: 5; " +
            "-fx-min-width: 60px;"
        );

        backToMenuButton.setOnAction(e -> {
            AudioManager.getInstance().playUiSound("CONFIRM");
            showMainMenu();
        });

        // Position the button in the upper right corner using AnchorPane
        javafx.scene.layout.AnchorPane anchorPane = new javafx.scene.layout.AnchorPane();
        anchorPane.getChildren().add(gameRoot.getCenter());
        anchorPane.getChildren().add(backToMenuButton);
        javafx.scene.layout.AnchorPane.setTopAnchor(backToMenuButton, 10.0);
        javafx.scene.layout.AnchorPane.setRightAnchor(backToMenuButton, 10.0);
        gameRoot.setCenter(anchorPane);

        // Get current window dimensions to handle resize
        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        // Use current size if window has been resized, otherwise use default
        double width = currentWidth > 0 ? currentWidth : WINDOW_WIDTH;
        double height = currentHeight > 0 ? currentHeight : WINDOW_HEIGHT;

        Scene gameScene = new Scene(gameRoot, width, height);
        input.handleInput(gameScene);

        startGameLoop();

        // Play battle background music when starting gameplay
        AudioManager.getInstance().playBackgroundMusic(true);

        primaryStage.setScene(gameScene);
    }
    
    private void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!logic.getPlayer().isAlive()) {
                    handleGameOver(false);
                    return;
                }

                if (logic.isLevelComplete()) {
                    handleGameOver(true);
                    return;
                }

                // Check if waiting for upgrade selection
                if (logic.isWaitingForUpgrade() && !upgradeWindowShown) {
                    showUpgradeSelection();
                    return;
                }

                logic.updateGame();

                updatePlayerInput();

                for (GameUpdateEvent event : logic.flushEvents()) {
                    panel.handleEvent(event);
                }
            }
        };
        
        gameLoop.start();
    }

    private void showUpgradeSelection() {
        // Prevent multiple windows from spawning
        if (upgradeWindowShown) return;

        upgradeWindowShown = true;
        logic.pauseGame();

        Platform.runLater(() -> {
            java.util.List<UpgradeManager.Upgrade> upgrades = logic.getUpgradeManager().getRandomUpgrades();

            UpgradeSelectionView upgradeView = new UpgradeSelectionView(upgrades, selectedUpgrade -> {
                // Apply the selected upgrade
                logic.getUpgradeManager().applyUpgrade(selectedUpgrade, logic.getPlayer(), logic);

                // Clear the book and notify spawn system
                logic.clearBook();
                logic.getSpawnSystem().onBookCollected();

                // Resume game and reset flag
                logic.resumeGame();
                upgradeWindowShown = false;
            });

            upgradeView.show();
        });
    }

    private void updatePlayerInput() {
        long currentTime = System.currentTimeMillis();
        
        // Handle push action (SPACEBAR)
        if (input.getSpaceKeyPressed() && !input.getSpacePushExecuted()) {
            boolean pushPerformed = logic.attemptPushAction();
            if (pushPerformed) {
                input.setSpacePushExecuted(true);
            }
        }
        
        // Handle movement
        if (currentTime - lastPlayerMoveTime < MIN_MOVE_INTERVAL_MS) {
            return;
        }
        
        int dirX = 0;
        int dirY = 0;
        boolean moveAttempted = false;
        
        if (input.getUpKeyPressed() && !input.getUpMoveExecuted()) {
            dirY = -1;
            
            moveAttempted = true;
        } else if (input.getDownKeyPressed() && !input.getDownMoveExecuted()) {
            dirY = 1;
            moveAttempted = true;
        } else if (input.getLeftKeyPressed() && !input.getLeftMoveExecuted()) {
            dirX = -1;
            moveAttempted = true;
        } else if (input.getRightKeyPressed() && !input.getRightMoveExecuted()) {
            dirX = 1;
            moveAttempted = true;
        }
        
        if (dirX != 0 && dirY != 0) {
            dirX = 0;
        }
        
        if (moveAttempted && (dirX != 0 || dirY != 0)) {
            boolean actionTaken = logic.attemptMove(dirX, dirY);
            
            if (actionTaken) {
                lastPlayerMoveTime = currentTime;
                
                if (dirY == -1) {
                	input.setUpMoveExecuted(true);
                }
                else if (dirY == 1) {
                	input.setDownMoveExecuted(true);
                }
                else if (dirX == -1) {
                	input.setLeftMoveExecuted(true);
                }
                else if (dirX == 1) {
                	input.setRightMoveExecuted(true);
                }
            }
        }
    }
    
    private void handleGameOver(boolean victory) {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        if (victory) {
            // For victory, show a simple alert for now
            SpawnSystem spawn = logic.getSpawnSystem();
            String message = "Congratulations! You completed Level " + spawn.getLevelNumber() + "!\n\n" +
                           "Final Wave: " + spawn.getCurrentWave() + "\n" +
                           "Enemies Defeated: " + spawn.getEnemiesDefeated();

            // Schedule dialog after current animation/layout cycle
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("LEVEL COMPLETE!");
                alert.setHeaderText(null);
                alert.setContentText(message);

                ButtonType playAgain = new ButtonType("Play Again");
                ButtonType levelSelect = new ButtonType("Level Select");
                ButtonType mainMenu = new ButtonType("Main Menu");
                ButtonType quit = new ButtonType("Quit");

                alert.getButtonTypes().setAll(playAgain, levelSelect, mainMenu, quit);

                alert.showAndWait().ifPresent(response -> {
                    // Play confirmation sound when selection is made
                    AudioManager.getInstance().playUiSound("CONFIRM");

                    if (response == playAgain) {
                        if (spawn.isEndlessMode()) {
                            startGame(0);
                        } else {
                            startGame(spawn.getLevelNumber());
                        }
                    } else if (response == levelSelect) {
                        showLevelSelect();
                    } else if (response == mainMenu) {
                        showMainMenu();
                    } else {
                        System.exit(0);
                    }
                });
            });
        } else {
            // For defeat, show the GameOverView
            Platform.runLater(() -> {
                // Stop the game loop and cleanup if in gameplay
                if (gameLoop != null) {
                    gameLoop.stop();
                    gameLoop = null;
                }

                // Clean up input handlers if they exist
                if (input != null) {
                    input = null;
                }

                // Get current window dimensions to handle resize
                double currentWidth = primaryStage.getWidth();
                double currentHeight = primaryStage.getHeight();

                // Use current size if window has been resized, otherwise use default
                double width = currentWidth > 0 ? currentWidth : WINDOW_WIDTH;
                double height = currentHeight > 0 ? currentHeight : WINDOW_HEIGHT;

                GameOverView gameOverView = new GameOverView();
                gameOverView.setOnRestartCallback(() -> startGame(0)); // Start endless mode
                gameOverView.setOnMenuCallback(this::showMainMenu);
                Scene gameOverScene = new Scene(gameOverView.getRoot(), width, height);
                primaryStage.setScene(gameOverScene);
            });
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
