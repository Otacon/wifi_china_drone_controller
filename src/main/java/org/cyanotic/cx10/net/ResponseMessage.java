package org.cyanotic.cx10.net;

/**
 * Created by orfeo.ciano on 18/11/2016.
 */
public class ResponseMessage extends Message {

    public ResponseMessage(byte[] bytes) {
        System.arraycopy(bytes, 0, header, 0, 10);
        System.arraycopy(bytes, 10, token, 0, 16);
        System.arraycopy(bytes, 26, line1, 0, 16);
        System.arraycopy(bytes, 42, line2, 0, 16);
        System.arraycopy(bytes, 58, line3, 0, 16);
        System.arraycopy(bytes, 74, line4, 0, 16);
        System.arraycopy(bytes, 90, line5, 0, 16);
    }

    @Override
    byte getTypeFlag() {
        return header[6];
    }

    @Override
    public String toString() {
        return "Response\n" + super.toString();
    }
}
