package com.cnksi.nari.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/8/14 15:33
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DecodeUtils {
    private static byte[] KEY = {84, 94, -60, 118, 67, -20, -55, -70};
    private static byte[] IV = {79, 76, -120, -36, -61, -72, 91, 50};
    private static String SUFFIX = "PIEncrypt";
    private static String SQLKEY = "mip12345mip12345mip12345mip12345mip12345mip12345";

    public static String piDecrypt(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            throw new NullPointerException("要解密的字符串不能为 null。");
        }
        try {
            SecretKey localSecretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(KEY));
            Cipher localCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            localCipher.init(2, localSecretKey, new IvParameterSpec(IV));
            byte[] bytes = Base64.decode(paramString.substring(0, paramString.length() - SUFFIX.length()), Base64.DEFAULT);

            return new String(localCipher.doFinal(bytes));
        } catch (Exception e) {
            throw new RuntimeException("使用“PIEncrypt”算法解密字符串时发生错误，请参考: ", e);
        }
    }


    public static byte[] md5EncryptToBytes(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new NullPointerException("要加密的字符串不能为 null。");
        }
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.reset();
            localMessageDigest.update(str.getBytes(Charset.forName("UTF-8")));
            return localMessageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前设备不支持 MD5 哈希算法。");
        } catch (Exception e) {
            throw new RuntimeException("使用“MD5”算法哈希字符串时发生错误，请参考: ", e);
        }
    }

    public static String aesEncrypt(String str1, String str2) {
        if (TextUtils.isEmpty(str1)) {
            throw new NullPointerException("要加密的字符串不能为 null。");
        }
        try {
            SecretKeySpec key = new SecretKeySpec(md5EncryptToBytes(str2), "AES");
            Cipher localCipher = Cipher.getInstance("AES");
            localCipher.init(1, key);
            return ByteUtils.byte2Hex(localCipher.doFinal(str1.getBytes("UTF-8")), false);
        } catch (Exception e) {
            throw new RuntimeException("使用“AES”算法加密字符串时发生错误，请参考: ", e);
        }
    }

    public static String aesDecrypt(String str1, String str2) {
        if (TextUtils.isEmpty(str1)) {
            throw new NullPointerException("要加密的字符串不能为 null。");
        }
        try {
            SecretKeySpec key = new SecretKeySpec(md5EncryptToBytes(str2), "AES");
            Cipher localCipher = Cipher.getInstance("AES");
            localCipher.init(2, key);
            return new String(localCipher.doFinal(hexToByte(str1)));
        } catch (Exception e) {
            throw new RuntimeException("使用“AES”算法加密字符串时发生错误，请参考: ", e);
        }
    }

    /**
     * 把HexString 转换成二进制解密
     *
     * @param hexString
     * @return
     */
    public static byte[] hexToByte(String hexString) {
        String str = hexString.toUpperCase();
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() - 1; i += 2) {
            bytes[i / 2] = (byte) ((CharToByte(str.charAt(i)) << 4) | CharToByte(str.charAt(i + 1)));
        }
        return bytes;
    }

    public static String CHARS = "0123456789ABCDEF";

    public static int CharToByte(char c) {
        return CHARS.indexOf(c);
    }

    /**
     * 完整性校验 用于SQL接口的
     *
     * @param str1
     * @param str2
     * @return
     */
    public static String getIntegrity(String str1, String str2) {
        return md5EncryptToString(piDecrypt("kJOqsAYpRgO6qsjsRRNBAA==PIEncrypt") + str1 + str2);
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String md5EncryptToString(String str) {
        return ByteUtils.byte2Hex(md5EncryptToBytes(str), false);
    }

    /**
     * 登录密码加密
     *
     * @param str
     * @return
     */
    public static String EncodePassword(String str) {
        try {
            str = str + "mip12345mip12345mip12345mip12345mip12345mip12345";
            SecretKeySpec key = new SecretKeySpec("FHuma025FHuma025".getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] b = cipher.doFinal(str.getBytes());
            return ByteUtils.byte2Hex(b, true);
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * SQl 加密 用于查询接口
     *
     * @param sql
     * @return
     */
    public static String EncodeSql(String sql) {
        return aesEncrypt(sql, SQLKEY);
    }

}
