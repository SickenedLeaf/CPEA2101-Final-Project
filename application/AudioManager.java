package application;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;
    
    // Audio clips organized by category
    private Map<String, AudioClip> menuSounds;
    private Map<String, AudioClip> battleSounds;
    private Map<String, AudioClip> uiSounds;
    private Map<String, AudioClip> mobSounds;
    private Map<String, AudioClip> playerSounds;
    
    // Volume controls
    private double masterVolume = 1.0;
    private double musicVolume = 1.0;
    private double sfxVolume = 1.0;
    
    private boolean audioEnabled = true;

    // Background music player and playlists
    private MediaPlayer bgPlayer;
    private List<String> menuMusicPaths;
    private List<String> battleMusicPaths;
    private Random random = new Random();

    private AudioManager() {
        initializeAudioClips();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void initializeAudioClips() {
        menuSounds = new HashMap<>();
        battleSounds = new HashMap<>();
        uiSounds = new HashMap<>();
        mobSounds = new HashMap<>();
        playerSounds = new HashMap<>();
        
        // Load menu sounds (area/ folder - non-battle)
        loadMenuSounds();
        
        // Load battle sounds (battle/ folder - during gameplay)
        loadBattleSounds();
        
        // Load UI sounds
        loadUiSounds();
        
        // Load mob sound effects
        loadMobSounds();
        
        // Load player sound effects
        loadPlayerSounds();

        // Prepare background playlists (resource paths)
        initMusicPlaylists();
    }

    private void initMusicPlaylists() {
        menuMusicPaths = new ArrayList<>();
        battleMusicPaths = new ArrayList<>();

        // Area/menu music (play one at random, then play another when finished)
        menuMusicPaths.add("/assets/WAV/Area/Home_Town_Loop.wav");
        menuMusicPaths.add("/assets/WAV/Area/Ethereal_Rain_Loop.wav");
        menuMusicPaths.add("/assets/WAV/Area/Ethereal_Rain_Loop_Lite.wav");

        // Battle music (use one or more tracks; loop during battle)
        battleMusicPaths.add("/assets/WAV/Battle/Wrath_Loop.wav");
        battleMusicPaths.add("/assets/WAV/Battle/End_In_Sight_Loop.wav");
    }
    
    private void loadMenuSounds() {
        // Non-battle area sounds
        // Use actual files present in src/assets/WAV/Area
        menuSounds.put("BACKGROUND", loadAudioClip("/assets/WAV/Area/Home_Town_Loop.wav"));
    }
    
    private void loadBattleSounds() {
        // Battle sounds
        // Use available battle loop files
        battleSounds.put("BACKGROUND", loadAudioClip("/assets/WAV/Battle/Wrath_Loop.wav"));
    }
    
    private void loadUiSounds() {
        // Navigation sounds
        // Match file names in src/assets/WAV/UI Sounds
        uiSounds.put("NAV1", loadAudioClip("/assets/WAV/UI Sounds/Nav1.wav"));   // Main menu navigation
        uiSounds.put("NAV2", loadAudioClip("/assets/WAV/UI Sounds/Nav2.wav"));   // Level selection navigation
        uiSounds.put("ACCEPT", loadAudioClip("/assets/WAV/UI Sounds/Accept.wav")); // Main menu accept
        uiSounds.put("CONFIRM", loadAudioClip("/assets/WAV/UI Sounds/Confirm.wav")); // Other menu confirmations
    }
    
    private void loadMobSounds() {
        // Mob attack and effect sounds
        // Map mob sounds to actual files in src/assets/WAV/Mob_Effects
        mobSounds.put("GOBLIN_ATTACK", loadAudioClip("/assets/WAV/Mob_Effects/goblin_attack_bite.wav"));
        mobSounds.put("SKELETON_ATTACK", loadAudioClip("/assets/WAV/Mob_Effects/skeleton_attack.wav"));
        mobSounds.put("BRUTE_ATTACK", loadAudioClip("/assets/WAV/Mob_Effects/skeletonBrute_attack.wav"));
        mobSounds.put("BOOMER_ATTACK", loadAudioClip("/assets/WAV/Mob_Effects/boomerGoblin_attackOrDie_explosion.wav"));
        // Generic mob death fallback (use dedicated mob death sound)
        mobSounds.put("MOB_DEATH", loadAudioClip("/assets/WAV/Mob_Effects/mob_death.wav"));
        mobSounds.put("EXPLODE", loadAudioClip("/assets/WAV/Mob_Effects/boomerGoblin_attackOrDie_explosion.wav"));
    }
    
    private void loadPlayerSounds() {
        // Player sound effects
        // Map player sounds to files in Mob_Effects (player_push) and others if available
        playerSounds.put("PUSH", loadAudioClip("/assets/WAV/Mob_Effects/player_push.wav"));
        // Keep MOVE key but it's not used for walking sound (kept for compatibility)
        playerSounds.put("MOVE", loadAudioClip("/assets/WAV/Mob_Effects/player_push.wav"));
        playerSounds.put("DAMAGE", loadAudioClip("/assets/WAV/Mob_Effects/skeleton_attack.wav"));
        // Map player death to a specific key; use the dedicated player death sound
        playerSounds.put("PLAYER_DEATH", loadAudioClip("/assets/WAV/Mob_Effects/player_death.wav"));
        // Legacy alias
        playerSounds.put("DIE", playerSounds.get("PLAYER_DEATH"));
    }
    
    private AudioClip loadAudioClip(String path) {
        try {
            java.net.URL resourceUrl = getClass().getResource(path);
            if (resourceUrl == null) {
                System.out.println("[AUDIO] Resource not found: " + path);
                System.out.println("[AUDIO] Full path being checked: " + path);
                return null;
            }
            String urlPath = resourceUrl.toString();
            System.out.println("[AUDIO] Loading audio: " + path + " from URL: " + urlPath);
            AudioClip clip = new AudioClip(urlPath);
            System.out.println("[AUDIO] Successfully loaded: " + path);
            return clip;
        } catch (Exception e) {
            System.out.println("[AUDIO] Failed to load audio clip: " + path);
            System.out.println("[AUDIO] Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String getResourceUrlString(String path) {
        try {
            java.net.URL resourceUrl = getClass().getResource(path);
            if (resourceUrl == null) {
                System.out.println("[AUDIO] Resource not found: " + path);
                return null;
            }
            return resourceUrl.toString();
        } catch (Exception e) {
            System.out.println("[AUDIO] Error getting resource URL: " + path + " -> " + e.getMessage());
            return null;
        }
    }

    private void stopBackgroundPlayer() {
        if (bgPlayer != null) {
            try {
                bgPlayer.stop();
            } catch (Exception ignored) {}
            bgPlayer.dispose();
            bgPlayer = null;
        }
    }

    private void playMenuBackgroundRandom() {
        stopBackgroundPlayer();
        if (menuMusicPaths == null || menuMusicPaths.isEmpty()) return;

        // pick random track
        int idx = random.nextInt(menuMusicPaths.size());
        String path = menuMusicPaths.get(idx);
        String url = getResourceUrlString(path);
        if (url == null) return;

        Media media = new Media(url);
        bgPlayer = new MediaPlayer(media);
        bgPlayer.setVolume(masterVolume * musicVolume);
        bgPlayer.setCycleCount(1); // play once, then we'll schedule next
        bgPlayer.setOnEndOfMedia(this::playMenuBackgroundRandom);
        bgPlayer.play();
        System.out.println("[AUDIO] Playing menu background: " + path);
    }

    private void playBattleBackgroundLoop() {
        stopBackgroundPlayer();
        if (battleMusicPaths == null || battleMusicPaths.isEmpty()) return;

        // Choose first battle track (or random)
        int idx = random.nextInt(battleMusicPaths.size());
        String path = battleMusicPaths.get(idx);
        String url = getResourceUrlString(path);
        if (url == null) return;

        Media media = new Media(url);
        bgPlayer = new MediaPlayer(media);
        bgPlayer.setVolume(masterVolume * musicVolume);
        bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgPlayer.setOnEndOfMedia(() -> {});
        bgPlayer.play();
        System.out.println("[AUDIO] Playing battle background (loop): " + path);
    }

    // Diagnostics: log which clips were successfully loaded
    public void logLoadedClips() {
        System.out.println("[AUDIO] --- Loaded Audio Clips ---");
        System.out.println("[AUDIO] MenuMusicPaths:");
        if (menuMusicPaths != null) for (String p : menuMusicPaths) System.out.println("  " + p + " -> " + (getResourceUrlString(p) != null));
        System.out.println("[AUDIO] BattleMusicPaths:");
        if (battleMusicPaths != null) for (String p : battleMusicPaths) System.out.println("  " + p + " -> " + (getResourceUrlString(p) != null));

        System.out.println("[AUDIO] UI Sounds:");
        for (String k : uiSounds.keySet()) {
            AudioClip c = uiSounds.get(k);
            System.out.println("  " + k + " -> " + (c != null ? c.getSource() : "null"));
        }

        System.out.println("[AUDIO] Mob Sounds:");
        for (String k : mobSounds.keySet()) {
            AudioClip c = mobSounds.get(k);
            System.out.println("  " + k + " -> " + (c != null ? c.getSource() : "null"));
        }

        System.out.println("[AUDIO] Player Sounds:");
        for (String k : playerSounds.keySet()) {
            AudioClip c = playerSounds.get(k);
            System.out.println("  " + k + " -> " + (c != null ? c.getSource() : "null"));
        }
        System.out.println("[AUDIO] ---------------------------");
    }

    // Debug: try playing a specific key from a category, returns true if played (clip non-null)
    public boolean debugPlayUi(String key) {
        AudioClip c = uiSounds.get(key);
        if (c != null) { adjustVolumeAndPlay(c, false); return true; }
        return false;
    }

    public boolean debugPlayMob(String key) {
        AudioClip c = mobSounds.get(key);
        if (c != null) { adjustVolumeAndPlay(c, false); return true; }
        return false;
    }

    public boolean debugPlayPlayer(String key) {
        AudioClip c = playerSounds.get(key);
        if (c != null) { adjustVolumeAndPlay(c, false); return true; }
        return false;
    }

    // Public methods to play sounds
    public void playMenuSound(String soundKey) {
        if (!audioEnabled) return;
        AudioClip clip = menuSounds.get(soundKey);
        if (clip != null) {
            adjustVolumeAndPlay(clip, true);
        }
    }
    
    public void playBattleSound(String soundKey) {
        if (!audioEnabled) return;
        AudioClip clip = battleSounds.get(soundKey);
        if (clip != null) {
            adjustVolumeAndPlay(clip, true);
        }
    }
    
    public void playUiSound(String soundKey) {
        if (!audioEnabled) return;
        AudioClip clip = uiSounds.get(soundKey);
        if (clip != null) {
            adjustVolumeAndPlay(clip, false);
        }
    }
    
    public void playMobSound(String soundKey) {
        if (!audioEnabled) return;
        AudioClip clip = mobSounds.get(soundKey);
        if (clip != null) {
            adjustVolumeAndPlay(clip, false);
        }
    }
    
    public void playPlayerSound(String soundKey) {
        if (!audioEnabled) return;
        AudioClip clip = playerSounds.get(soundKey);
        if (clip != null) {
            adjustVolumeAndPlay(clip, false);
        }
    }
    
    private void adjustVolumeAndPlay(AudioClip clip, boolean isMusic) {
        if (clip == null) return;
        double vol = masterVolume * (isMusic ? musicVolume : sfxVolume);
        try {
            clip.play(vol);
        } catch (Exception e) {
            clip.play();
        }
    }

    // Volume control methods
    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
    }
    
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        // apply to background player immediately
        if (bgPlayer != null) {
            try { bgPlayer.setVolume(masterVolume * musicVolume); } catch (Exception ignored) {}
        }
    }
    
    public void setSfxVolume(double volume) {
        this.sfxVolume = Math.max(0, Math.min(1, volume));
    }
    
    public double getMasterVolume() { return masterVolume; }
    public double getMusicVolume() { return musicVolume; }
    public double getSfxVolume() { return sfxVolume; }
    
    public void toggleAudio() {
        this.audioEnabled = !this.audioEnabled;
    }
    
    public boolean isAudioEnabled() {
        return audioEnabled;
    }
    
    // Methods for specific game state audio
    public void playBackgroundMusic(boolean inBattle) {
        if (!audioEnabled) return;
        if (inBattle) {
            // stop menu music and start battle looping music
            playBattleBackgroundLoop();
        } else {
            // start random menu playlist
            playMenuBackgroundRandom();
        }
    }
    
    // Cleanup method if needed
    public void stopAllAudio() {
        // Stop background media player if active
        stopBackgroundPlayer();
        // No global stop for AudioClip; individual clips will stop naturally
    }
}