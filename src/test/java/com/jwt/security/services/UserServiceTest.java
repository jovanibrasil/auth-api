package com.jwt.security.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.security.jwt.entities.Application;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.exceptions.UserServiceException;
import com.security.jwt.integration.Integration;
import com.security.jwt.repositories.ApplicationRepository;
import com.security.jwt.repositories.UserRepository;
import com.security.jwt.services.UserService;
import com.security.jwt.services.impl.UserServiceImpl;
import com.security.jwt.utils.ApplicationType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
	
	@Mock
	UserRepository userRepository;
	
	@Autowired
	ApplicationRepository applicationRepository;
	
	@InjectMocks
	UserService userService = new UserServiceImpl();
	
	@Spy
	Integration integrationService;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDate(new Date());
		
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
		user.setSignUpDate(new Date());
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
		user.setSignUpDate(new Date());
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
		user.setSignUpDate(new Date());
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
