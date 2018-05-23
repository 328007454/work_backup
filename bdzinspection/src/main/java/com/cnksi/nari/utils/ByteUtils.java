package com.cnksi.nari.utils;

/**
 * Created by wastrel on 2017/6/23.
 */
public class ByteUtils {
    public static String byte2Hex(byte[] bytes, boolean isUpper) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hv = "0" + hv;
            }
            builder.append(hv);
        }
        if (isUpper) {
            return builder.toString().toUpperCase();
        } else {
            return builder.toString().toLowerCase();
        }
    }
}
