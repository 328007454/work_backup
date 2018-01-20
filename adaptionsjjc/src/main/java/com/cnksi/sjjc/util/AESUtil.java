package com.cnksi.sjjc.util;


import android.util.Base64;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * 编码工具类 实现aes加密、解密
 */
public class AESUtil {

    /**
     * 密钥
     */
    public static final String KEY = "AES1234567890abc";

    /**
     * 算法
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 解密及校验数据是否完整
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean decryptAndValidate(String data, String split) {
        try {
            String decrypt = aesDecrypt(data); // AES 解密
            String clientMD5Value = decrypt.split(split)[0];
            String originValue = decrypt.split(split)[1]; // 原始密码

            String serverMD5Value = MD5Util.encodeMD5Hex(originValue); // 进行MD5加密

            return clientMD5Value.equals(serverMD5Value); // 加密后的数据对比
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解密及校验数据是否完整
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String decode(String data) throws Exception {
        String regex = "-";
        try {
            String decrypt = aesDecrypt(data); // AES 解密
            String[] split = decrypt.split(regex);
            if (split.length < 2) {
                return null;
            }
            String clientMD5Value = split[0];
            String originValue = split[1]; // 原始密码

            String serverMD5Value = MD5Util.encodeMD5Hex(originValue); // 进行MD5加密
            System.out.println(originValue);
            if (clientMD5Value.equals(serverMD5Value)) { // 加密后的数据对比
                return originValue;
            } else {
                throw new Exception("数据不完整，无法验证通过");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("数据不完整，无法验证通过");
        }
    }

    /**
     * 密码加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encode(String data) {
        try {
            String encodeMD5Hex = MD5Util.encodeMD5Hex(data);
            encodeMD5Hex = encodeMD5Hex + "-" + data;
            String encrypt;
            encrypt = AESUtil.aesEncrypt(encodeMD5Hex, AESUtil.KEY);
            return encrypt;
        } catch (Exception e) {
            e.printStackTrace();
            // throw new Exception("加密失败");
        }
        return null;
    }

    /**
     * aes解密
     *
     * @param encrypt 内容
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String encrypt) throws Exception {
        return aesDecrypt(encrypt, KEY);
    }

    /**
     * aes加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String content) throws Exception {
        return aesEncrypt(content, KEY);
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception {
        return com.cnksi.core.utils.StringUtils.isEmpty(base64Code) ? null : android.util.Base64.encode(base64Code.getBytes(), android.util.Base64.URL_SAFE);
    }

    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return com.cnksi.core.utils.StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

}