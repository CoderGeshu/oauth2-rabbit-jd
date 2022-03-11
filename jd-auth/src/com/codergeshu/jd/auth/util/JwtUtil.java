package com.codergeshu.jd.auth.util;

import com.codergeshu.jd.auth.common.AuthInfo;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * @Date: 2022/3/10 21:44
 * @Author: Eric
 */
public class JwtUtil {

    private static final String USER_ID = "userId";
    private static final String APP_ID = "appId";
    private static final String SCOPE = "scope";

    public static String generateToken(AuthInfo authInfo, Long ttlMillis) throws Exception {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 3600000 * 24 * 7; // 默认有效期为7天
        if (ttlMillis != null && ttlMillis >= 0) {
            expMillis = nowMillis + ttlMillis;
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, authInfo.getUserId());
        claims.put(APP_ID, authInfo.getAppId());
        claims.put(SCOPE, authInfo.getScope());
        SecretKey key = generateKey();
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)    // 设置声明
                .setId(generateJti()) // 设置jti
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date((expMillis)))
                .setSubject(authInfo.getUserId())  // JWT的主体，即它的所有人，可以是一个json格式的字符串，作为用户的唯一标志。
                .signWith(key, signatureAlgorithm);
        return builder.compact();
    }

    /**
     * 从token中获取用户的信息
     */
    public static AuthInfo getAuthInfoFromToken(String token) throws Exception {
        Claims claims = parseToken(token);
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAppId(claims.get(APP_ID).toString());
        authInfo.setUserId(claims.get(USER_ID).toString());
        authInfo.setScope((List<String>) claims.get(SCOPE));
        return authInfo;
    }


    /**
     * 解析token的claims
     */
    private static Claims parseToken(String token) throws Exception {
        SecretKey key = generateKey();  //签名秘钥，和生成的签名的秘钥一模一样
        JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
        return parser.parseClaimsJws(token).getBody();
    }

    /**
     * 一个生成密钥的方法
     */
    private static SecretKey generateKey() {
        String secretKey = "7786df7fc3a34e26a61c034d5ec8245d";
        byte[] encodedKey = Base64.getEncoder().encode(secretKey.getBytes()); // 编码成byte[]
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
    }

    /**
     * jti：JWT ID，为 JWT 提供唯一标识符，用于防止重播 JWT
     */
    private static String generateJti() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

//    public static void main(String[] args) {
//        AuthInfo authInfo = new AuthInfo();
//        authInfo.setUserId("CoderGeshu#12345");
//        authInfo.setAppId("rabbit_app#12");
//        List<String> list = new ArrayList<>();
//        list.add("query");
//        authInfo.setScope(list);
//        try {
//            String token = generateToken(authInfo, null);
//            System.out.println("token: " + token);
//            AuthInfo result = getAuthInfoFromToken(token);
//            System.out.println(result);
//        } catch (Exception e) {
//            System.out.println("token错误");
//        }
//    }

}
