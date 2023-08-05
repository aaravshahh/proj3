package byow.Core;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound {

    public static void RunMusic(String path) {


        try {
            AudioInputStream ais = null;
            File file = new File(path);
            ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }


    }
}
