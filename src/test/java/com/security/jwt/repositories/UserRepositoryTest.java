package com.security.jwt.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.security.jwt.entities.Application;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.repositories.ApplicationRepository;
import com.security.jwt.repositories.UserRepository;
import com.security.jwt.utils.ApplicationType;

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
		user.setSignUpDateTime(LocalDateTime.now());
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
		u.setSignUpDateTime(LocalDateTime.now());
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
	public void testHasSpecificApplication() {
		user = this.userRepository.findUserByUserName("test");
		assertTrue(user.hasRegistry(ApplicationType.BLOG_APP));	
	}
	
	@Test
	public void testHasValidApplications() {
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
