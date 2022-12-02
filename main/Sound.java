package main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    public static enum Sounditem {
        ST_tango("futuro-por-pasado"),
        ST_marimba("ave-marimba"),
        ST_new_wave("newer-wave"),
        collect("collect"),
        explosion("explosion");

        public final String label;

        private Sounditem(String label) {
            this.label = label;
        }
    }

    private Clip clip;
    private static Map<Sounditem, File> soundFiles;

    public Sound() {
        if (soundFiles == null)
            soundFiles = new HashMap<Sounditem, File>();
        for (var val : Sounditem.values()) {
            soundFiles.put(val, new File("resources/sound/" + val.label + ".wav"));
        }
    }

    public void setFile(Sounditem item) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFiles.get(item));
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }
}
