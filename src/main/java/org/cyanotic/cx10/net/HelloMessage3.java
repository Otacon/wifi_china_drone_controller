package org.cyanotic.cx10.net;

/**
 * Created by orfeo.ciano on 18/11/2016.
 */
public class HelloMessage3 extends HelloMessage1 {

    public HelloMessage3() {
        super();

        token = asUnsigned(0x21, 0xe0, 0xc4, 0x77, 0xc7, 0x73, 0x94, 0xe8, 0x5d, 0x66, 0xa9, 0x8c, 0x2c, 0x92, 0x2c, 0xc5);
    }

    @Override
    byte getTypeFlag() {
        return 0x52;
    }

    @Override
    public String toString() {
        return "Hello3\n" + super.toString();
    }
}
