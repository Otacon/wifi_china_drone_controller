package org.cyanotic.cx10.io.video;

import java.io.IOException;

/**
 * Created by cyanotic on 25/11/2016.
 */
public class FFPlayProcessVideoPlayer implements IVideoPlayer {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 8889;
    private Process ffplay;

    public void start() {
        if (ffplay != null) {
            stop();
        }
        try {
            String threads = "" + Runtime.getRuntime().availableProcessors() / 2;
            String output = "tcp://" + HOSTNAME + ":" + PORT + "?listen";
            ffplay = new ProcessBuilder("ffplay.exe", "-threads", "" + threads, "-i", output)
                    .inheritIO()
                    .start();
            System.out.println("ffplay.exe" + " -threads " + threads + " -i " + output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (ffplay != null) {
            ffplay.destroy();
            ffplay = null;
        }
    }
}
