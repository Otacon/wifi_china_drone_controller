package org.cyanotic.cx10.net;

import static org.cyanotic.cx10.utils.ByteUtils.asUnsigned;

/**
 * Created by orfeo.ciano on 18/11/2016.
 */
public class HelloMessage2 extends HelloMessage1 {

    public HelloMessage2() {
        super();
        token = asUnsigned(0x98, 0x42, 0x97, 0xe1, 0xa1, 0x78, 0xec, 0x10, 0x3e, 0x8f, 0x4a, 0xa6, 0x25, 0xf9, 0x3b, 0xe8);
    }

    @Override
    byte getTypeFlag() {
        return 0x52;
    }

    @Override
    public String toString() {
        return "Hello2\n" + super.toString();
    }
}
