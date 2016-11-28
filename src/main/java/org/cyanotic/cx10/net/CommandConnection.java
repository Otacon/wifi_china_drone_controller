package org.cyanotic.cx10.net;

import org.cyanotic.cx10.model.Command;
import org.cyanotic.cx10.utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

    public static final byte checksum(byte[] bytes) {
        byte sum = 0;
        for (byte b : bytes) {
            sum ^= b;
        }
        return sum;
    }

    public void sendCommand(Command command) {
        byte[] data = asByteArray(command);
        //System.out.println(bytesToHex(data));
        DatagramPacket packet = new DatagramPacket(data, 0, data.length, host, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Unable to send packet: ");
            e.printStackTrace();
        }
    }

    private byte[] asByteArray(Command command) {
        int pitch = command.getPitch() + 127;
        int yaw = command.getYaw() + 127;
        int roll = command.getRoll() + 127;
        int throttle = command.getThrottle() + 127;
        boolean takeOff = command.isTakeOff();
        boolean land = command.isLand();

        byte[] data = new byte[8];
        data[0] = (byte) 0xCC;
        data[1] = (byte) roll;
        data[2] = (byte) pitch;
        data[3] = (byte) throttle;
        data[4] = (byte) yaw;
        if (takeOff) {
            data[5] = (byte) 0x01;
        } else if (land) {
            data[5] = (byte) 0x02;
        } else {
            data[5] = (byte) 0x00;
        }

        data[6] = checksum(ByteUtils.asUnsigned(data[1], data[2], data[3], data[4], data[5]));

        data[7] = (byte) 0x33;
        return data;
    }

}
