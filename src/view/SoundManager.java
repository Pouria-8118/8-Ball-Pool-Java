package view;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class SoundManager {
    
    private static SoundManager instance;
    private boolean enabled = true;
    private float masterVolume = 0.7f;
    
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
    
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
    }
    
    private void play(String filename) {
        if (!enabled) return;
        try {
            File soundFile = new File("sounds/" + filename);
            if (!soundFile.exists()) return;
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            if (masterVolume > 0) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float dB = min + (max - min) * masterVolume;
                gainControl.setValue(dB);
            }
            
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