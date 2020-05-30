package com.security.web.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jovanibrasil.captcha.aspect.Recaptcha;
import com.security.web.domain.User;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.form.UpdateUserForm;
import com.security.web.domain.form.UserForm;
import com.security.web.domain.mappers.UserMapper;
import com.security.web.service.UserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;
	
	@ApiOperation(value = "Busca usuário por nome.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Usuario encontrado e retornado.", response = UserDTO.class),
		@ApiResponse(code = 404, message = "Usuário não encontrado.")})
	@GetMapping("/{userName}")
	public ResponseEntity<UserDTO> getUser(@PathVariable String userName) {
		User user = userService.findUserByUserName(userName);
		return ResponseEntity.ok(userMapper.userToUserDto(user));
	}
	
	@ApiOperation(value = "Busca usuário por email.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Usuario encontrado e retornado.", response = UserDTO.class),
		@ApiResponse(code = 404, message = "Usuário não encontrado.")})
	@GetMapping
	public ResponseEntity<UserDTO> getUserByEmail(@RequestParam(required = true) String email) {
		User user = userService.findUserByEmail(email);
		return ResponseEntity.ok(userMapper.userToUserDto(user));
	}
	
	@ApiOperation("Cria um usuário.")
	@ApiResponses({@ApiResponse(code = 200, message = "Usuário criado com sucesso.", response = Object.class)})
	@Recaptcha
	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody UserForm userForm) {
		log.info("User registration");

		User user = userService.saveUser(userMapper.userFormToUser(userForm));
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{userName}")
				.buildAndExpand(user.getUsername())
				.toUri();
		
		return ResponseEntity.created(uri).build();
	}

	@ApiOperation("Atualiza um usuário.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Usuário atualizado com sucesso.", response = UserDTO.class),
		@ApiResponse(code = 404, message = "Usuário não encontrado.", response = Object.class)
	})
	@Recaptcha
	@PutMapping
	public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UpdateUserForm userDto){
		User user = userService.updateUser(userMapper.updateUserDtoToUser(userDto));
		return ResponseEntity.ok().body(userMapper.userToUserDto(user));
	}

	@ApiOperation(value = "Remove um usuário.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Usuário removido."),
		@ApiResponse(code = 404, message = "Usuário não encontrado.")})
	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteUser(@PathVariable("username") String userName){
		log.info("Delete user {}", userName);
		userService.deleteUserByName(userName);
		return ResponseEntity.noContent().build();
	}
	
}
