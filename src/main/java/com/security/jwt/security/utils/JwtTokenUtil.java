package com.security.jwt.security.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.security.jwt.exceptions.InvalidTokenException;
import com.security.jwt.utils.ApplicationType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

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
	static final String CLAIM_KEY_EMAIL = "email";
	static final String CLAIM_KEY_APPLICATION_NAME = "appname";
	static final String CLAIM_KEY_PASSWORD = "password";
	
	// Get data from application.properties
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private Long expiration;
	
	public JwtTokenUtil(String secret, Long expiration) {
		this.secret = secret;
		this.expiration = expiration;
	}
	
	public JwtTokenUtil() {}
	
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
	public String createToken(UserDetails userDetails, ApplicationType application) {
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
	public String createRegistrationToken(String email, ApplicationType application) {
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
	 * @throws InvalidTokenException
	 */
	public Claims getClaimsFromToken(String token) throws InvalidTokenException {
		try {
			return Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();
		} catch (UnsupportedJwtException e) {
			throw new InvalidTokenException("the token format is not supported.");
		} catch (MalformedJwtException e) {
			throw new InvalidTokenException("The token was not correctly constructed.");
		} catch (SignatureException e) {
			throw new InvalidTokenException("Calculating a signature or verifying an existing signature of a JWT failed");
		} catch (ExpiredJwtException e) {
			throw new InvalidTokenException("The token is expired.");
		} catch (Exception e) {
			throw new InvalidTokenException("Unexpected error while processing the token.");
		}
	}
	
	/**
	 * Retrieves the user name from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public String getUserNameFromToken(String token) throws InvalidTokenException {
		try {
			Claims claims = this.getClaimsFromToken(token);
			return claims.getSubject();	
		} catch (InvalidTokenException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidTokenException("Error getting user name from the token.");
		}
	}
	
	/**
	 * Retrieves user authority from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public String getAuthority(String token) throws InvalidTokenException {
		try {
			Claims claims = this.getClaimsFromToken(token);
			return claims.get(CLAIM_KEY_ROLE).toString();
		} catch (InvalidTokenException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidTokenException("Error getting user authority from the token.");
		}
	}
	
	/**
	 * Retrieves expiration date from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public Date getExpirationDate(String token) throws InvalidTokenException {
		Claims claims = this.getClaimsFromToken(token);
		if(claims == null) throw new InvalidTokenException("Expiration date is null.");
		return claims.getExpiration();
	}
	
	/**
	 * Retrieves application name from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public String getApplicationName(String token) throws InvalidTokenException {
		Claims claims = this.getClaimsFromToken(token);
		Object obj = claims.get(CLAIM_KEY_APPLICATION_NAME);
		if(obj == null) throw new InvalidTokenException("Application name is null");
		return (String) obj; 
	}
	
	/**
	 * Retrieves email from the token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public String getEmailFromToken(String token) throws InvalidTokenException {
		Claims claims = this.getClaimsFromToken(token);
		Object obj = claims.get(CLAIM_KEY_EMAIL);
		if(obj == null) throw new InvalidTokenException("Email is null.");
		return (String) obj;
	}
	
	/**
	 * Creates a new token JWT.
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public String refreshToken(String token) throws InvalidTokenException {
		Claims claims = this.getClaimsFromToken(token);
		claims.put(CLAIM_KEY_CREATED, new Date());
		return buildToken(claims);
	}
	
	/**
	 * Verifies if a token JWT is valid.
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public boolean tokenIsValid(String token) throws InvalidTokenException {
		try {
			Date expirationDate = this.getExpirationDate(token);
			if(expirationDate == null) return false;
			return new Date().before(expirationDate);
		} catch (Exception e) {
			return false;
		}
	}
	
}
