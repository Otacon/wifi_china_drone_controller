package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by cyanotic on 27/11/2016.
 */
public class CX10NalDecoder {

    private static byte[] ph = ByteUtils.asUnsigned(
            0x00, 0x00, 0x00, 0x19, 0xD0,
            0x02, 0x40, 0x02, 0x00, 0xBF,
            0x8A, 0x00, 0x01, 0x5D, 0x03,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00);
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Socket socket;
    private boolean savedData = false;
    private boolean initialized = false;
    private int sequence = 0;

    public CX10NalDecoder(String host, int port) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
    }

    public byte[] readNal() throws IOException {
        if (!initialized) {
            byte[] bytes = Files.readAllBytes(Paths.get("video.bin"));
            outputStream.write(bytes);
            byte[] response = new byte[106];
            inputStream.read(response);
            initialized = true;
        }
        byte[] nalHeader = readData(10);
        sequence = nalHeader[5] & 0xFF;
        int nalType = nalHeader[3] & 0xFF;

        int headerSize;
        int headerType = nalHeader[7] & 0XFF;
        switch (headerType) {
            case 0x02:
            case 0x03:
                headerSize = 30;
                break;
            case 0x01:
                headerSize = 2;
                break;
            default:
                System.err.println("Unknown header type " + headerType);
                return null;

        }

        int dataLength = ((nalHeader[9] & 0xff) << 8) | (nalHeader[8] & 0xff);
        byte[] fullNalHeader = new byte[headerSize + nalHeader.length];
        System.arraycopy(nalHeader, 0, fullNalHeader, 0, nalHeader.length);

        inputStream.read(fullNalHeader, 10, headerSize);
        if (nalType == 0xA0 && headerType == 0x03) {
            byte[] newHeader = replaceA003(fullNalHeader);
            byte[] data = readData(dataLength);
            return ByteBuffer.allocate(newHeader.length + data.length).put(newHeader).put(data).array();
        } else if (nalType == 0xA1 && headerType == 0x02) {
            ph[8] = fullNalHeader[8];
            ph[9] = fullNalHeader[9];
            ph[10] = fullNalHeader[10];
            ph[11] = fullNalHeader[11];

            ph[16] = fullNalHeader[5];
            ph[17] = fullNalHeader[4];
            ph[18] = fullNalHeader[33];
            ph[19] = fullNalHeader[32];
            savedData = true;
            return readData(dataLength);
        } else if (nalType == 0xA1 && headerType == 0x01) {
            byte[] ret = readData(dataLength);
            byte[] tmp;
            if (savedData) {
                tmp = ByteBuffer.allocate(ph.length + ret.length).put(ph).put(ret).array();
                savedData = false;
            } else {
                tmp = ByteBuffer.allocate(ret.length).put(ret).array();
            }
            return tmp;
        } else {
            return readData(dataLength);
        }
    }

    private byte[] replaceA003(byte[] nalA0) {
        byte[] out = new byte[32];
        byte[] params = ByteUtils.asUnsigned(0x01, 0x00, 0x00, 0x19, 0xD0, 0x02, 0x40, 0x02);
        System.arraycopy(params, 0, out, 0, params.length);
        System.arraycopy(nalA0, 12, out, 8, 8);
        out[16] = nalA0[5];
        out[18] = nalA0[9];
        out[19] = nalA0[8];
        return out;
    }

    private byte[] readData(int length) throws IOException {
        int read = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        while (read < length) {
            byte[] buffer = new byte[length - read];
            int lastRead = inputStream.read(buffer);
            byteBuffer.put(buffer, 0, lastRead);
            read += lastRead;
        }
        return byteBuffer.array();
    }

    public void disconnect() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
