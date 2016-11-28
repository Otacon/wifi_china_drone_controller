package org.cyanotic.cx10.io.video;

import java.io.IOException;

/**
 * Created by cyanotic on 25/11/2016.
 */
public class FFMpegProcessVideoEncoder implements IVideoEncoder {

    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 8890;
    private Process ffmpeg;
    private String fileName;

    public void setFileName(String name) {
        this.fileName = name;
    }

    public void start() {
        if (ffmpeg != null) {
            stop();
        }
        try {
            String output = "tcp://" + HOSTNAME + ":" + PORT + "?listen";
            ffmpeg = new ProcessBuilder("cmd", "/c", "start", "ffmpeg", "-f", "h264", "-i", output, "-vcodec", "copy", "-r", "25", fileName)
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (ffmpeg != null) {
            ffmpeg.destroy();
            ffmpeg = null;
        }
    }
}
