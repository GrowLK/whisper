package common;

public class Cipher {
    public static byte[] encrypt(byte[] data, int pos, int len) {
    	byte[] buf = new byte[len];
        for(int i=0; i<len; ++i)
            buf[i] = (byte) ((data[i+pos]+0X7F) ^ 0XAA);
        return buf;
    }
    public static byte[] decrypt(byte[] buf, int pos, int len) {
    	byte[] data = new byte[len];
        for(int i=0; i<len; ++i)
            data[i] = (byte) ((buf[i+pos]^0XAA) - 0X7F);
        return data;
    }
}
