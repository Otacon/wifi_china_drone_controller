package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.io.XInput;
import org.cyanotic.cx10.io.video.CX10NalDecoder;
import org.cyanotic.cx10.io.video.FFPlayProcessVideoPlayer;
import org.cyanotic.cx10.io.video.IVideoPlayer;
import org.cyanotic.cx10.net.CommandConnection;
import org.cyanotic.cx10.net.Connection;
import org.cyanotic.cx10.net.Heartbeat;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

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
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        InetAddress ffplay = InetAddress.getByName("localhost");
        Socket ffplaySocket = new Socket(ffplay, 8889);
        BufferedOutputStream ffplayOutput = new BufferedOutputStream(ffplaySocket.getOutputStream());

        Heartbeat heartbeat = new Heartbeat("172.16.10.1", 8888);
        heartbeat.start();

        CX10NalDecoder decoder = new CX10NalDecoder(inputStream, outputStream);
        byte[] data = null;
        do {
            data = decoder.readNal();
            ffplayOutput.write(data);
        } while (data != null);
    }
}
