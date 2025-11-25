package com.pushknight.systems;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages audio playback for the game.
 * Handles sound effects and background music with separate volume controls.
 * Implements Singleton pattern to ensure only one audio manager exists.
 */
public class AudioManager {
    private static AudioManager instance;
    
    // Volume controls (0.0 to 1.0)
    private double soundEffectVolume;
    private double musicVolume;
    
    // Sound effects storage
    private Map<String, AudioClip> soundEffects;
    
    // Music player
    private MediaPlayer musicPlayer;
    private String currentMusicTrack;
    
    // Settings
    private boolean soundEffectsEnabled;
    private boolean musicEnabled;
    
    /**
     * Private constructor to enforce Singleton pattern.
     */
    private AudioManager() {
        this.soundEffectVolume = 0.7;
        this.musicVolume = 0.5;
        this.soundEffects = new HashMap<>();
        this.soundEffectsEnabled = true;
        this.musicEnabled = true;
        this.currentMusicTrack = null;
    }
    
    /**
     * Gets the singleton instance of AudioManager.
     * 
     * @return The AudioManager instance
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Loads a sound effect from a resource path.
     * 
     * @param name The name identifier for the sound
     * @param resourcePath The resource path to the sound file
     * @return true if the sound was loaded successfully, false otherwise
     */
    public boolean loadSoundEffect(String name, String resourcePath) {
        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.err.println("Sound effect not found: " + resourcePath);
                return false;
            }
            
            AudioClip sound = new AudioClip(resource.toString());
            sound.setVolume(soundEffectVolume);
            soundEffects.put(name, sound);
            return true;
        } catch (Exception e) {
            System.err.println("Error loading sound effect " + name + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Plays a sound effect by name.
     * 
     * @param name The name of the sound effect to play
     */
    public void playSound(String name) {
        if (!soundEffectsEnabled) {
            return;
        }
        
        AudioClip sound = soundEffects.get(name);
        if (sound != null) {
            sound.play();
        } else {
            System.err.println("Sound effect not found: " + name);
        }
    }
    
    /**
     * Plays a sound effect with a specific volume override.
     * 
     * @param name The name of the sound effect to play
     * @param volume The volume (0.0 to 1.0)
     */
    public void playSound(String name, double volume) {
        if (!soundEffectsEnabled) {
            return;
        }
        
        AudioClip sound = soundEffects.get(name);
        if (sound != null) {
            double originalVolume = sound.getVolume();
            sound.setVolume(volume);
            sound.play();
            sound.setVolume(originalVolume);
        } else {
            System.err.println("Sound effect not found: " + name);
        }
    }
    
    /**
     * Stops a specific sound effect.
     * 
     * @param name The name of the sound effect to stop
     */
    public void stopSound(String name) {
        AudioClip sound = soundEffects.get(name);
        if (sound != null) {
            sound.stop();
        }
    }
    
    /**
     * Stops all sound effects.
     */
    public void stopAllSounds() {
        for (AudioClip sound : soundEffects.values()) {
            sound.stop();
        }
    }
    
    /**
     * Loads and plays background music from a resource path.
     * 
     * @param resourcePath The resource path to the music file
     * @param loop Whether to loop the music
     * @return true if the music was loaded and started successfully, false otherwise
     */
    public boolean playMusic(String resourcePath, boolean loop) {
        if (!musicEnabled) {
            return false;
        }
        
        try {
            // Stop current music if playing
            stopMusic();
            
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.err.println("Music file not found: " + resourcePath);
                return false;
            }
            
            Media media = new Media(resource.toString());
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setVolume(musicVolume);
            musicPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
            musicPlayer.play();
            
            currentMusicTrack = resourcePath;
            return true;
        } catch (Exception e) {
            System.err.println("Error loading music " + resourcePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Stops the currently playing music.
     */
    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
            currentMusicTrack = null;
        }
    }
    
    /**
     * Pauses the currently playing music.
     */
    public void pauseMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }
    
    /**
     * Resumes the paused music.
     */
    public void resumeMusic() {
        if (musicPlayer != null && musicEnabled) {
            musicPlayer.play();
        }
    }
    
    /**
     * Sets the volume for sound effects.
     * 
     * @param volume The volume (0.0 to 1.0)
     */
    public void setSoundEffectVolume(double volume) {
        this.soundEffectVolume = Math.max(0.0, Math.min(1.0, volume));
        
        // Update all loaded sound effects
        for (AudioClip sound : soundEffects.values()) {
            sound.setVolume(soundEffectVolume);
        }
    }
    
    /**
     * Gets the current sound effect volume.
     * 
     * @return The volume (0.0 to 1.0)
     */
    public double getSoundEffectVolume() {
        return soundEffectVolume;
    }
    
    /**
     * Sets the volume for background music.
     * 
     * @param volume The volume (0.0 to 1.0)
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        
        if (musicPlayer != null) {
            musicPlayer.setVolume(musicVolume);
        }
    }
    
    /**
     * Gets the current music volume.
     * 
     * @return The volume (0.0 to 1.0)
     */
    public double getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Enables or disables sound effects.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setSoundEffectsEnabled(boolean enabled) {
        this.soundEffectsEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    /**
     * Checks if sound effects are enabled.
     * 
     * @return true if enabled, false otherwise
     */
    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }
    
    /**
     * Enables or disables background music.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopMusic();
        } else if (currentMusicTrack != null) {
            // Resume music if it was playing
            playMusic(currentMusicTrack, true);
        }
    }
    
    /**
     * Checks if music is enabled.
     * 
     * @return true if enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    /**
     * Cleans up resources and stops all audio.
     */
    public void cleanup() {
        stopAllSounds();
        stopMusic();
        soundEffects.clear();
    }
}

