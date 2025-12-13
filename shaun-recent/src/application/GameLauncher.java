package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class GameLauncher extends Application {
    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 720;

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

        showMainMenu();

        primaryStage.setTitle("Push Knight Peril - Wave Survival");
        primaryStage.show();
    }
    
    private void showMainMenu() {
        mainMenuView = new MainMenuView(WINDOW_WIDTH, WINDOW_HEIGHT);

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
        settingsView = new SettingsView(WINDOW_WIDTH, WINDOW_HEIGHT);

        settingsView.setOnBackSelected(this::showMainMenu);

        // Play menu background music in settings (part of menu area)
        AudioManager.getInstance().playBackgroundMusic(false);

        primaryStage.setScene(settingsView.getScene());
    }

    private void showLevelSelect() {
        levelSelectView = new LevelSelectView(WINDOW_WIDTH, WINDOW_HEIGHT);

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
        System.out.println("[GAME] Starting " +
            (levelNumber == 0 ? "Endless Mode" : "Level " + levelNumber));

        logic = new GameLogic(levelNumber);
        panel = new GamePanel(logic);
        input = new InputHandler();

        Scene gameScene = new Scene(panel.getGridView());
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

        SpawnSystem spawn = logic.getSpawnSystem();

        String title = victory ? "LEVEL COMPLETE!" : "GAME OVER";
        String message;

        if (victory) {
            message = "Congratulations! You completed Level " + spawn.getLevelNumber() + "!\n\n" +
                     "Final Wave: " + spawn.getCurrentWave() + "\n" +
                     "Enemies Defeated: " + spawn.getEnemiesDefeated() + "\n\n" +
                     "Would you like to play again?";
        } else {
            if (spawn.isEndlessMode()) {
                message = "You survived until Wave " + spawn.getCurrentWave() + "!\n" +
                         "Enemies Defeated: " + spawn.getEnemiesDefeated() + "\n\n" +
                         "Would you like to try again?";
            } else {
                message = "You were defeated on Wave " + spawn.getCurrentWave() + "\n" +
                         "Enemies Defeated: " + spawn.getEnemiesDefeated() + "\n\n" +
                         "Would you like to try again?";
            }
        }

        // Schedule dialog after current animation/layout cycle
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
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
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
