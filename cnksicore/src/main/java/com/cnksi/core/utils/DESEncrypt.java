package com.cnksi.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DESEncrypt {
    private static DESEncrypt instence = null;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.CANADA);

    private String encrypt = "";

    private DESEncrypt(String key) {
        byte[] inputData = key.getBytes();
        try {
            BigInteger sha = new BigInteger(Coder.encryptMD5(inputData));
            encrypt = sha.toString(32);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DESEncrypt getIntence(String strKey) {
        strKey = strKey.toUpperCase(Locale.CHINA);
        instence = new DESEncrypt(strKey);
        return instence;
    }

    /**
     * 生成16位序列号
     * <p/>
     * <pre>
     * i[0]
     * c[0]
     * i[1]
     * Y1
     *
     * c[1]
     * Y2
     * i[2]
     * c[2]
     *
     * M1
     * i[3]
     * M2
     * c[3]
     *
     * i[4]
     * D1
     * c[4]
     * D2
     *
     * </pre>
     *
     * @param validDate
     * @return
     * @throws Exception
     */
    public String Serial(Date validDate) throws Exception {
        String validDateStr = sdf.format(validDate);
        Random random = new Random();
        int n = encrypt.length();
        if (n > 15) {
            n = 15;
        }
        int r1 = random.nextInt(n);
        int r2 = random.nextInt(n);
        int r3 = random.nextInt(n);
        int r4 = random.nextInt(n);
        int r5 = random.nextInt(n);

        // System.out.println(r1 + "-" + r2 + "-" + r3 + "-" + r4);
        return String.format("%s%c%s%s-%c%s%s%c-%s%s%s%c-%s%s%c%s", Integer.toHexString(r1), // i[0]
                encrypt.charAt(r1), // c[0]
                Integer.toHexString(r2), // i[1]
                validDateStr.substring(0, 1), // Y1

                encrypt.charAt(r2), // c[1]
                validDateStr.substring(1, 2), // Y2
                Integer.toHexString(r3), // i[2]
                encrypt.charAt(r3), // c[2]

                validDateStr.substring(2, 3), // M1
                Integer.toHexString(r4), // i[3]
                validDateStr.substring(3, 4), // M2
                encrypt.charAt(r4), // c[3]

                Integer.toHexString(r5), // i[4]
                validDateStr.substring(4, 5), // D1
                encrypt.charAt(r5), // c[4]
                validDateStr.substring(5, 6) // D2
        );
    }

    /**
     * 验证序列号是否合法，如果合法，则返回到期时间
     *
     * @param serial
     * @param key    (AndroidId_ProjectId)注意是AndroidId+下划线+ProjectId
     * @return 过期日期（2016-01-01）
     */
    public Date verifySerialNO(String serial, String key) {
        try {
            String _serial = serial.replace("-", "");
            int r1 = Integer.parseInt(_serial.charAt(0) + "", 16);
            int r2 = Integer.parseInt(_serial.charAt(2) + "", 16);
            int r3 = Integer.parseInt(_serial.charAt(6) + "", 16);
            int r4 = Integer.parseInt(_serial.charAt(9) + "", 16);
            int r5 = Integer.parseInt(_serial.charAt(12) + "", 16);
            char c1 = _serial.charAt(1);
            char c2 = _serial.charAt(4);
            char c3 = _serial.charAt(7);
            char c4 = _serial.charAt(11);
            char c5 = _serial.charAt(14);

            String serial2 = String.format("%c%c%c%c%c", encrypt.charAt(r1), encrypt.charAt(r2), encrypt.charAt(r3), encrypt.charAt(r4), encrypt.charAt(r5));
            String serial3 = String.format("%c%c%c%c%c", c1, c2, c3, c4, c5);
            boolean isValid = serial2.equalsIgnoreCase(serial3) ? true : false;

            if (isValid) {
                char y1 = _serial.charAt(3);
                char y2 = _serial.charAt(5);
                char m1 = _serial.charAt(8);
                char m2 = _serial.charAt(10);
                char d1 = _serial.charAt(13);
                char d2 = _serial.charAt(15);
                String dateStr = String.format("%c%c%c%c%c%c", y1, y2, m1, m2, d1, d2);
                return sdf.parse(dateStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * MD5 加密
     *
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public final static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
