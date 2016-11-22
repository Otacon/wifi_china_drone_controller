package org.cyanotic.cx10;

import com.ivan.xinput.exceptions.XInputNotLoadedException;
import org.cyanotic.cx10.utils.ByteUtils;
import org.jcodec.api.JCodecException;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, XInputNotLoadedException, JCodecException {
//        DirectFileReadDataSource dataSource = new DirectFileReadDataSource(new File("output.mp4"));
//        H264TrackImpl h264Track = new H264TrackImpl(dataSource);
//        Movie movie = new Movie();
//        movie.addTrack(h264Track);
//        Container mp4file = new DefaultMp4Builder().build(movie);
//        FileChannel fc = new FileOutputStream(new File("encoded.mp4")).getChannel();
//        mp4file.writeContainer(fc);
//        fc.close();


//        Connection c1 = new Connection("172.16.10.1", 8888);
//        Controller controller = new Controller(new XInput(), new CommandConnection("172.16.10.1", 8895), c1);
//        controller.start();
//
//        InetAddress address = InetAddress.getByName("172.16.10.1");
//        Socket socket = new Socket(address, 8888);
//        byte[] bytes = Files.readAllBytes(Paths.get("video.bin"));
//        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//        outputStream.write(bytes);
//        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        DataInputStream inputStream = new DataInputStream(new FileInputStream("conversation.bin"));
        byte[] response = new byte[106];
        inputStream.read(response);

        Thread.sleep(1000);
        FileOutputStream file = new FileOutputStream("output.mp4");
        try {
            int bytesCount = 0;
            while (true) {
                int[] data = new int[3];
                int bytesRead = 0;
                data[0] = inputStream.readUnsignedByte();
                if (data[0] == 0x00) {
                    bytesRead++;
                    data[1] = inputStream.readUnsignedByte();
                    if (data[1] == 0x00) {
                        bytesRead++;
                        data[2] = inputStream.readUnsignedByte();
                        if (data[2] == (0x01 & 0xff)) {
                            bytesRead++;
                        }
                    }
                }
                boolean isHeader = bytesRead == 3;
                if (isHeader) {
                    byte[] header = new byte[41];
                    header[0] = 0x00;
                    header[1] = 0x00;
                    header[2] = 0x00;
                    header[3] = 0x01 & 0xFF;
                    inputStream.read(header, 4, 37);
                    inputStream.skipBytes(4);
                    System.out.println(ByteUtils.bytesToHex(header));
                    file.write(header, 0, 4);
                } else {
                    for (int i = 0; i <= bytesRead; i++) {
                        file.write(data[i]);
                    }
                }

                bytesCount++;
            }
        } catch (IOException e) {
            file.flush();
            file.close();
        }
    }
}
