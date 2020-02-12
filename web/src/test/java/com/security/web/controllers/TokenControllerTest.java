package com.security.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.domain.Application;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import com.security.web.dto.CreateUserDTO;
import com.security.web.dto.JwtAuthenticationDTO;
import com.security.web.exceptions.implementations.ForbiddenUserException;
import com.security.web.exceptions.implementations.UnauthorizedUserException;
import com.security.web.mappers.UserMapper;
import com.security.web.repositories.UserRepository;
import com.security.web.services.TokenService;
import com.security.web.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TokenControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@MockBean
	private JwtTokenGenerator jwtTokenUtil;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private TokenService tokenService;

	@MockBean
	private UserMapper userMapper;

	private List<String> passwordBlankErrors = Arrays.asList("Password length must be between 4 and 12.", 
			"Password must not be blank or null.");
	
	private List<String> userNameBlankErrors = Arrays.asList("Username length must be between 2 and 12.", 
			"Username must not be blank or null.");
	
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
		userDto.setEmail("test@gmail.com");
		userDto.setUserName("test");
		userDto.setPassword("password");
		userDto.setApplication(ApplicationType.BLOG_APP);
		
	}
	
	@After
	public void tearDown() {
		this.userRepository.deleteAll();
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

		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName(user.getUserName());
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(tokenDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errors").isEmpty());
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

		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName(user.getUserName());
		tokenDTO.setPassword("kkkk");
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(tokenDTO)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.errors[0].message", 
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

		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName(user.getUserName());
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(null);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(tokenDTO)))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.errors[0].errors[0].message",
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

		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName(null);
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(tokenDTO)))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.errors[0].errors[0].message",
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

		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName("kkk");
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);

		mvc.perform(MockMvcRequestBuilders.post("/token/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(tokenDTO)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.errors[0].message",
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
			.content(asJsonString(userDto)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.errors[0].message", 
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
				.content(asJsonString(userDto)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.errors[0].message",
						equalTo("Usuário não registrado para esta aplicação.")));
	}

	/**
	 * Test token creation with blank user name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationBlankUserName() throws Exception {
		userDto.setUserName(" ");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message",
					isIn(userNameBlankErrors)));
	}
	
	/**
	 * Test token creation with a longer user name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationLongUserName() throws Exception {
		userDto.setUserName("aaaaaaaaaaaaa");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message",
					equalTo("Username length must be between 2 and 12.")));
	}
	
	/**
	 * Test token creation with null password.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationNullPassword() throws Exception {
		userDto.setPassword(null);
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message", equalTo("Password must not be blank or null.")));
	}
	
	/**
	 * Test token creation with blank password.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationBlankPassword() throws Exception {
		userDto.setPassword(" ");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message", isIn(passwordBlankErrors)));
	}
	
	/**
	 * Test token creation with a longer password.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationLongPassword() throws Exception {
		userDto.setPassword("ppppppppppppp");
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message", equalTo("Password length must be between 4 and 12.")));
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
