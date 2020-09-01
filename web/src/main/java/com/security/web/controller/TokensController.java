package com.security.web.controller;

import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.jwt.util.Keys;
import com.security.web.domain.User;
import com.security.web.domain.dto.CheckedTokenInfoDTO;
import com.security.web.domain.dto.TokenDTO;
import com.security.web.domain.form.JwtAuthenticationForm;
import com.security.web.domain.mappers.UserMapper;
import com.security.web.service.TokenService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/token")
public class TokensController {

	private final TokenService tokenService;
	private final UserMapper userMapper;

	@ApiOperation(value = "Cria um token JWT.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Token criado com sucesso.", response = TokenDTO.class),
		@ApiResponse(code = 400, message = "Requisição inválida.")})
	@PostMapping
	public ResponseEntity<TokenDTO> createTokenJwt(@Valid @RequestBody JwtAuthenticationForm authenticationDto) {
		User user = userMapper.jwtAuthenticationDtoToUser(authenticationDto);
		String token = tokenService.createToken(user, authenticationDto.getApplication());
		return ResponseEntity.ok(new TokenDTO(token));
	}
	
	@ApiOperation(value = "Renova um token JWT.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Token renovado com sucesso.", response = TokenDTO.class),
		@ApiResponse(code = 400, message = "Requisição inválida.")})
	@GetMapping(value="/refresh")
	public ResponseEntity<TokenDTO> refreshTokenJwt(HttpServletRequest request){
		log.info("Refreshing JWT token");
		String refreshedToken = tokenService.refreshToken(request.getHeader(Keys.TOKEN_HEADER));
		return ResponseEntity.ok(new TokenDTO(refreshedToken));
	}
	
	@ApiOperation(value = "Verifica se um token JWT é válido.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Token válido.", response = CheckedTokenInfoDTO.class),
		@ApiResponse(code = 400, message = "Token inválido.")})
	@GetMapping(value="/check")
	public ResponseEntity<CheckedTokenInfoDTO> checkToken(HttpServletRequest request){
		log.info("Checking JWT token");
		// Verify if the token is valid and the user has register for the required application
		User user = tokenService.checkToken(request.getHeader(Keys.TOKEN_HEADER));
		return ResponseEntity.ok(userMapper.userToCheckedTokenInfoDto(user));
	}
	
}
