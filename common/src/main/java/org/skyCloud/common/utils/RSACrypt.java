package org.skyCloud.common.utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yq on 2017/02/06 10:55.
 * 私钥 公钥 加密解密 签名类
 * 非对称
 */
public class RSACrypt {

    private static final String KEY_ALGORITHM = "RSA";

    public static final String SPECIFIC_KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";

    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    private static final String PRIVATEKEY = "privatekey";

    private static final String PUBLICKEY = "publickey";

    private static final int DEFAULTKEYSIZE = 1024;

    private static final int MAX_ENCRYPT_BLOCK = 117;

    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 生成公私钥对
     * @param randomStr 用于产生随机数的字符
     * @return
     * @throws Exception
     */
    public static Map<String,Object> getKeyPair(boolean useRamdomString, String... randomStr) throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        if(useRamdomString){
            SecureRandom ranDom = new SecureRandom(randomStr[0].getBytes());
            keyPairGenerator.initialize(DEFAULTKEYSIZE, ranDom);
        }else{
            keyPairGenerator.initialize(DEFAULTKEYSIZE);
        }
        KeyPair keyPair = keyPairGenerator.generateKeyPair(); // 生成密钥组
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); //私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); //公钥
        Map<String, Object> keyPairMap = new HashMap<String,Object>(2);
        keyPairMap.put(PRIVATEKEY, privateKey);
        keyPairMap.put(PUBLICKEY, publicKey);

        return keyPairMap;
    }

    /**
     * 获取私钥 base64加密
     * @param keyPairMap
     * @return base64加密后私钥
     */
    public static String getPrivateKey(Map<String,Object> keyPairMap){
        Key key =  (Key) keyPairMap.get(PRIVATEKEY);
        return  CodeUtils.encryptBASE64(key.getEncoded());
    }

    /**
     * 获取公钥
     * @param keyPairMap
     * @return base64加密后公钥
     */
    public static String getPublicKey(Map<String,Object> keyPairMap){
        Key key =  (Key) keyPairMap.get(PUBLICKEY);
        return  CodeUtils.encryptBASE64(key.getEncoded());
    }

    /**
     * 使用私钥对数据进行签名
     * @param data 需要签名的数据
     * @param privateKeyStr 私钥（使用BASE64进行编码）
     * @return 返回加签名后的BASE64编码的字符串
     * @throws Exception
     */
    public static String signByPrivateKey(String data, String privateKeyStr,String charsetName) throws Exception{

        byte[] privateKeyByte = CodeUtils.decryptBASE64(privateKeyStr); //base64 解密
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte); //私钥 用 PKCS#8 编码
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);//获取使用 KEY_ALGORITHM 算法的密钥工厂
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);//根据给定的算法生成一个私钥对象
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);//根据算法获取一个签名对象
        signature.initSign(privateKey);
        signature.update(data.getBytes(charsetName));//签名

        return CodeUtils.encryptBASE64(signature.sign()); //返回签名后的字节数组 并用BASE64加密
    }

    /**
     * 使用公钥进行数字签名的校验
     * @param data 需要进行验签的原始数据
     * @param publicKeyStr 公钥（BASE64编码字符串）
     * @param sign 签名 （BASE64编码字符串）
     * @return
     * @throws Exception
     */
    public static boolean verifyByPublicKey(String data, String publicKeyStr, String sign,String charsetName) throws Exception{
        try{
            byte[] publicKeyByte = CodeUtils.decryptBASE64(publicKeyStr);
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKeyByte); //公钥 用 X509#8 编码
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM); //获取使用 KEY_ALGORITHM 算法的密钥工厂
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(encodedKeySpec); //根据给定的算法生成一个公钥对象
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM); //根据算法获取一个签名对象
            signature.initVerify(publicKey); //初始化签名对象
            signature.update(data.getBytes(charsetName));
            return signature.verify(CodeUtils.decryptBASE64(sign)); //验证是否签名
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String publicKeyStr) throws Exception {
        if(null == data) return null;
        byte[] dataB = data.getBytes("UTF-8");
        byte[] publicKeyByte = CodeUtils.decryptBASE64(publicKeyStr);
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKeyByte); //公钥 用 X509#8 编码
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(encodedKeySpec);

        Cipher cipher = Cipher.getInstance(SPECIFIC_KEY_ALGORITHM); //根据算法获取加解密器
        cipher.init(Cipher.ENCRYPT_MODE, publicKey); //用指定的密钥和模式初始化 Cipher 对象
        int inputLen = dataB.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        try {
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(dataB, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(dataB, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            return CodeUtils.encryptBASE64(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            out.close();
        }
        return null;
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, String privateKeyStr) throws Exception {
        if(null == data) return null;
//    	byte[] dataB = data.getBytes("UTF-8");
        byte[] dataB = CodeUtils.decryptBASE64(data);
        byte[] privateKeyByte = CodeUtils.decryptBASE64(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec); //生成私钥对象

        Cipher cipher = Cipher.getInstance(SPECIFIC_KEY_ALGORITHM);//根据算法获取加解密器
        cipher.init(Cipher.DECRYPT_MODE, privateKey);//用指定的密钥和模式初始化 Cipher 对象
        //模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] decryptedData = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            int dataLength = dataB.length;
            for (int i = 0; i < dataLength; i += key_len) {
                int decryptLength = dataLength - i < key_len ? dataLength - i
                        : key_len;
                byte[] doFinal = cipher.doFinal(dataB, i, decryptLength);
                bout.write(doFinal);
            }
            decryptedData = bout.toByteArray();
        } finally {
            if (bout != null) {
                bout.close();
            }
        }
        return CodeUtils.encryptBASE64(decryptedData);
    }

    /**
     *
     * @param apiKey  加密地址
     * @param secret secretKey
     * @return
     * @throws Exception
     */
    public static String getSign(String apiKey, String timeStrap , String data , String secret) throws Exception {

        Map<String,String> signMap = new HashMap<String,String>();

        signMap.put("appkey" , apiKey);
        signMap.put("data" , data);
        signMap.put("timestrap" , timeStrap);

        return Signing(signMap, secret);
    }



    private static String Signing(Map<String,String> paramsMap, String secret) throws Exception {

        // 第一步：检查参数是否已经排序
        String[] keys = paramsMap.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder strBuilder = new StringBuilder();
        for (String key : keys) {
            String value = paramsMap.get(key);
            strBuilder.append(key).append(value);
        }
        // 第三步：使用MD5/HMAC加密
        byte[] bytes = encryptHMAC(strBuilder.toString(), secret);
        // 第四步：把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

    private static byte[] encryptHMAC(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"),
                    "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            //Log.e("TB_ERR", gse.getMessage());
        }
        return bytes;
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }



    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i=0; i<x+z; i++) {
            if (i==x+z-1 && y!=0) {
                str = string.substring(i*len, i*len+y);
            }else{
                str = string.substring(i*len, i*len+len);
            }
            strings[i] = str;
        }
        return strings;
    }
    /**
     *拆分数组
     */
    public static byte[][] splitArray(byte[] data,int len){
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if(y!=0){
            z = 1;
        }
        byte[][] arrays = new byte[x+z][];
        byte[] arr;
        for(int i=0; i<x+z; i++){
            arr = new byte[len];
            if(i==x+z-1 && y!=0){
                System.arraycopy(data, i*len, arr, 0, y);
            }else{
                System.arraycopy(data, i*len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }


    /**
     * ASCII码转BCD码
     *
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }
    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char [] temp = new char[bytes.length * 2];
        char val;
        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    public static void main(String[] args) throws Exception {
        Map<String,Object> map =   getKeyPair(true,RandomUtils.generateString(8));
        String privateKey =getPrivateKey(map) ;
        String publicKey =getPublicKey(map) ;
        System.out.println("私钥 "+ privateKey);
        System.out.println("公钥 "+ publicKey);

        String password = "adasdashduash" ;
        //公钥加密
        String afterPublicKeyen =  encryptByPublicKey(password,publicKey);
        System.out.println("公钥加密后: " + afterPublicKeyen);
        //私钥解密
        String afterPride =  decryptByPrivateKey(afterPublicKeyen,privateKey);
        System.out.println("私钥解密后: " + afterPride);
        System.out.println("base解密:" + CodeUtils.decodeBASE64(afterPride));

        System.out.println("sh1加密转16进制:" + CodeUtils.sha1Hex(afterPride) );
    }
}
