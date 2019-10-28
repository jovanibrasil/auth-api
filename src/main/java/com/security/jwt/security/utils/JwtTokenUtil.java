package com.security.jwt.security.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.security.jwt.entities.Application;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.utils.ApplicationType;
import com.security.jwt.utils.PasswordUtils;

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
	
	/*
	 * Create a new token JWT.
	 */
	private String createToken(Map<String, Object> claims) {
		// Define the expiration date
		Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
		// Generate a new token
		return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, secret).compact();
	}
	
	public String createRegistrationToken(String email, ApplicationType application) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_CREATED, new Date());
		claims.put(CLAIM_KEY_EMAIL, email);
		claims.put(CLAIM_KEY_APPLICATION_NAME, application);	
		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secret).compact();
	}
	
	/*
	 * Create a new token JWT.
	 */
	public String createToken(UserDetails userDetails, ApplicationType application) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		userDetails.getAuthorities().forEach(authority -> {
			claims.put(CLAIM_KEY_ROLE, authority.getAuthority());
		});
		claims.put(CLAIM_KEY_CREATED, new Date());
		claims.put(CLAIM_KEY_APPLICATION_NAME, application);	
		
		return createToken(claims);
	}
	
	public User getUserFromToken(String token) {
		try {
			Claims claims = this.getClaimsFromToken(token);
			User user = new User();
			user.setUserName(claims.getSubject());
			user.setEmail((String)claims.get(CLAIM_KEY_EMAIL));
			user.setSignUpDate(new Date((Long)claims.get(CLAIM_KEY_CREATED)));
			user.setPassword((String)claims.get(CLAIM_KEY_PASSWORD));
			String applicationName = (String)claims.get(CLAIM_KEY_APPLICATION_NAME);
			user.setRegistries(Arrays.asList(new Registry(new Application(
					ApplicationType.valueOf(applicationName)), user)));
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
	 * Get user name from the token JWT.
	 */
	public String getAuthority(String token) {
		try {
			Claims claims = this.getClaimsFromToken(token);
			return claims.get(CLAIM_KEY_ROLE).toString();
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
	
	public String getEmailFromToken(@Valid @NotBlank String token) {
		try {
			Claims claims = this.getClaimsFromToken(token);
			Object obj = claims.get(CLAIM_KEY_EMAIL);
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
		if(expirationDate == null) return false;
		System.out.println("Expiration date: " + expirationDate);
		System.out.println("Atual date: " + new Date());
		return !expirationDate.before(new Date());
	}
	
}
