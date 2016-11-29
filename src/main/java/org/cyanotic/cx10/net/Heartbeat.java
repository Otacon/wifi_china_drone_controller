package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;

import java.io.*;
import java.net.Socket;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Heartbeat extends Thread {

    private final String host;
    private final int port;

    private Socket socket;

    public Heartbeat(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public synchronized void start() {
        try {
            socket = new Socket(host, port);
            super.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection failed");
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                sendHeartBeat();
                Thread.sleep(5000);
            } catch (IOException e) {
                System.err.println("Unable to send heartbeat");
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Heartbeat interrupted");
                e.printStackTrace();
            }
        }
    }

    private void sendHeartBeat() throws IOException {
        System.out.println("Sending heartbeat...");
        byte[] heartbeatData = ByteUtils.loadMessageFromFile("heartbeat.bin");
        int start = 0;
        int len = heartbeatData.length;
        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        if (len > 0) {
            dos.write(heartbeatData, start, len);
        }
        dos.flush();
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buf = new byte[106];
        int bytesRead;
        bytesRead = dis.read(buf);
        baos.write(buf, 0, bytesRead);
        System.out.println("The drone is alive.");
    }
}
