package com.security.web.controllers;

import com.security.captcha.CaptchaService;
import com.security.captcha.InvalidRecaptchaException;
import com.security.captcha.ReCaptchaInvalidException;
import com.security.jwt.utils.Keys;
import com.security.web.domain.Registry;
import com.security.web.dto.JwtAuthenticationDTO;
import com.security.web.dto.TokenDTO;
import com.security.web.domain.CheckedTokenInfoDTO;
import com.security.web.domain.User;
import com.security.web.dto.Response;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.mappers.UserMapper;
import com.security.web.services.TokenService;
import com.security.web.services.UserService;
import com.security.web.configurations.CustomMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Locale;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/token")
public class TokensController {

	private final TokenService tokenService;
	private final UserMapper userMapper;

	/**
	 * Creates and returns a new token JWT.
	 * 
	 * @param authenticationDto
	 * @return
	 */
	@PostMapping("/create")
	public ResponseEntity<Response<TokenDTO>> createTokenJwt(@Valid @RequestBody JwtAuthenticationDTO authenticationDto) {
		User user = userMapper.jwtAuthenticationDtoToUser(authenticationDto);
		String token = tokenService.createToken(user, authenticationDto.getApplication());
		return ResponseEntity.ok(new Response<>(new TokenDTO(token)));
	}
	
	@GetMapping(value="/refresh")
	public ResponseEntity<Response<TokenDTO>> refreshTokenJwt(HttpServletRequest request){
		log.info("Refreshing JWT token");
		String refreshedToken = tokenService.refreshToken(request.getHeader(Keys.TOKEN_HEADER));
		return ResponseEntity.ok(new Response<>(new TokenDTO(refreshedToken)));
	}
	
	/**
	 * Returns a list of errors if the token has problems. Otherwise, returns an empty
	 * list of errors.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(value="/check")
	public ResponseEntity<Response<CheckedTokenInfoDTO>> checkToken(HttpServletRequest request){
		log.info("Checking JWT token");
		User user = tokenService.checkToken(request.getHeader(Keys.TOKEN_HEADER));
		// Verify if user has register for the required application
		return ResponseEntity.ok(new Response<>(
				userMapper.userToCheckedTokenInfoDto(user)
		));
	}
	
}
