package com.security.web.services;

import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.AuthenticationMock;
import com.security.web.domain.Application;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;
import com.security.web.exceptions.implementations.NotFoundException;
import com.security.web.exceptions.implementations.ValidationException;
import com.security.web.repositories.ApplicationRepository;
import com.security.web.repositories.UserRepository;
import com.security.web.services.impl.IntegrationServiceImpl;
import com.security.web.services.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTest {
	
	@Mock
    private UserRepository userRepository;

	@Mock
    private IntegrationServiceImpl integrationService;

	@Mock
    private ApplicationRepository applicationRepository;

	@InjectMocks
    private UserServiceImpl userService;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Mock
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtTokenGenerator jwtTokenUtil;

	private User user;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		userService = new UserServiceImpl(userRepository,
				integrationService, authenticationManager, jwtTokenUtil);
		user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());

		BDDMockito.given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(user));
		BDDMockito.given(userRepository.findByEmail("test2@gmail.com")).willReturn(null);
		BDDMockito.given(userRepository.findByUserName("test")).willReturn(Optional.of(user));
		BDDMockito.given(userRepository.findByUserName("test2")).willReturn(Optional.empty());
		BDDMockito.given(userRepository.findUserById(1L)).willReturn(Optional.of(user));
		
		Mockito.doNothing().when(integrationService).createServiceUser(Mockito.any());
		Mockito.doNothing().when(integrationService).deleteServiceUser(Mockito.any());

		AuthenticationMock auth = new AuthenticationMock();
		when(authenticationManager.authenticate(any())).thenReturn(auth);
	}
	
	@Test
	public void testFindValidUserByUserName() {
		User savedUser = userService.findByUserName("test");
		assertNotNull(savedUser);
	}
	
	@Test(expected = NotFoundException.class)
	public void testFindInvalidUserByUserName() {
		userService.findByUserName("invalidUser");
	}

	@Test
	public void testFindValidUserByEmail() {
		User savedUser = userService.findUserByEmail("test@gmail.com");
		assertNotNull(savedUser);
	}
	
	@Test(expected = NotFoundException.class)
	public void testFindInvalidUserByEmail() {
		userService.findUserByEmail("test");
	}
	
	@Test
	public void testSave() {
		// Save application
		Application application = new Application(ApplicationType.BLOG_APP);
		applicationRepository.save(application);
		
		// Save user
		User newUser = new User();
		newUser.setId(0L);
		newUser.setEmail("tes2t@gmail.com");
		newUser.setUserName("test2");
		newUser.setPassword("password");
		newUser.setProfile(ProfileEnum.ROLE_USER);
		newUser.setSignUpDateTime(LocalDateTime.now());
		newUser.addApplication(application);

		when(userRepository.save(newUser)).thenReturn(newUser);
		newUser = userService.saveUser(newUser);
		
		assertNotNull(newUser.getId());
	}
	
	@Test
	public void testSaveRepeatedEmail() {
		when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
		expectedEx.expect(ValidationException.class);
		expectedEx.expectMessage("[error.email.alreadyexists]");
		User newUser = new User();
		newUser.setEmail("test@gmail.com");
		newUser.setUserName("test2");
		newUser.setPassword("password");
		newUser.setProfile(ProfileEnum.ROLE_USER);
		newUser.setSignUpDateTime(LocalDateTime.now());
		newUser.setRegistries(Arrays.asList());
		this.userService.saveUser(newUser);
	}
	
	@Test
	public void testSaveRepeatedUserName() {
		when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
		when(userRepository.findByUserName(any())).thenReturn(Optional.of(user));
		expectedEx.expect(ValidationException.class);
		expectedEx.expectMessage("[error.username.alreadyexists]");
		User newUser = new User();
		newUser.setEmail("test2@gmail.com");
		newUser.setUserName("test");
		newUser.setPassword("password");
		newUser.setProfile(ProfileEnum.ROLE_USER);
		newUser.setSignUpDateTime(LocalDateTime.now());
		newUser.setRegistries(Arrays.asList());
		this.userService.saveUser(newUser);
	}

	@Test
	public void testInvalidUpdateUserName() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

		User newUser = new User();
		newUser.setId(1L);
		newUser.setEmail("test2@gmail.com");
		newUser.setUserName("test");
		newUser.setPassword("password");

		expectedEx.expect(ValidationException.class);
		expectedEx.expectMessage("[error.username.alreadyexists]");

		this.userService.saveUser(newUser);
	}

	@Test
	public void testInvalidUpdateEmail() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

		expectedEx.expect(ValidationException.class);
		expectedEx.expectMessage("[error.email.alreadyexists]");
		User newUser = new User();
		newUser.setId(1L);
		newUser.setEmail("test@gmail.com");
		newUser.setUserName("test2");
		newUser.setPassword("password");
		this.userService.saveUser(newUser);
	}

	@Ignore
	@Test
	public void testValidUpdateUserName() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

		User newUser = new User();
		newUser.setId(1L);
		newUser.setEmail("test@gmail.com");
		newUser.setUserName("test2");
		newUser.setPassword("password");
		User updatedUser = this.userService.updateUser(newUser);
		assertEquals("test2", updatedUser.getUserName());
	}

	@Test
	public void testValidUpdateEmail() {

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

		User newUser = new User();
		newUser.setId(1L);
		newUser.setEmail("test2@gmail.com");
		newUser.setUserName("test");
		newUser.setPassword("password");

		User updatedUser = this.userService.updateUser(newUser);
		assertEquals("test2@gmail.com", updatedUser.getEmail());
	}
	
	@Test
	public void testDeleteUser() {
		try {
			this.userService.deleteUserByName("test");
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test(expected = NotFoundException.class)
	public void testDeleteInvalidUser() {
		//The user does not exist.
		userService.deleteUserByName("java");
	}

}
