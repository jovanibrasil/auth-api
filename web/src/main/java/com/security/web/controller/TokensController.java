package com.security.web.controller;

import com.security.jwt.util.Keys;
import com.security.web.domain.User;
import com.security.web.domain.dto.CheckedTokenInfoDTO;
import com.security.web.domain.dto.TokenDTO;
import com.security.web.domain.form.JwtAuthenticationForm;
import com.security.web.domain.mappers.UserMapper;
import com.security.web.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
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
	public ResponseEntity<TokenDTO> createTokenJwt(@Valid @RequestBody JwtAuthenticationForm authenticationDto) {
		User user = userMapper.jwtAuthenticationDtoToUser(authenticationDto);
		String token = tokenService.createToken(user, authenticationDto.getApplication());
		return ResponseEntity.ok(new TokenDTO(token));
	}
	
	/**
	 * Refresh a valid token.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(value="/refresh")
	public ResponseEntity<TokenDTO> refreshTokenJwt(HttpServletRequest request){
		log.info("Refreshing JWT token");
		String refreshedToken = tokenService.refreshToken(request.getHeader(Keys.TOKEN_HEADER));
		return ResponseEntity.ok(new TokenDTO(refreshedToken));
	}
	
	/**
	 * Check a token and returns a list of errors if the token has problems. Otherwise, 
	 * returns an empty list of errors.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(value="/check")
	public ResponseEntity<CheckedTokenInfoDTO> checkToken(HttpServletRequest request){
		log.info("Checking JWT token");
		// Verify if the token is valid and the user has register for the required application
		User user = tokenService.checkToken(request.getHeader(Keys.TOKEN_HEADER));
		return ResponseEntity.ok(userMapper.userToCheckedTokenInfoDto(user));
	}
	
}
