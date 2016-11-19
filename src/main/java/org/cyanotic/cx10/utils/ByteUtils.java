package org.cyanotic.cx10.utils;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class ByteUtils {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        String output = "";
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            output += hexArray[v >>> 4];
            output += hexArray[v & 0x0F];
            output += " ";
        }
        return output;
    }

    public static byte[] asUnsigned(int... values){
        byte[] bytes = new byte[values.length];
        for(int i = 0; i < values.length; i++){
            int value = values[i];
            if(value > Byte.MAX_VALUE){
                bytes[i] = (byte) value;
            } else {
                bytes[i] = (byte) (value & 0xff);
            }
        }
        return bytes;
    }
}
