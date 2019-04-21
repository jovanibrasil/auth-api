package com.jwt.security.controllers;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.response.Response;
import com.jwt.security.dto.DTOUtils;
import com.jwt.security.dto.UserDetailsDTO;
import com.jwt.security.dto.UserDto;
import com.jwt.security.entities.User;
import com.jwt.security.services.UserService;
import com.jwt.security.services.impl.UserServiceException;

@RestController
@RequestMapping("/userdetails")
@CrossOrigin(origins = "*")
public class UserDetailsController {

	/**
	 *  
	 * adiciona testes unitários para algumas classes
	 * muda a utilização de userrepository para userservice
	 * adicionar metódo para adicionar error na classe Respose
	 * separa operações de token em um controller
	 * adiciona update de usuário. Pode ser feito o update de nome, email e senha
	 * adiciona um update de detalhes de usuário
	 * retorna UNPROCESSABLE_ENTITY (522) caso haja erro nos dados recebidos
	 * return FORBIDDEN (403) caso não seja possível registrar um usuário
	 * retorna UNAUTHORIZED (401) caso os dados de autenticação estejam errados
	 * adiciona mensagens de validação para o user DTO
	 * adciona validações da criação de um novo usuário
	 * 
	 * 
	 */
		
	private static final Logger log = LoggerFactory.getLogger(UserDetailsController.class);
	
	@Autowired
	private UserService userService;
		
	
	/**
	 * 
	 * Accessible only for local services
	 */
	@PutMapping
	public ResponseEntity<Response<UserDto>> updateUserDetails(@Valid @RequestBody UserDetailsDTO userDto, BindingResult result, HttpServletRequest request){
		
		log.info("Update user");
		Response<UserDto> response = new Response<>();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.addError(err.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
		}
		
		Optional<User> optUser = this.userService.findByUserName(userDto.getUserName());
		
		if(!optUser.isPresent()) {
			log.error("User not found.");
			response.addError("User not found.");
			return ResponseEntity.badRequest().body(response);
		}
		
		User currentUser = optUser.get();
		
		if(result.hasErrors()) {
			log.error("Validation error {}", result.getAllErrors());
			result.getAllErrors().forEach(err -> response.addError(err.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		try {
			this.userService.save(currentUser);
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setData(DTOUtils.userToUserDTO(currentUser));
		return ResponseEntity.ok(response);
	}
		
}
