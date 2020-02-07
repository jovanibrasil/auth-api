package com.security.web.services;

import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;
import com.security.jwt.enums.ProfileEnum;
import com.security.web.exceptions.implementations.UserServiceException;
import com.security.web.services.impl.IntegrationServiceImpl;
import com.security.web.repositories.ApplicationRepository;
import com.security.web.repositories.UserRepository;
import com.security.web.services.impl.UserServiceImpl;
import com.security.web.domain.Application;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

@ActiveProfiles("test")
public class UserServiceTest {
	
	@Mock
    UserRepository userRepository;

	@Mock
    IntegrationServiceImpl integrationService;

	@Mock
    ApplicationRepository applicationRepository;
	
	@InjectMocks
    UserServiceImpl userService;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.userService = new UserServiceImpl(userRepository, integrationService);

		User user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());

		BDDMockito.given(userRepository.findUserByEmail("test@gmail.com")).willReturn(user);
		BDDMockito.given(userRepository.findUserByEmail("test2@gmail.com")).willReturn(null);
		BDDMockito.given(userRepository.findUserByUserName("test")).willReturn(user);
		BDDMockito.given(userRepository.findUserByUserName("test2")).willReturn(null);
		BDDMockito.given(userRepository.findUserById(1L)).willReturn(user);
		
		Mockito.doNothing().when(integrationService).createServiceUser(Mockito.any());
		Mockito.doNothing().when(integrationService).deleteServiceUser(Mockito.any());
	}
	
	@Test
	public void testFindValidUserByUserName() {
		Optional<User> optional = this.userService.findByUserName("test");
		assertEquals(true, optional.isPresent());
	}
	
	@Test
	public void testFindInvalidUserByUserName() {
		Optional<User> optional = this.userService.findByUserName("invalidUser");
		assertEquals(false, optional.isPresent());
	}

	@Test
	public void testFindValidUserByEmail() {
		Optional<User> optional = this.userService.findUserByEmail("test@gmail.com");
		assertEquals(true, optional.isPresent());
	}
	
	@Test
	public void testFindInvalidUserByEmail() {
		Optional<User> optional = this.userService.findUserByEmail("test");
		assertEquals(false, optional.isPresent());
	}
	
	@Test
	public void testSave() throws UserServiceException {
		
		// Save application
		Application application = new Application(ApplicationType.BLOG_APP);
		applicationRepository.save(application);
		
		// Save user
		User user = new User();
		user.setId(0L);
		user.setEmail("tes2t@gmail.com");
		user.setUserName("test2");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());
		user.addApplication(application);
		this.userService.save(user);
		
		assertNotNull(user.getId());
	}
	
	@Test
	public void testSaveRepeatedEmail() throws UserServiceException {
		expectedEx.expect(UserServiceException.class);
		expectedEx.expectMessage("This email already exists.");
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setUserName("test2");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());
		user.setRegistries(Arrays.asList());
		this.userService.save(user);
	}
	
	@Test
	public void testSaveRepeatedUserName() throws UserServiceException {
		expectedEx.expect(UserServiceException.class);
		expectedEx.expectMessage("This username already exists.");
		User user = new User();
		user.setEmail("test2@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDateTime(LocalDateTime.now());
		user.setRegistries(Arrays.asList());
		this.userService.save(user);
	}

	@Test
	public void testInvalidUpdateUserName() throws UserServiceException {
		expectedEx.expect(UserServiceException.class);
		expectedEx.expectMessage("This username already exists.");
		User user = new User();
		user.setId(1L);
		user.setEmail("test2@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		this.userService.save(user);
	}

	@Test
	public void testInvalidUpdateEmail() throws UserServiceException {
		expectedEx.expect(UserServiceException.class);
		expectedEx.expectMessage("This email already exists.");
		User user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");
		user.setUserName("test2");
		user.setPassword("password");
		this.userService.save(user);
	}
	
	@Test
	public void testValidUpdateUserName() throws UserServiceException {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");
		user.setUserName("test2");
		user.setPassword("password");
		User updatedUser = this.userService.updateUser(user);
		assertEquals("test2", updatedUser.getUserName());
	}

	@Test
	public void testValidUpdateEmail() throws UserServiceException {
		User user = new User();
		user.setId(1L);
		user.setEmail("test2@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		User updatedUser = this.userService.updateUser(user);
		assertEquals("test2@gmail.com", updatedUser.getEmail());
	}
	
	@Test
	public void testDeleteUser() throws UserServiceException {
		try {
			this.userService.deleteUser("test");
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testDeleteInvalidUser() {
		try {
			this.userService.deleteUser("java");
			assertTrue(false);
		} catch (Exception e) {
			if(e.getMessage().equals("The user does not exist.")) {	
				assertTrue(true);
			}else {
				assertTrue(false);	
			}
		}
	}

}
