package org.cyanotic.cx10.io;

import org.cyanotic.cx10.utils.ByteUtils;
import org.junit.Before;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by orfeo.ciano on 23/11/2016.
 */
public class VideoStreamControlTest {

    public byte[] test1 = ByteUtils.asUnsigned(
            0x00, 0x00, 0x01, 0x42, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x01, 0x11,
            0x11, 0x11, 0x11, 0x01, 0x11,
            0x11, 0x11, 0x11, 0x01, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11, 0x12);
    private VideoStreamControl control;

    @Before
    public void setUp() throws Exception {
        control = new VideoStreamControl();
    }

    @org.junit.Test
    public void feed_headerBegin() throws Exception {
        byte[] feed = control.feed(test1);
        assertArrayEquals(ByteUtils.asUnsigned(0x00, 0x00, 0x00, 0x01, 0x42, 0x12), feed);
    }

}