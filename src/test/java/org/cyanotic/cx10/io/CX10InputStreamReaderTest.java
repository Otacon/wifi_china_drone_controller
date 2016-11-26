package org.cyanotic.cx10.io;

import org.cyanotic.cx10.utils.ByteUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by cyanotic on 24/11/2016.
 */
public class CX10InputStreamReaderTest {
    public byte[] test1 = ByteUtils.asUnsigned(
            0x00, 0x00, 0x01, 0xA0, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,
            0x11, 0x11, 0x11, 0x01, 0x11,
            0x11, 0x11, 0x11, 0x01, 0x11,
            0x11, 0x11, 0x11, 0x01, 0x11,
            0x11, 0x11, 0x11, 0x11, 0x11,
            0x12, 0x13, 0x14, 0x15);
    private CX10InputStreamReader streamReader;

    @Before
    public void setUp() throws Exception {
        streamReader = new CX10InputStreamReader();
    }

    @Test
    public void feed_headerBegin() throws Exception {
        byte[] feed = streamReader.feed(test1);
        byte[] expecteds = ByteUtils.asUnsigned(0x12, 0x13, 0x14, 0x15);
        assertArrayEquals(expecteds, feed);
    }

}