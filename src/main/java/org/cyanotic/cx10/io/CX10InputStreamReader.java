package org.cyanotic.cx10.io;

import org.cyanotic.cx10.utils.ByteUtils;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * Created by orfeo.ciano on 23/11/2016.
 */
public class CX10InputStreamReader {

    private static final int STATE_INIT = 0;
    private static final int STATE_00 = 1;
    private static final int STATE_00_00 = 2;
    private static final int STATE_00_00_01 = 3;
    private static final int STATE_SKIP = 4;

    private boolean firstNal = true;
    private int state = STATE_INIT;
    private int toSkip = 0;
    private int byteCount = 0;

    private ByteBuffer buffer;

    public byte[] feed(byte[] bytes) {
        buffer = ByteBuffer.allocate(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            byteCount++;
            nextByte(bytes[i]);
        }
        byte[] ret = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(ret);
        return ret;
    }

    private void nextByte(byte b) {
        switch (state) {
            case STATE_INIT:
                stateInit(b);
                break;
            case STATE_00:
                state00(b);
                break;
            case STATE_00_00:
                state00_00(b);
                break;
            case STATE_00_00_01:
                state00_00_01(b);
                break;
            case STATE_SKIP:
                stateSkip(b);
                break;
            default:
                stateInit(b);
        }
    }

    private void stateInit(byte b) {
        if (b == 0x00) {
            state = STATE_00;
        } else {
            if (!firstNal) {
                try {
                    buffer.put(b);
                } catch (BufferOverflowException e) {
                    e.printStackTrace();
                }
            }
            state = STATE_INIT;
        }
    }

    private void state00(byte b) {
        if (b == 0x00) {
            state = STATE_00_00;
        } else {
            if (!firstNal) {
                buffer.put(ByteUtils.asUnsigned(0x00, b));
            }
            state = STATE_INIT;
        }
    }

    private void state00_00(byte b) {
        if (b == 0x00) {
            state = STATE_00_00;
        } else if (b == 0x01) {
            state = STATE_00_00_01;
        } else {
            if (!firstNal) {
                buffer.put(ByteUtils.asUnsigned(0x00, 0x00, b));
            }
            state = STATE_INIT;
        }
    }

    private void state00_00_01(byte b) {
        int nal_unit_type = (b & 0x1F);
        System.out.print("NAL " + nal_unit_type);
        if (isValid(nal_unit_type)) {

            if (firstNal && nal_unit_type == 7) {
                System.out.println(" first nal!");
                buffer.put(ByteUtils.asUnsigned(0x00, 0x00, 0x00, 0x01, b));
                firstNal = false;
            } else if (!firstNal) {
                buffer.put(ByteUtils.asUnsigned(0x00, 0x00, 0x00, 0x01, b));
            }
            state = STATE_INIT;
        } else {
            System.out.println(" skipped " + byteCount + " " + ByteUtils.bytesToHex(ByteUtils.asUnsigned(b)));
            toSkip = 36;
            state = STATE_SKIP;
        }
    }

    private boolean isValid(int nal_unit_type) {
        return nal_unit_type > 0 && nal_unit_type < 16 || nal_unit_type > 18 && nal_unit_type < 22;
    }

    private void stateSkip(byte b) {
        toSkip--;
        if (toSkip == 0) {
            state = STATE_INIT;
        }
    }
}
