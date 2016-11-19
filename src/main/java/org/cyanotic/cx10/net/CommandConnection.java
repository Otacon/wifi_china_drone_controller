package org.cyanotic.cx10.net;

import org.cyanotic.cx10.model.Command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.cyanotic.cx10.utils.ByteUtils.bytesToHex;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class CommandConnection {

    private final DatagramSocket socket;
    private final int port;
    private final InetAddress host;

    public CommandConnection(String host, int port) throws IOException {
        this.port = port;
        this.host = InetAddress.getByName(host);
        socket = new DatagramSocket();
    }

    public void sendCommand(Command command) {
        byte[] data = asByteArray(command);
        System.out.println(bytesToHex(data));
        DatagramPacket packet = new DatagramPacket(data, 0, data.length, host, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Unable to send packet: ");
            e.printStackTrace();
        }
    }

    private byte[] asByteArray(Command command) {
        int pitch = command.getPitch() + 128;
        int yaw = command.getYaw() + 128;
        int roll = command.getRoll() + 128;
        int throttle = command.getThrottle() + 128;
        boolean takeOff = command.isTakeOff();
        boolean land = command.isLand();

        byte[] data = new byte[8];
        data[0] = (byte) 0xCC;
        data[1] = (byte) 0x80;
        data[2] = (byte) 0x80;
        data[3] = (byte) 0x80;
        data[4] = (byte) throttle;
        if (takeOff) {
            data[5] = (byte) 0x01;
            data[6] = (byte) 0xFE;
        } else if (land) {
            data[5] = (byte) 0x02;
            data[6] = (byte) 0xFD;
        } else {
            data[5] = (byte) 0x00;
            data[6] = (byte) 0xFF;
        }

        data[7] = (byte) 0x33;
        return data;
    }


}
