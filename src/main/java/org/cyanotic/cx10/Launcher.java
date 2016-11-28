package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.io.XInput;
import org.cyanotic.cx10.io.video.CX10NalDecoder;
import org.cyanotic.cx10.io.video.FFPlayProcessVideoPlayer;
import org.cyanotic.cx10.io.video.IVideoPlayer;
import org.cyanotic.cx10.net.CommandConnection;
import org.cyanotic.cx10.net.Connection;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException {
        IVideoPlayer player = new FFPlayProcessVideoPlayer();
        player.start();
        Thread.sleep(1000);

        Connection c1 = new Connection("172.16.10.1", 8888);
        Controller controller = new Controller(new XInput(), new CommandConnection("172.16.10.1", 8895), c1);
        controller.start();

        InetAddress address = InetAddress.getByName("172.16.10.1");
        Socket socket = new Socket(address, 8888);
        byte[] bytes = Files.readAllBytes(Paths.get("video.bin"));
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(bytes);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
//        FileInputStream inputStream = new FileInputStream("video_protocol");
        byte[] response = new byte[106];
        inputStream.read(response);

        InetAddress ffplay = InetAddress.getByName("localhost");
        Socket ffplaySocket = new Socket(ffplay, 8889);
        BufferedOutputStream ffplayOutput = new BufferedOutputStream(ffplaySocket.getOutputStream());
//        BufferedOutputStream ffplayOutput = new BufferedOutputStream(new FileOutputStream("media_1t_t.h264"));
//        CX10InputStreamReader control = new CX10InputStreamReader();
//        byte[] buf = new byte[64];
//        while (inputStream.read(buf) > 0) {
//            byte[] cleanOutput = control.feed(buf);
//            //ffplayOutput.write(cleanOutput);
//        }

        CX10NalDecoder decoder = new CX10NalDecoder(inputStream);
        byte[] data = null;
        do {
            data = decoder.readNal();
            ffplayOutput.write(data);
        } while (data != null);
    }
}
