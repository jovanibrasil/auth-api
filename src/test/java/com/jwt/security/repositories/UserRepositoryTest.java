package com.jwt.security.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.jwt.security.entities.Application;
import com.jwt.security.entities.Registry;
import com.jwt.security.entities.User;
import com.jwt.security.enums.ProfileEnum;
import com.jwt.utils.ApplicationType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	private User user;
	
	@Before
	public void setUp() {
		Application blog = new Application(ApplicationType.BLOG_APP);
		applicationRepository.save(blog);
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDate(new Date());
		user.addApplication(blog);		
		user = userRepository.save(user);
	}
	
	@Test
	public void testFindValidUserByName() {
		User user = this.userRepository.findUserByUserName("test");
		assertNotNull(user);
	}
	
	@Test
	public void testFindValidUserByEmail() {
		User user = this.userRepository.findUserByEmail("test@gmail.com");
		assertNotNull(user);
	}
	
	@Test
	public void testFindInvalidUserByName() {
		User user = this.userRepository.findUserByUserName("test@gmail.com");
		assertNull(user);
	}
	
	@Test
	public void testFindInvalidUserByEmail() {
		User user = this.userRepository.findUserByEmail("test");
		assertNull(user);
	}

	@Test
	public void testDeleteUser() {
		User user = userRepository.findUserByUserName("test");
		userRepository.delete(user);
		Optional<User> u = userRepository.findById(user.getId());
		assertEquals(false, u.isPresent());
	}
	
	@Test
	public void testSaveUser() {
		Application blog = new Application(ApplicationType.BLOG_APP);
		blog = applicationRepository.save(blog);
		User u = new User();
		u.setEmail("test@gmail.com");
		u.setUserName("test");
		u.setPassword("password");
		u.setProfile(ProfileEnum.ROLE_USER);
		u.setSignUpDate(new Date());
		u.addApplication(blog);		
		u = userRepository.save(u);
		assertNotNull(u.getId());
	}

	@Test
	public void testFindValidUserByNameVerifyApplications() {
		user = this.userRepository.findUserByUserName("test");
		Application app = user.getRegistries().get(0).getApplication();
		assertEquals(ApplicationType.BLOG_APP, app.getApplication());
	}
	
	@Test
	public void testHasValidApplication() {
		user = this.userRepository.findUserByEmail("test@gmail.com");
		assertNotNull(user.getRegistries());
		assertNotEquals(0, user.getRegistries().size());
		Registry registry = user.getRegistries().get(0);
		if(registry != null) {
			assertNotNull(registry.getApplication());
			assertNotNull(registry.getApplication().getId());
			assertNotNull(registry.getUser());
		}		
	}

}
