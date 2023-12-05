import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class AudioPlayer {
    public boolean PlayTheme = true;
    public boolean PlayAnother = false;
    public AudioPlayer(){
    }
    public void setPlayAnother(){
        PlayAnother = true;
    }
    protected void startAudio(){
        Runnable themeSound = () -> {
            try {

                Clip clip = AudioSystem.getClip();

                clip.open(AudioSystem.getAudioInputStream(Objects.requireNonNull(this.getClass().getResource("audio/theme.wav"))));

                clip.setFramePosition(0);
                clip.loop(999);

                while (PlayTheme){
                    Thread.sleep(10000);
                }

                clip.stop();
                clip.close();

            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
                exc.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        Runnable anotherSound = () -> {
            try {
                Clip clip = AudioSystem.getClip();

                clip.open(AudioSystem.getAudioInputStream(Objects.requireNonNull(this.getClass().getResource("audio/line.wav"))));

                while (PlayTheme){
                    if(PlayAnother){
                        clip.setFramePosition(0);
                        clip.start();
                        PlayAnother = false;
                    }
                    Thread.sleep(10);
                }

                clip.stop();
                clip.close();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
                exc.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread themeSoundClass = new Thread(themeSound);
        Thread anotherSoundClass = new Thread(anotherSound);
        themeSoundClass.start();
        anotherSoundClass.start();
    }
}
