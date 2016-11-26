package org.cyanotic.cx10.io.video;

import org.cyanotic.cx10.utils.ByteUtils;

import java.nio.ByteBuffer;

/**
 * Created by orfeo.ciano on 23/11/2016.
 */
public class VideoStreamControl {

    private int position = 0;
    private byte[] window = new byte[]{0x02, 0x02, 0x02, 0x02, 0x02};
    private int toSkip = 0;
    private boolean streamStarted = false;

    public VideoStreamControl() {
    }

    public byte[] feed(byte[] bytes) {
        System.out.println("Feeding the VideoStream");
        ByteBuffer out = ByteBuffer.allocate(bytes.length);
        position = 0;

        for (; position < bytes.length; position++) {
            if (toSkip > 0) {
                toSkip--;
                continue;
            }
            pushInWindow(bytes[position]);
            if (streamStarted) {
                out.put(ByteUtils.asUnsigned(bytes[position]));
            }
            if (isHeader() && !streamStarted) {
                streamStarted = true;
                out.put(ByteUtils.asUnsigned(0x00, 0x00, 0x00, 0x01, 0x67));
            }
        }

        byte[] ret = new byte[out.position()];
        out.rewind();
        out.get(ret);
        //System.out.println("Return is " + ByteUtils.bytesToHex(ret));
        return ret;
    }

    private void pushInWindow(byte b) {
        window[0] = window[1];
        window[1] = window[2];
        window[2] = window[3];
        window[3] = window[4];
        window[4] = b;
    }

    private boolean isHeader() {
        return window[0] == 0x00 && window[1] == 0x00 && window[2] == 0x00 && window[3] == (0x01 & 0xFF) && window[4] == 0x67;
    }

}
