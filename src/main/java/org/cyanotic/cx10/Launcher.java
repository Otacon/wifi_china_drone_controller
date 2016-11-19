package org.cyanotic.cx10;

import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.net.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException {
        if(XInputDevice.isAvailable()){
            XInputDevice[] devices = XInputDevice.getAllDevices();
            XInputDevice device = devices[0];



        }

        Connection c = new Connection("172.16.10.1", 8888);
        c.connect();
        c.setName("org.cyanotic.cx10.net.Connection 1");
        c.sendMessage(new HelloMessage1());

        Connection c2 = new Connection("172.16.10.1", 8888);
        c2.connect();
        c2.setName("org.cyanotic.cx10.net.Connection 2");
        c2.sendMessage(new HelloMessage2());
        c2.disconnect();

        c.sendMessage(new HelloMessage3());
        c.sendMessage(new HelloMessage4());
        c.sendMessage(new HelloMessage5());
        c.sendMessage(new HelloMessage6());

        byte[] hold = hold();
        byte[] fly = fly();

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("172.16.10.1");
        DatagramPacket packet;
        System.out.println("Holding");
        for (int i = 0; i < 40; i++) {
            packet = new DatagramPacket(hold, hold.length, IPAddress, 8895);
            clientSocket.send(packet);
            Thread.sleep(50);
        }

        System.out.println("Takeoff");
        for (int i = 0; i < 20; i++) {
            packet = new DatagramPacket(fly, fly.length, IPAddress, 8895);
            clientSocket.send(packet);
            Thread.sleep(50);
            clientSocket.send(packet);
            Thread.sleep(50);
            clientSocket.send(packet);
            Thread.sleep(50);
        }

        System.out.println("Hold");
        for (int i = 0; i < 40; i++) {
            packet = new DatagramPacket(hold, hold.length, IPAddress, 8895);
            clientSocket.send(packet);
            Thread.sleep(50);
        }
    }

    public static final byte[] hold() {
        byte[] command = new byte[8];
        command[0] = (byte) 0xCC;
        command[1] = (byte) 0x80;
        command[2] = (byte) 0x80;
        command[3] = (byte) 0x80;
        command[4] = (byte) 0x7F;
        command[5] = (byte) 0x00;
        command[6] = (byte) 0xFF;
        command[7] = (byte) 0x33;
        return command;
    }

    public static final byte[] fly() {
        byte[] command = new byte[8];
        command[0] = (byte) 0xCC;
        command[1] = (byte) 0x80;
        command[2] = (byte) 0x80;
        command[3] = (byte) 0x80;
        command[4] = (byte) 0x7F;
        command[5] = (byte) 0x01;
        command[6] = (byte) 0xFE;
        command[7] = (byte) 0x33;
        return command;
    }
}
