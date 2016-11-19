package org.cyanotic.cx10.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by orfeo.ciano on 18/11/2016.
 */
public class Connection {

    private final String host;
    private final int port;
    private String name;
    Socket socket;

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        if (socket != null && socket.isConnected()) {
            System.err.println("The socket is already open and connected");
            return;
        }

        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
    }

    public void setName(String name){
        this.name = name;
    }

    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public Message sendMessage(Message message) throws IOException {
        if (message == null) {
            return null;
        }
        System.out.print(name + " >>> ");
        System.out.print(message);
        byte[] bytes = message.getCommand();
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.write(bytes);
        System.out.print(name + " <<< ");
        byte[] buffer;
        if(message instanceof HelloMessage4){
            buffer = new byte[170];
        } else {
            buffer = new byte[106];
        }
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        dataInputStream.read(buffer);

        ResponseMessage response = new ResponseMessage(buffer);
        System.out.println(response);
        return response;
    }
}
