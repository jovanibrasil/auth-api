package com.jwt.security.utils;

import java.io.Console;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/*
 * Set of method used to manipulate JWT tokens.
 * 
 * @Author Jovani Brasil
 * 
 */
@Component
public class JwtTokenUtil {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_ROLE = "role";
	static final String CLAIM_KEY_CREATED = "created";
	static final String CLAIM_KEY_APPLICATION_NAME = "appname";
	
	// Get data from application.properties
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private Long expiration;
	
	/*
	 * Create a new token JWT.
	 */
	private String createToken(Map<String, Object> claims) {
		// Define the expiration date
		Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
		// Generate a new token
		return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, secret).compact();
	}
	
	/*
	 * Create a new token JWT.
	 */
	public String createToken(UserDetails userDetails, String applicationName) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		userDetails.getAuthorities().forEach(authority -> claims.put(CLAIM_KEY_ROLE, authority.getAuthority()));
		claims.put(CLAIM_KEY_CREATED, new Date());
		claims.put(CLAIM_KEY_APPLICATION_NAME, applicationName);
		return createToken(claims);
	}
	
	/*
	 * Get user name from the token JWT.
	 */
	public String getUserNameFromToken(String token) {
		try {
			Claims claims = this.getClaimsFromToken(token);
			return claims.getSubject();
		} catch (Exception e) {
			return null;
		}
	}
	
	/*
	 * Extract informations from the token JWT.
	 */
	public Claims getClaimsFromToken(String token){
		try {
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			return null;
		}
	}
	
	/*
	 * Get expiration date from the token JWT.
	 */
	public Date getExpirationDate(String token) {
		try {
			Claims claims = this.getClaimsFromToken(token);
			return claims.getExpiration();
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getApplicationName(String token) {
		try {
			Claims claims = this.getClaimsFromToken(token);
			Object obj = claims.get(CLAIM_KEY_APPLICATION_NAME);
			if(obj == null) {
				return "";
			}
			return (String) obj; 
		} catch (Exception e) {
			return null;
		}
	}
	
	/*
	 * Create a new token JWT.
	 */
	public String refreshToken(String token) {
		try {
			Claims claims = this.getClaimsFromToken(token);
			claims.put(CLAIM_KEY_CREATED, new Date());
			return createToken(claims);
		} catch (Exception e) {
			return null;
		}
	}
	
	/*
	 * Verify if a token JWT is valid.
	 */
	public boolean tokenIsValid(String token) {
		Date expirationDate = this.getExpirationDate(token);
		System.out.println(expirationDate);
		if(expirationDate == null)
			return false;
		return !expirationDate.before(new Date());
	}
	
}
