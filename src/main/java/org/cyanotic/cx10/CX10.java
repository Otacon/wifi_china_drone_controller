package org.cyanotic.cx10;

import org.cyanotic.cx10.io.controls.Controller;
import org.cyanotic.cx10.io.controls.XInput;
import org.cyanotic.cx10.io.video.FFMpegProcessVideoEncoder;
import org.cyanotic.cx10.io.video.FFPlayProcessVideoPlayer;
import org.cyanotic.cx10.io.video.IVideoEncoder;
import org.cyanotic.cx10.io.video.IVideoPlayer;
import org.cyanotic.cx10.net.CX10NalDecoder;
import org.cyanotic.cx10.net.CommandConnection;
import org.cyanotic.cx10.net.Heartbeat;
import org.cyanotic.cx10.net.TransportConnection;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cyanotic on 28/11/2016.
 */
public class CX10 {
    public static final String HOST = "172.16.10.1";

    private TransportConnection transportConnection;
    private Controller controller;
    private IVideoPlayer previewPlayer;
    private IVideoEncoder recorder;
    private Heartbeat heartbeat;
    private OutputStream ffplayOutput;
    private OutputStream ffmpegOutput;
    private Socket ffplaySocket;
    private Socket ffmpegSocket;
    private boolean videoStarted = false;

    public void connect() throws IOException {
        if (transportConnection != null) {
            transportConnection.disconnect();
        }
        transportConnection = new TransportConnection(HOST, 8888);
        transportConnection.connect();
        transportConnection.setName("Transport Connection");
        transportConnection.sendMessage("message1.bin", 106);
        transportConnection.sendMessage("message2.bin", 106);
        transportConnection.sendMessage("message3.bin", 170);
        transportConnection.sendMessage("message4.bin", 106);
        transportConnection.sendMessage("message5.bin", 106);
        heartbeat = new Heartbeat(HOST, 8888);
        heartbeat.start();
    }

    public void disconnect() {
        if (heartbeat != null) {
            heartbeat.interrupt();
        }

        stopControls();
        stopVideoStream();
        stopVideoRecorder();

        if (transportConnection != null) {
            try {
                transportConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startControls() throws IOException {
        if (controller != null) {
            stopControls();
        }
        controller = new Controller(new XInput(), new CommandConnection(HOST, 8895));
        controller.start();
    }

    public void stopControls() {
        if (controller != null) {
            controller.interrupt();
            controller = null;
        }
    }

    public void startVideoStream() throws IOException {
        if (previewPlayer != null) {
            stopVideoStream();
        }
        previewPlayer = new FFPlayProcessVideoPlayer();
        previewPlayer.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final InetAddress ffplay = InetAddress.getByName("localhost");
        ffplaySocket = new Socket(ffplay, 8889);
        ffplayOutput = new BufferedOutputStream(ffplaySocket.getOutputStream());
        startVideo();
    }

    public void stopVideoStream() {
        if (previewPlayer != null) {
            previewPlayer.stop();
            previewPlayer = null;
        }

        try {
            ffplayOutput.close();
            ffplaySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ffplayOutput = null;
        ffplaySocket = null;
    }

    public void startVideoRecorder() throws IOException {
        if (recorder != null) {
            stopVideoRecorder();
        }
        recorder = new FFMpegProcessVideoEncoder();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        recorder.setFileName("output-" + timestamp + ".mp4");
        recorder.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final InetAddress ffmpeg = InetAddress.getByName("localhost");
        ffmpegSocket = new Socket(ffmpeg, 8890);
        ffmpegOutput = new BufferedOutputStream(ffmpegSocket.getOutputStream());
        startVideo();

    }

    public void stopVideoRecorder() {
        if (recorder != null) {
            recorder.stop();
            recorder = null;
        }
        try {
            ffmpegOutput.close();
            ffmpegSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ffmpegOutput = null;
        ffmpegSocket = null;
    }

    private void startVideo() throws IOException {
        if (videoStarted) {
            return;
        }
        final CX10NalDecoder decoder = new CX10NalDecoder(HOST, 8888);
        final Thread t = new Thread(new Runnable() {
            public void run() {
                byte[] data;
                videoStarted = true;
                do {
                    try {
                        data = decoder.readNal();
                        if (ffplayOutput != null) {
                            ffplayOutput.write(data);
                        }
                        if (ffmpegOutput != null) {
                            ffmpegOutput.write(data);
                        }

                        if (ffplayOutput == null && ffmpegOutput == null) {
                            decoder.disconnect();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } while (data != null);
                videoStarted = false;
            }
        });
        t.start();
    }
}
