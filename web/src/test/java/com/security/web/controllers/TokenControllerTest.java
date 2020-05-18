package com.security.web.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.exceptions.TokenException;
import com.security.web.domain.Application;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import com.security.web.domain.form.JwtAuthenticationForm;
import com.security.web.domain.form.UserForm;
import com.security.web.domain.mappers.UserMapper;
import com.security.web.exceptions.implementations.ForbiddenUserException;
import com.security.web.exceptions.implementations.UnauthorizedUserException;
import com.security.web.services.TokenService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TokenControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private TokenService tokenService;

	@MockBean
	private UserMapper userMapper;

	private List<String> passwordBlankErrors = Arrays.asList("Password length must be between 4 and 12.", 
			"Password must not be blank or null.");
	
	private List<String> userNameBlankErrors = Arrays.asList("Username length must be between 2 and 12.", 
			"Username must not be blank or null.");
	
	private User user;
	private UserForm userForm;

	@Before
	public void setUp() {
		user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");
		user.setUsername("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());
		user.setRegistries(Arrays.asList(
				new Registry(new Application(ApplicationType.BLOG_APP), user)));
		userForm = new UserForm();
		userForm.setEmail("test@gmail.com");
		userForm.setUserName("test");
		userForm.setPassword("password");
		userForm.setApplication(ApplicationType.BLOG_APP);
		
	}
	
	/**
	 * Test token creation with a valid user information.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreation() throws Exception {

		when(userMapper.jwtAuthenticationDtoToUser(ArgumentMatchers.any()))
				.thenReturn(user);
		when(tokenService.createToken(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn("TOKEN");

		JwtAuthenticationForm tokenDTO = new JwtAuthenticationForm();
		tokenDTO.setUserName(user.getUsername());
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(tokenDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}
	
	/**
	 * Test token creation passing a invalid password for a registered user.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationInvalidPassword() throws Exception {
		when(userMapper.jwtAuthenticationDtoToUser(ArgumentMatchers.any()))
				.thenReturn(user);
		when(tokenService.createToken(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(new UnauthorizedUserException("error.login.invalid"));

		JwtAuthenticationForm tokenDTO = new JwtAuthenticationForm();
		tokenDTO.setUserName(user.getUsername());
		tokenDTO.setPassword("kkkk");
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(tokenDTO)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message", 
					equalTo("Invalid username or password.")));
	}
	
	/**
	 * Test token creation passing a invalid application.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationInvalidApplication() throws Exception {
		when(userMapper.jwtAuthenticationDtoToUser(ArgumentMatchers.any()))
				.thenReturn(user);

		JwtAuthenticationForm tokenDTO = new JwtAuthenticationForm();
		tokenDTO.setUserName(user.getUsername());
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(null);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(tokenDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].message",
					equalTo("Application cannot be null.")));
	}
	
	/**
	 * Test token creation passing a invalid username for a registered user.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationNullUsername() throws Exception {
		when(userMapper.jwtAuthenticationDtoToUser(ArgumentMatchers.any()))
				.thenReturn(user);

		JwtAuthenticationForm tokenDTO = new JwtAuthenticationForm();
		tokenDTO.setUserName(null);
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(tokenDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].message",
						equalTo("Username must not be blank or null.")));
	}

	/**
	 * Test token creation passing a invalid username for a registered user.
	 *
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationInvalidUsername() throws Exception {
		when(userMapper.jwtAuthenticationDtoToUser(ArgumentMatchers.any()))
				.thenReturn(user);
		when(tokenService.createToken(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(new UnauthorizedUserException("error.login.invalid"));

		JwtAuthenticationForm tokenDTO = new JwtAuthenticationForm();
		tokenDTO.setUserName("kkk");
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(tokenDTO)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message",
						equalTo("Invalid username or password.")));
	}

	/**
	 * Test token creation for a user that are not registered for the application. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationForbiddenApplication() throws Exception {
		user.setRegistries(Arrays.asList(
				new Registry(new Application(ApplicationType.NOTES_APP), user)));

		when(userMapper.jwtAuthenticationDtoToUser(ArgumentMatchers.any()))
				.thenReturn(user);
		when(tokenService.createToken(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(new ForbiddenUserException("error.user.notregistered"));

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userForm)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message", 
					equalTo("User not registered for this application.")));
	}

	/**
	 * Test token creation for a user that are not registered for the application.
	 *
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationForbiddenApplicationPtMessage() throws Exception {
		user.setRegistries(Arrays.asList(
				new Registry(new Application(ApplicationType.NOTES_APP), user)));

		when(userMapper.jwtAuthenticationDtoToUser(ArgumentMatchers.any()))
				.thenReturn(user);
		when(tokenService.createToken(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenThrow(new ForbiddenUserException("error.user.notregistered"));

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
				.contentType(MediaType.APPLICATION_JSON)
				.locale( new Locale("pt"))
				.content(asJsonString(userForm)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message",
						equalTo("Usuário não registrado para esta aplicação.")));
	}

	/**
	 * Test token creation with blank user name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationBlankUserName() throws Exception {
		userForm.setUserName(" ");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0].message",
					isIn(userNameBlankErrors)));
	}
	
	/**
	 * Test token creation with a longer user name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationLongUserName() throws Exception {
		userForm.setUserName("aaaaaaaaaaaaa");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0].message",
					equalTo("Username length must be between 2 and 12.")));
	}
	
	/**
	 * Test token creation with null password.
	 *
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationNullPassword() throws Exception {
		userForm.setPassword(null);
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Password must not be blank or null.")));
	}
	
	/**
	 * Test token creation with blank password.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationBlankPassword() throws Exception {
		userForm.setPassword(" ");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0].message", isIn(passwordBlankErrors)));
	}
	
	/**
	 * Test token creation with a longer password.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationLongPassword() throws Exception {
		userForm.setPassword("ppppppppppppp");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userForm)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0].message", equalTo("Password length must be between 4 and 12.")));
	}

	/**
	 * Test token checking with expired password.
	 *
	 * @throws Exception
	 */
	@Test
	public void testCheckingExpiredToken() throws Exception {
		String token = "Bearer token";
		when(tokenService.checkToken(token)).thenThrow(new TokenException("error.token.invalid"));
		mvc.perform(MockMvcRequestBuilders.get("/token/check")
				.header("Authorization", token))
				.andExpect(status().isUnprocessableEntity());
	}

	/**
	 * Test token checking with expired password.
	 *
	 * @throws Exception
	 */
	@Test
	public void testTokenCheckingValidToken() throws Exception {
		String token = "Bearer token";
		when(tokenService.checkToken(token)).thenReturn(user);
		mvc.perform(MockMvcRequestBuilders.get("/token/check")
				.header("Authorization", token))
				.andExpect(status().isOk());
	}

	public static String asJsonString(final Object obj) throws JsonProcessingException {
	    try {
	        final ObjectMapper mapper = new ObjectMapper();
	        final String jsonContent = mapper.writeValueAsString(obj);
	        return jsonContent;
	    } catch (TokenException e) {
	        throw new RuntimeException(e);
	    }
	}

}
