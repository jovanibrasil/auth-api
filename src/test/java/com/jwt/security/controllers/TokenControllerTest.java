package com.jwt.security.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
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
import com.jwt.integration.Integration;
import com.jwt.security.dto.UserDto;
import com.jwt.security.entities.Application;
import com.jwt.security.entities.Registry;
import com.jwt.security.entities.User;
import com.jwt.security.enums.ProfileEnum;
import com.jwt.security.repositories.UserRepository;
import com.jwt.security.services.UserService;
import com.jwt.security.services.impl.UserServiceImpl;
import com.jwt.security.utils.JwtTokenUtil;
import com.jwt.utils.ApplicationType;

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
	
	
	private User user;
	private UserDto userDto;
	
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
		userDto = new UserDto();
		userDto.setId(1L);
		userDto.setEmail("test@gmail.com");
		userDto.setUserName("test");
		userDto.setPassword("password");
		userDto.setApplication(ApplicationType.BLOG_APP);
		
	}
	
	@After
	public void tearDown() {
		this.userRepository.deleteAll();
	}
	
	@Test
	public void testTokenCreationForbiddenApplication() throws Exception {
		user.setRegistries(Arrays.asList(
				new Registry(new Application(ApplicationType.NOTES_APP), user)));
		BDDMockito.given(this.userService.findByUserName("test")).willReturn(Optional.of(user));
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().is(403))
			.andExpect(jsonPath("$.errors[0]", equalTo("Authentication error. User not registered for this application.")));
	}
	
	@Test
	public void testTokenCreation() throws Exception {
		
		BDDMockito.given(this.userService.findByUserName("test")).willReturn(Optional.of(user));
		BDDMockito.given(this.authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken("test", "password")))
		.willReturn(new AuthenticationMock());
		mvc.perform(MockMvcRequestBuilders.post("/token/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errors").isEmpty());
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

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getCredentials() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getDetails() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getPrincipal() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isAuthenticated() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			// TODO Auto-generated method stub
		}
	}


}
