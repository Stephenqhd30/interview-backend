package com.stephen.popcorn.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 生成和解析JWT的工具类
 *
 * @author: stephen qiu
 **/
public class JWTUtils {
	
	// 签名密钥
	private static final String SECRET = "popcorn";
	
	/**
	 * 生成token
	 *
	 * @param payload token携带的信息
	 * @return token字符串
	 */
	public static String getToken(Map<String, Object> payload) {
		// 指定token过期时间为7天
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 7);
		
		JWTCreator.Builder builder = JWT.create();
		// 构建payload
		payload.forEach((key, value) -> builder.withClaim(key, value.toString()));
		// 指定过期时间和签名算法
		return builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SECRET));
	}
	
	
	/**
	 * 解析token
	 *
	 * @param token token字符串
	 * @return 解析后的token
	 */
	public static DecodedJWT decode(String token) {
		// 校验JWT的合法性
		boolean validateToken = validateToken(token);
		if (validateToken) {
			JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			return jwtVerifier.verify(token);
		}
		return null;
	}
	
	/**
	 * 验证JWT的有效性
	 *
	 * @param token JWT字符串
	 * @return 是否有效
	 */
	public static boolean validateToken(String token) {
		try {
			JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			DecodedJWT decodedJWT = jwtVerifier.verify(token);
			
			// 检查是否过期
			Date expiresAt = decodedJWT.getExpiresAt();
			return expiresAt != null && expiresAt.after(new Date());
		} catch (Exception e) {
			// 如果验证失败，则返回false
			return false;
		}
	}
	
	
}
