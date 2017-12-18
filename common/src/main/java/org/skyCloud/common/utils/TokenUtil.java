package org.skyCloud.common.utils;

import io.jsonwebtoken.*;
import org.skyCloud.common.exception.SkyException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class TokenUtil {
    /**
     * 生成token
     * @param  username 用户名
     * @param  ttlMillis 过期时间
     * @param  secret 签名秘钥
     * @param  claims 需要缓存的一些基础数据字段
     */
    public static String getToken(String username, long ttlMillis,
                                  String secret, Map<String, Object> claims) {
        if (isEmpty(username) || isEmpty(secret)) {
            throw new SkyException("110","null");
        }
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes,
                signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT").setIssuer("rt")//头部(Header)
                .setSubject(username).setAudience("user").setClaims(claims) //载荷（payload） Audience 接收方 用户
                .setIssuedAt(new Date())
                .signWith(signatureAlgorithm, signingKey);
        // 添加Token过期时间
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }
        return builder.compact();
    }

    /**
     * 判断token是否有效
     * @param token token
     * @param secret 密钥
     */
    public static boolean isValid(String token, String secret) {
        try {
            Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                    .parseClaimsJws(token.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析token获取用户名
     * @param jwsToken token
     * @param secret 密钥
     */
    public static String getFieldValue(String jwsToken, String secret,String fieldName) {
        if (isValid(jwsToken, secret)) {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                    .parseClaimsJws(jwsToken);
            return String.valueOf(claimsJws.getBody().get(fieldName));
        }
        return null;
    }

    private static boolean isEmpty(String token){
        return (token == null || token.trim().length() == 0);
    }
}
