package com.security.jwt.controllers;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.dto.JwtAuthenticationDTO;
import com.security.jwt.dto.CreateUserDTO;
import com.security.jwt.entities.Application;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.repositories.UserRepository;
import com.security.jwt.security.utils.JwtTokenUtil;
import com.security.jwt.services.UserService;
import com.security.jwt.utils.ApplicationType;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TokenControllerTest {

	@Mock
	private UserRepository userRepository;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	UserService userService;
	
	@MockBean
	private JwtTokenUtil jwtTokenUtil;
	
	@MockBean
	private AuthenticationManager authenticationManager;
	
	@MockBean
	private UserDetailsService userDetailsService;
	
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
		user.setSignUpDate(new Date());
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
		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName(user.getUserName());
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);
		BDDMockito.given(this.userService.findByUserName("test")).willReturn(Optional.of(user));
		BDDMockito.given(this.authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("test", "password")))
		.willReturn(new AuthenticationMock());
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
		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName(user.getUserName());
		tokenDTO.setPassword("kkkk");
		tokenDTO.setApplication(ApplicationType.BLOG_APP);
		BDDMockito.given(this.userService.findByUserName(user.getUserName())).willReturn(Optional.of(user));
		BDDMockito.given(this.authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(tokenDTO.getUserName(), tokenDTO.getPassword())))
		.willReturn(new AuthenticationMock(false));
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
		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName(user.getUserName());
		tokenDTO.setPassword("kkkk");
		tokenDTO.setApplication(null);
		BDDMockito.given(this.userService.findByUserName(user.getUserName())).willReturn(Optional.of(user));
		BDDMockito.given(this.authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(tokenDTO.getUserName(), tokenDTO.getPassword())))
		.willReturn(new AuthenticationMock());
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
	public void testTokenCreationInvalidUsername() throws Exception {
		JwtAuthenticationDTO tokenDTO = new JwtAuthenticationDTO();
		tokenDTO.setUserName("kkk");
		tokenDTO.setPassword(user.getPassword());
		tokenDTO.setApplication(ApplicationType.BLOG_APP);
		BDDMockito.given(this.userService.findByUserName(tokenDTO.getUserName())).willReturn(Optional.empty());
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
		BDDMockito.given(this.userService.findByUserName("test")).willReturn(Optional.of(user));
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.errors[0].message", 
					equalTo("User not registered for this application.")));
	}
	
	/**
	 * Test token creation with null user name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTokenCreationNullUserName() throws Exception {
		userDto.setUserName(null);
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message", equalTo("Username must not be blank or null.")));
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
			.andExpect(jsonPath("$.errors[0].errors[0].message", isIn(userNameBlankErrors)));
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
			.andExpect(jsonPath("$.errors[0].errors[0].message", equalTo("Username length must be between 2 and 12.")));
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
	
	public class AuthenticationMock implements Authentication {

		private static final long serialVersionUID = 6373211614082998724L;

		private boolean isAuthenticated;
		
		public AuthenticationMock() {
			this.isAuthenticated = true;
		}
		
		public AuthenticationMock(boolean isAuthenticated) {
			this.isAuthenticated = isAuthenticated;
		}
		
		@Override
		public String getName() { return null; }

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() { return null; }

		@Override
		public Object getCredentials() { return null; }

		@Override
		public Object getDetails() { return null; }

		@Override
		public Object getPrincipal() { return null; }

		@Override
		public boolean isAuthenticated() { return isAuthenticated; }

		@Override
		public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
	}


}
