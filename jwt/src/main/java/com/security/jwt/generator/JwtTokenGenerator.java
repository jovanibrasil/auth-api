package com.security.jwt.generator;

import com.security.jwt.exceptions.TokenException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * Set of methods used to manipulate JWT tokens.
 * 
 * @Author Jovani Brasil
 * 
 */
@Component
public class JwtTokenGenerator<T> {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_ROLE = "role";
	static final String CLAIM_KEY_CREATED = "created";
	static final String CLAIM_KEY_EMAIL = "email";
	static final String CLAIM_KEY_APPLICATION_NAME = "appname";
	static final String CLAIM_KEY_PASSWORD = "password";
	
	// Get data from application.properties
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private Long expiration;
		
	public JwtTokenGenerator(String secret, Long expiration) {
		this.secret = secret;
		this.expiration = expiration;
	}
	
	public JwtTokenGenerator() {}
	
	/**
	 * Creates a new token JWT.
	 * 
	 * @param claims
	 * @return
	 */
	private String buildToken(Map<String, Object> claims) {
		// Define the expiration date
		Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
		// Generate a new token
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}
	
	/**
	 * Creates a new token JWT with username, roles, creation date and application name.
	 * 
	 * @param userDetails
	 * @param application
	 * @return
	 */
	public String createToken(UserDetails userDetails, T application) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		userDetails.getAuthorities().forEach(authority -> {
			claims.put(CLAIM_KEY_ROLE, authority.getAuthority());
		});
		claims.put(CLAIM_KEY_CREATED, new Date());
		claims.put(CLAIM_KEY_APPLICATION_NAME, application);	
		
		return buildToken(claims);
	}

	/**
	 * Creates a new token JWT with user email, creation date and application name. 
	 * 
	 * @param email
	 * @param application
	 * @return
	 */
	public String createRegistrationToken(String email, T application) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_CREATED, new Date());
		claims.put(CLAIM_KEY_EMAIL, email);
		claims.put(CLAIM_KEY_APPLICATION_NAME, application);	
		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}
	
	/**
	 * 
	 * Retrieves informations from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public Claims getClaimsFromToken(String token) throws TokenException {
		try {
			return Jwts.parser() 			// Gets a JWT parser instance
					.setSigningKey(secret)  // Sets the sign key used to verify the JWS signature
					.parseClaimsJws(token)  // Parses JWS token. Throws an exception if the token aren't a JWS token. 
					.getBody();
		} catch (UnsupportedJwtException e) {
			throw new TokenException("the token format is not supported. The token is not a JWS token. " + e.getMessage());
		} catch (MalformedJwtException e) {
			throw new TokenException("The token was not correctly constructed.");
		} catch (SignatureException e) {
			throw new TokenException("Calculating a signature or verifying an existing signature of a JWS failed");
		} catch (ExpiredJwtException e) {
			throw new TokenException("The token is expired.");
		} catch (TokenException e) {
			throw new TokenException("Unexpected error while processing the token.");
		}
	}
	
	/**
	 * Retrieves the user name from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public String getUserNameFromToken(String token) throws TokenException {
		try {
			Claims claims = getClaimsFromToken(token);
			return claims.getSubject();	
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException("Error getting user name from the token.");
		}
	}
	
	/**
	 * Retrieves user authority from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public String getAuthority(String token) throws TokenException {
		try {
			Claims claims = getClaimsFromToken(token);
			return claims.get(CLAIM_KEY_ROLE).toString();
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException("Error getting user authority from the token.");
		}
	}
	
	/**
	 * Retrieves expiration date from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public Date getExpirationDate(String token) throws TokenException {
		Claims claims = getClaimsFromToken(token);
		if(claims == null) throw new TokenException("Expiration date is null.");
		return claims.getExpiration();
	}
	
	/**
	 * Retrieves application name from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public String getApplicationName(String token) throws TokenException {
		Claims claims = getClaimsFromToken(token);
		Object obj = claims.get(CLAIM_KEY_APPLICATION_NAME);
		if(obj == null) throw new TokenException("Application name is null");
		return (String) obj; 
	}
	
	/**
	 * Retrieves email from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public String getEmailFromToken(String token) throws TokenException {
		Claims claims = getClaimsFromToken(token);
		Object obj = claims.get(CLAIM_KEY_EMAIL);
		if(obj == null) throw new TokenException("Email is null.");
		return (String) obj;
	}
	
	/**
	 * Creates a new token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public String refreshToken(String token) throws TokenException {
		Claims claims = getClaimsFromToken(token);
		claims.put(CLAIM_KEY_CREATED, new Date());
		return buildToken(claims);
	}
	
	/**
	 * Verifies if a token JWT is valid.
	 * 
	 * @param token
	 * @return
	 * @throws TokenException
	 */
	public boolean tokenIsValid(String token) throws TokenException {
		try {
			Date expirationDate = getExpirationDate(token);
			if(expirationDate == null) return false;
			return new Date().before(expirationDate);
		} catch (TokenException e) {
			return false;
		}
	}
	
}
