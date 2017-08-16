package org.skyCloud.utils;

import io.jsonwebtoken.Jwts;

import javax.xml.bind.DatatypeConverter;

public class TokenUtil {

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
}
