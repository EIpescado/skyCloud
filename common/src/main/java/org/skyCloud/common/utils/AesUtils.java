package org.skyCloud.common.utils;

import org.apache.commons.codec.binary.Hex;
import org.skyCloud.common.exception.SkyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yq on 2017/04/01 14:20.
 * 加密请求参数 对称加密
 */
public class AesUtils{

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**加密方式*/
    private static final String KEY_ALGORITHM = "AES";

    /**字符编码*/
    private static final String ENCODING = "utf-8";

    /**密钥*/
    private static final String KEY = "mySpringBootDemo";

    private static Logger logger = LoggerFactory.getLogger(AesUtils.class);

    private AesUtils() {
    }

    /**
     * 加密
     * @param value
     */
    public static String encryptParams(String value){
        try {
            return URLEncoder.encode(AesUtils.encrypt(value), ENCODING);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }


    /**
     * 解密
     * @param content
     */
    public static String decryptParams(String content) {
        if(!StringUtils.isEmpty(content)){
            return  decrypt(content);
        }
        return content;
    }


    /**
     * 加密方式 密钥必须是16位
     */
    public static String encrypt(String content) {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);
        Cipher cipher;
        try {
            //Cipher对象实际完成解密操作
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            //用密钥加密明文(plainText),生成密文(cipherText)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);// 初始化
            //得到加密后的字节数组
            byte[] result = cipher.doFinal(content.getBytes(ENCODING));
            return Hex.encodeHexString(result);//转16进制
        } catch (Exception e) {
            logger.error("加密 {} 失败",content);
            throw new SkyException("10086","加密失败");
        }
    }

    /**
     * 解密参数
     * @param content
     */
    public static String decrypt(String content) {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);// 初始化
            byte[] result = cipher.doFinal(Hex.decodeHex(content.toCharArray()));
            return new String(result);
        } catch (Exception e) {
            logger.error("解密 {} 失败",content);
            throw new SkyException("10068","解密失败");
        }
    }
}
