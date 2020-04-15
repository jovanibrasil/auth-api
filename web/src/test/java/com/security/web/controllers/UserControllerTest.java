package com.security.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.captcha.CaptchaServiceImpl;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.domain.Application;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import com.security.web.dto.ConfirmUserDTO;
import com.security.web.dto.CreateUserDTO;
import com.security.web.dto.RegistrationUserDTO;
import com.security.web.exceptions.implementations.NotFoundException;
import com.security.web.exceptions.implementations.ValidationException;
import com.security.web.mappers.UserMapper;
import com.security.web.services.UserService;
import com.security.web.services.impl.IntegrationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private JwtTokenGenerator jwtTokenUtil;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private CaptchaServiceImpl captchaService;

	@MockBean
	private UserMapper userMapper;

	private User user;
	private CreateUserDTO userDto;
	
	@Before
	public void setUp() {
		user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());
		user.setRegistries(Arrays.asList(
				new Registry(new Application(ApplicationType.BLOG_APP), user)));
		userDto = new CreateUserDTO();
		userDto.setId(1L);
		userDto.setEmail("test@gmail.com");
		userDto.setUserName("test");
		userDto.setPassword("password");
		userDto.setApplication(ApplicationType.BLOG_APP);
		
	}

	/**
	 * Test user creation with valid data.
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateUser() throws Exception {
		when(userService.saveUser(Mockito.any())).thenReturn(user);
		when(userMapper.registrationUserDtoToUser(any())).thenReturn(user);

		// validate recaptcha successfully
		//	CaptchaService captchaService = mock(CaptchaService.class);
		Mockito.doNothing().when(captchaService).processResponse(null);

		RegistrationUserDTO userRegDTO = new RegistrationUserDTO();
		userRegDTO.setPassword("teste");
		userRegDTO.setUserName("teste");
		userRegDTO.setToken(jwtTokenUtil.createRegistrationToken("teste@gmail.com", ApplicationType.BLOG_APP));

		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isCreated());
	}
	
	/**
	 * Test user creation with an user name that already exists.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserNameAlreadyExists() throws Exception {
		when(userService.saveUser(Mockito.any()))
				.thenThrow(ValidationException.class);
		when(userMapper.registrationUserDtoToUser(any())).thenReturn(user);
		when(userService.existUserWithUserName(any())).thenReturn(true);

		RegistrationUserDTO userRegDTO = new RegistrationUserDTO();
		userRegDTO.setPassword("teste");
		userRegDTO.setUserName("teste");
		userRegDTO.setToken(jwtTokenUtil.createRegistrationToken("teste@gmail.com", ApplicationType.BLOG_APP));
		
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", equalTo("This user name already exists.")));
		
	}
	
	/**
	 * Test user creation with empty user name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserEmptyUserName() throws Exception {
		BDDMockito.given(userService
			.saveUser(Mockito.any())).willReturn(user); // save successfully
		// send email successfully
		IntegrationServiceImpl integration = mock(IntegrationServiceImpl.class);
		BDDMockito.doNothing().when(integration)
			.sendEmail(BDDMockito.any());
		
		RegistrationUserDTO userRegDTO = new RegistrationUserDTO();
		userRegDTO.setPassword("teste");
		userRegDTO.setUserName("");
		userRegDTO.setToken(jwtTokenUtil.createRegistrationToken("teste@gmail.com", ApplicationType.BLOG_APP));
		
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Username must not be blank or null.")));
	}
	
	/**
	 * Test user creation with empty password.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserEmptyPassword() throws Exception {
		BDDMockito.given(userService
			.saveUser(Mockito.any())).willReturn(user); // save successfully
		// send email successfully
		IntegrationServiceImpl integration = mock(IntegrationServiceImpl.class);
		BDDMockito.doNothing().when(integration)
			.sendEmail(BDDMockito.any());
		
		RegistrationUserDTO userRegDTO = new RegistrationUserDTO();
		userRegDTO.setPassword("");
		userRegDTO.setUserName("teste");
		userRegDTO.setToken(jwtTokenUtil.createRegistrationToken("teste@gmail.com", ApplicationType.BLOG_APP));
		
		List<String> passwordErrors = Arrays.asList(
				"Password must not be blank or null.",
				"Password length must be between 4 and 12.");
		
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", 
					isIn(passwordErrors)));
	}
	
	/**
	 * Test user creation with null user name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserNullUserName() throws Exception {
		BDDMockito.given(userService
			.saveUser(Mockito.any())).willReturn(user); // save successfully
		// send email successfully
		IntegrationServiceImpl integration = mock(IntegrationServiceImpl.class);
		BDDMockito.doNothing().when(integration)
			.sendEmail(BDDMockito.any());
		
		RegistrationUserDTO userRegDTO = new RegistrationUserDTO();
		userRegDTO.setPassword("teste");
		userRegDTO.setUserName(null);
		userRegDTO.setToken(jwtTokenUtil.createRegistrationToken("teste@gmail.com", ApplicationType.BLOG_APP));
		
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Username must not be blank or null.")));
	}
	
	/**
	 * Test user creation with null password.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserNullPassword() throws Exception {
		BDDMockito.given(this.userService
			.saveUser(Mockito.any())).willReturn(user); // save successfully
		// send email successfully
		IntegrationServiceImpl integration = mock(IntegrationServiceImpl.class);
		BDDMockito.doNothing().when(integration)
			.sendEmail(BDDMockito.any());
		
		RegistrationUserDTO userRegDTO = new RegistrationUserDTO();
		userRegDTO.setPassword(null);
		userRegDTO.setUserName("teste");
		userRegDTO.setToken(jwtTokenUtil.createRegistrationToken("teste@gmail.com", ApplicationType.BLOG_APP));
		
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Password must not be blank or null.")));
	}
	
	/**
	 * Tests an email confirmation of an email that already exists.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConfirmUserEmailAlreadyExists() throws Exception {
		when(userService.existUserWithEmail(any())).thenReturn(true);
		BDDMockito.willThrow(new ValidationException(""))
				.given(userService).confirmUserEmail(any(), any());
		mvc.perform(MockMvcRequestBuilders.post("/users/confirmation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message",
					equalTo("This email already exists.")));
	}
	
	@Test
	public void testConfirmUserEmptyEmail() throws Exception {
		ConfirmUserDTO userRegDTO = new ConfirmUserDTO();
		userRegDTO.setEmail("");
		userRegDTO.setApplication(ApplicationType.BLOG_APP);
		
		mvc.perform(MockMvcRequestBuilders.post("/users/confirmation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Email must not be blank or null.")));
	}
	
	@Test
	public void testConfirmUserNullApplication() throws Exception {
		ConfirmUserDTO userRegDTO = new ConfirmUserDTO();
		userRegDTO.setEmail("jovanibrasil@gmail.com");
		userRegDTO.setApplication(null);
		
		mvc.perform(MockMvcRequestBuilders.post("/users/confirmation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Application cannot be null.")));
	}
	
	@Test
	public void testConfirmUserNullEmail() throws Exception {
		ConfirmUserDTO userRegDTO = new ConfirmUserDTO();
		userRegDTO.setEmail(null);
		userRegDTO.setApplication(ApplicationType.BLOG_APP);
		
		mvc.perform(MockMvcRequestBuilders.post("/users/confirmation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userRegDTO)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Email must not be blank or null.")));
	}
	
//	/**
//	 * Tests the user update operation. The updated user is valid.
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testUpdateUser() throws Exception {
//		User updatedUser = new User();
//		updatedUser.setId(1L);
//		updatedUser.setEmail("newtest@gmail.com");
//		updatedUser.setUserName("newtest");
//		updatedUser.setPassword("password");
//		user.setRegistries(Arrays.asList(new Registry(new Application(ApplicationType.BLOG_APP), user)));
//
//		// SecurityContextHolder.getContext().getAuthentication().getName();
////		org.springframework.security.core.Authentication auth = authenticationManager.authenticate(
////				new UsernamePasswordAuthenticationToken(userName, userDto.getActualPassword()));
//
//		BDDMockito.given(this.userService.findByUserName("newtest"))
//			.willReturn(Optional.of(updatedUser));
//
//		UpdateUserDTO updateUserDto = new UpdateUserDTO("password", "newpassword");
//		BDDMockito.given(this.userService.updateUser(Mockito.any()))
//			.willReturn(updatedUser);
//
//		mvc.perform(MockMvcRequestBuilders.put("/users")
//			.header("Authorization", "Bearer x.x.x.x")
//			.contentType(MediaType.APPLICATION_JSON)
//			.content(asJsonString(updateUserDto)))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.errors").isEmpty())
//			.andExpect(jsonPath("$.data.userName", equalTo("newtest")))
//			.andExpect(jsonPath("$.data.email", equalTo("newtest@gmail.com")));
//	}
	
	/**
	 * Tests a deletion of a valid and existent user.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteUser() throws Exception {
		BDDMockito.doNothing().when(userService).deleteUserByName(any());
		mvc.perform(MockMvcRequestBuilders.delete("/users/test")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}
	
	/**
	 * Tests a deletion of a non-existent user.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeleteInvalidUser() throws Exception {
		doThrow(new NotFoundException("error.user.notfound"))
				.when(this.userService).deleteUserByName(Mockito.anyString());
		mvc.perform(MockMvcRequestBuilders.delete("/users/java")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message",
					equalTo("User not found.")));
	}

	/**
	 * Tests full registration process.
	 *
	 * @throws Exception
	 */
	public void testFullRegistrationProcess() throws Exception {
		// todo
	}

	public static String asJsonString(final Object obj) {
	    try {
	        final ObjectMapper mapper = new ObjectMapper();
	        final String jsonContent = mapper.writeValueAsString(obj);
	        return jsonContent;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}
