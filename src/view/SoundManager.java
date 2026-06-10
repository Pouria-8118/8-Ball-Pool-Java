package view;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

public class SoundManager {
    
    private static SoundManager instance;
    private boolean enabled = true;
    
    private SoundManager() {}
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    private void play(String filename) {
        if (!enabled) return;
        try {
            File soundFile = new File("sounds/" + filename);
            if (!soundFile.exists()) return;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void playCueHit() {
        play("cue_hit.wav");
    }
    
    public void playBallCollision() {
        play("collision.wav");
    }
    
    public void playPocket() {
        play("pocket.wav");
    }

}