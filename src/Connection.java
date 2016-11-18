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

    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public Message sendMessage(Message command) throws IOException {
        if (command == null) {
            return null;
        }

        byte[] bytes = command.getCommand();
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.write(bytes);

        byte[] buffer = new byte[160];
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        dataInputStream.read(buffer);

        ResponseMessage response = new ResponseMessage(buffer);
        System.out.println(response);
        return response;
    }
}
