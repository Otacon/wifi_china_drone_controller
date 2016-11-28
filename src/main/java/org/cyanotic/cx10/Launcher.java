package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.io.XInput;
import org.cyanotic.cx10.io.video.*;
import org.cyanotic.cx10.net.CommandConnection;
import org.cyanotic.cx10.net.Connection;
import org.cyanotic.cx10.net.Heartbeat;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException {
        final IVideoPlayer player = new FFPlayProcessVideoPlayer();
        player.start();
        IVideoEncoder encoder = new FFMpegProcessVideoEncoder();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        encoder.setFileName("output-" + timestamp + ".mp4");
        encoder.start();
        Thread.sleep(1000);

        Connection c1 = new Connection("172.16.10.1", 8888);
        Controller controller = new Controller(new XInput(), new CommandConnection("172.16.10.1", 8895), c1);
        controller.start();

        InetAddress address = InetAddress.getByName("172.16.10.1");
        Socket socket = new Socket(address, 8888);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        final InetAddress ffplay = InetAddress.getByName("localhost");
        final Socket ffplaySocket = new Socket(ffplay, 8889);
        final BufferedOutputStream ffplayOutput = new BufferedOutputStream(ffplaySocket.getOutputStream());

        final InetAddress ffmpeg = InetAddress.getByName("localhost");
        final Socket ffmpegSocket = new Socket(ffmpeg, 8890);
        final BufferedOutputStream ffmpegOutput = new BufferedOutputStream(ffmpegSocket.getOutputStream());

        final Heartbeat heartbeat = new Heartbeat("172.16.10.1", 8888);
        heartbeat.start();

        final CX10NalDecoder decoder = new CX10NalDecoder(inputStream, outputStream);

        Thread t = new Thread(new Runnable() {
            public void run() {
                byte[] data;
                do {
                    try {
                        data = decoder.readNal();
                        ffplayOutput.write(data);
                        ffmpegOutput.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } while (data != null);
            }
        });
        t.start();
        System.in.read();

        System.out.println("Shutdown");

        try {
            ffplaySocket.close();
        } catch (IOException e) {

        }

        try {
            ffmpegSocket.close();
        } catch (IOException e) {

        }

        System.exit(0);
    }
}
