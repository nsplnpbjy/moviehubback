package com.comradegenrr.moviehubback.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

    @Value("secretkey")
    private String SECRET_KEY;

    // 定义一个令牌有效期，单位为毫秒
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // 生成令牌的方法，接收一个用户名作为参数
    public  String generateToken(String username) {
        // 使用JwtBuilder创建一个令牌
        JwtBuilder builder = Jwts.builder()
                .setSubject(username) // 设置主题
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY); // 设置签名算法和密钥
        // 返回生成的令牌字符串
        return builder.compact();
    }

    // 解析令牌的方法，接收一个令牌字符串作为参数，返回一个用户名
    public  String parseToken(String token) {
        // 使用JwtParser解析令牌
        JwtParser parser = Jwts.parser()
                .setSigningKey(SECRET_KEY); // 设置密钥
        // 获取令牌中的主体
        String username = parser.parseClaimsJws(token)
                .getBody()
                .getSubject();
        // 返回主体
        return username;
    }

    // 验证令牌的方法，接收一个令牌字符串作为参数，返回一个布尔值，表示令牌是否有效
    public  boolean validateToken(String token) {
        try {
            // 使用JwtParser解析和验证令牌，如果成功则返回true，如果失败则抛出异常
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 捕获异常并返回false
            return false;
        }
    }
}
