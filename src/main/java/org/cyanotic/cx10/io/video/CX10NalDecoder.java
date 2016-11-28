package org.cyanotic.cx10.io.video;

import org.cyanotic.cx10.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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
    boolean savedData = false;

    public CX10NalDecoder(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public byte[] readNal() throws IOException {
        System.out.println();
        byte[] nalHeader = new byte[10];

        int read = inputStream.read(nalHeader);
        System.out.println("Data read:" + read);
        if (read < nalHeader.length) {
            return null;
        }
        System.out.println("Header: " + ByteUtils.bytesToHex(nalHeader));
        int nalType = nalHeader[3] & 0xFF;
        switch (nalType) {
            case 0xA0:
                System.out.println("The nal is A0");
                break;
            case 0xA1:
                System.out.println("The nal is A1");
                break;
            default:
                System.err.println("Unknown NAL Type" + nalType);
                return null;
        }

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

//        System.out.println("Header length = " + headerSize);

        int dataLength = ((nalHeader[9] & 0xff) << 8) | (nalHeader[8] & 0xff);
        System.out.println("Data length: " + dataLength);
        byte[] fullNalHeader = new byte[headerSize + nalHeader.length];
        System.arraycopy(nalHeader, 0, fullNalHeader, 0, nalHeader.length);

        inputStream.read(fullNalHeader, 10, headerSize);
        System.out.println(ByteUtils.bytesToHex(fullNalHeader));
        if (nalType == 0xA0 && headerType == 0x03) {
//            System.out.println("Old Header " + ByteUtils.bytesToHex(fullNalHeader));
            byte[] newHeader = replaceA003(fullNalHeader);
//            System.out.println("New Header " + ByteUtils.bytesToHex(newHeader));
            byte[] data = readData(dataLength);
            byte[] ret = ByteBuffer.allocate(newHeader.length + data.length).put(newHeader).put(data).array();
//            System.out.println(ByteUtils.bytesToHex(ret));
            return ret;
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
            System.out.println("Created next thing " + ByteUtils.bytesToHex(ph));
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
            System.out.println("Replaced old thing with new one " + ByteUtils.bytesToHex(ph));
            return tmp;
        } else {
            return readData(dataLength);
        }
    }

    byte[] replaceA003(byte[] nalA0) {
        byte[] out = new byte[32];
        byte[] params = ByteUtils.asUnsigned(0x01, 0x00, 0x00, 0x19, 0xD0, 0x02, 0x40, 0x02);
        System.arraycopy(params, 0, out, 0, params.length);
        System.arraycopy(nalA0, 12, out, 8, 8);
        out[16] = nalA0[5];
        out[18] = nalA0[9];
        out[19] = nalA0[8];
        return out;
    }

    byte[] readData(int length) throws IOException {
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
}
