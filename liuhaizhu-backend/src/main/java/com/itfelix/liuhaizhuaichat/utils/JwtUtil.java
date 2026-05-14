package com.itfelix.liuhaizhuaichat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * @author aceFelix
 */
@Component
public class JwtUtil {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;
    /**
     * 生成accessToken
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return 生成的accessToken
     */
    public String generateToken(String userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS);
        return createToken(claims, userId, expiration);
    }
    /**
     * 生成refreshToken
     * @param userId 用户ID
     * @return 生成的refreshToken
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH);
        return createToken(claims, userId, refreshExpiration);
    }
    /**
     * 创建JWT token
     * @param claims 声明
     * @param subject 主题（通常是用户ID）
     * @param expiration 过期时间（毫秒）
     * @return 生成的JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withPayload(claims)
                .sign(algorithm);
    }
    /**
     * 从token中提取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        return jwt.getSubject();
    }
    /**
     * 从token中提取用户名
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        return jwt.getClaim("username").asString();
    }
    /**
     * 从token中提取用户角色
     * @param token JWT token
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        return jwt.getClaim("role").asString();
    }
    /**
     * 验证JWT token是否有效
     * @param token JWT token
     * @return 如果token有效则返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        try {
            DecodedJWT jwt = decodeToken(token);
            return !jwt.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    /**
     * 验证是否为有效的refreshToken
     * 防止用accessToken冒充refreshToken来刷新
     * @param token JWT token
     * @return 如果token有效则返回true，否则返回false
     */
    public Boolean validateRefreshToken(String token) {
        try {
            DecodedJWT jwt = decodeToken(token);
            if (jwt.getExpiresAt().before(new Date())) {
                return false;
            }
            String tokenType = jwt.getClaim(TOKEN_TYPE_CLAIM).asString();
            return TOKEN_TYPE_REFRESH.equals(tokenType);
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    /**
     * 验证是否为有效的accessToken
     * @param token JWT token
     * @return 如果token有效则返回true，否则返回false
     */
    public Boolean validateAccessToken(String token) {
        try {
            DecodedJWT jwt = decodeToken(token);
            if (jwt.getExpiresAt().before(new Date())) {
                return false;
            }
            String tokenType = jwt.getClaim(TOKEN_TYPE_CLAIM).asString();
            return TOKEN_TYPE_ACCESS.equals(tokenType);
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    /**
     * 解码JWT token
     * @param token JWT token
     * @return 解码后的JWT对象
     */
    private DecodedJWT decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
