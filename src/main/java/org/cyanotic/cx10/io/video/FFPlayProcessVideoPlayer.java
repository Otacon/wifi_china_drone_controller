package org.cyanotic.cx10.io.video;

import java.io.IOException;

/**
 * Created by cyanotic on 25/11/2016.
 */
public class FFPlayProcessVideoPlayer implements IVideoPlayer {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 8889;
    private Process ffplay;
    private String SO = "";

    public FFPlayProcessVideoPlayer(String SO) {
        this.SO = SO;
    }

    public void start() {
        if (ffplay != null) {
            stop();
        }
        try {
            String output = "tcp://" + HOSTNAME + ":" + PORT + "?listen";
            if (SO == "win")
                ffplay = new ProcessBuilder("cmd", "/c", "start", "ffplay", "-probesize", "64", "-sync", "ext", output)
                    .start();
            else
                ffplay = new ProcessBuilder("ffplay", "-fflags", "nobuffer", output)
                        .start();
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
