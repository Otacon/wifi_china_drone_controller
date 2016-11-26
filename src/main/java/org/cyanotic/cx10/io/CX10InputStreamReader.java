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
    private static final int STATE_CAPTURE_NAL_A0 = 5;
    private static final int STATE_CAPTURE_NAL_A1 = 6;

    private boolean firstNal = false;
    private int state = STATE_INIT;
    private int toSkip = 0;
    private int toCapture = 0;
    private ByteBuffer nalBuffer;

    private ByteBuffer buffer;

    public byte[] feed(byte[] bytes) {
        buffer = ByteBuffer.allocate(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
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
            case STATE_CAPTURE_NAL_A0:
                stateCaptureNalA0(b);
                break;
            case STATE_CAPTURE_NAL_A1:
                stateCaptureNalA1(b);
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
        if (firstNal) {
            if (nal_unit_type == 7) {
                buffer.put(ByteUtils.asUnsigned(0x00, 0x00, 0x00, 0x01, b));
                firstNal = false;
                state = STATE_INIT;
            }
        } else {
            if ((int) b == -96) {
                nalBuffer = ByteBuffer.allocate(40);
                nalBuffer.put(ByteUtils.asUnsigned(0x00, 0x00, 0x01, 0xA0));
                toCapture = 36;
                state = STATE_CAPTURE_NAL_A0;
            } else if ((int) b == -95) {
                nalBuffer = ByteBuffer.allocate(13);
                nalBuffer.put(ByteUtils.asUnsigned(0x00, 0x00, 0x01, 0xA1));
                toCapture = 8;
                state = STATE_CAPTURE_NAL_A1;
            } else {
                buffer.put(ByteUtils.asUnsigned(0x00, 0x00, 0x00, 0x01, b));
                state = STATE_INIT;
            }
        }

    }

    private void stateCaptureNalA0(byte b) {
        nalBuffer.put(b);
        toCapture--;
        if (toCapture == 0) {
            byte[] translatedNal = replaceNal(nalBuffer.array());
            buffer.put(translatedNal);
            state = STATE_INIT;
        }
    }

    private void stateCaptureNalA1(byte b) {
        nalBuffer.put(b);
        toCapture--;
        if (toCapture == 0) {
            state = STATE_INIT;
        }
    }

    private void stateSkip(byte b) {
        toSkip--;
        if (toSkip == 0) {
            state = STATE_INIT;
        }
    }

    byte[] replaceNal(byte[] nalA0) {
        byte[] out = new byte[32];
        byte[] params = ByteUtils.asUnsigned(0x01, 0x00, 0x00, 0x19, 0xD0, 0x02, 0x40, 0x02);
        System.arraycopy(params, 0, out, 0, params.length);
        System.arraycopy(nalA0, 12, out, 8, 8);
        out[16] = nalA0[5];
        out[18] = nalA0[9];
        out[19] = nalA0[8];
        return out;
    }


}
