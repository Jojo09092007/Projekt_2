import javax.sound.sampled.*;

/***Der abzuspielende Ton ist als .wav File im selben Verzeichnis abzulegen
AudioPlayer player = new AudioPlayer();
player.playAudio(audioFilePafd);

Es ist aufgrund einiger Bluej Beschränkungen nicht möglich stop Moglichkeiten einzubauen 
***/

import javax.sound.sampled.*;

public class AudioPlayer {
    private SourceDataLine audioLine;
    
    public void playAudio(String audioFilePath) {
        try {
            //Oeffnen des Audiostreams
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(AudioPlayer.class.getResourceAsStream(audioFilePath));

            // Kontrolle AudioFormat
            AudioFormat format = audioStream.getFormat();

            // DataLine wird erstellt
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            // Kontrolle des Systems
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("System nicht unterstuetzt.");
                System.exit(1);
            }

            // Oeffnen  der AudioLinie
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);

            // Abspielen 
            audioLine.start();

            // Buffer zur Absichereung des Nachladens bei langsamen Systemen
            byte[] buffer = new byte[4096];
            int bytesRead = 0;

            //Lesen und Schreiben des Buffers in den AudioStream
            while ((bytesRead = audioStream.read(buffer)) != -1) {
                
                audioLine.write(buffer, 0, bytesRead);
                
            }
            audioLine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

