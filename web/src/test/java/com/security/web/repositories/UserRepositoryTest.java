package com.security.web.repositories;

import com.security.jwt.enums.ProfileEnum;
import com.security.web.domain.Application;
import com.security.web.domain.ApplicationType;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	private Optional<User> optUser;
	
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
		optUser = userRepository.findByUserName("test");
		assertTrue(optUser.isPresent());
	}
	
	@Test
	public void testFindValidUserByEmail() {
		optUser = userRepository.findByEmail("test@gmail.com");
		assertTrue(optUser.isPresent());
	}
	
	@Test
	public void testFindInvalidUserByName() {
		optUser = userRepository.findByUserName("test@gmail.com");
		assertFalse(optUser.isPresent());
	}
	
	@Test
	public void testFindInvalidUserByEmail() {
		optUser = userRepository.findByEmail("test");
		assertFalse(optUser.isPresent());
	}

	@Test
	public void testDeleteUser() {
		optUser = userRepository.findByUserName("test");
		userRepository.delete(optUser.get());
		Optional<User> u = userRepository.findById(optUser.get().getId());
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
		optUser = userRepository.findByUserName("test");
		Application app = optUser.get().getRegistries().get(0).getApplication();
		assertEquals(ApplicationType.BLOG_APP, app.getApplication());
	}
	
	@Test
	public void testHasSpecificApplication() {
		optUser = userRepository.findByUserName("test");
		assertTrue(optUser.get().hasRegistry(ApplicationType.BLOG_APP));
	}
	
	@Test
	public void testHasValidApplications() {
		optUser = userRepository.findByEmail("test@gmail.com");
		assertNotNull(optUser.get().getRegistries());
		assertNotEquals(0, optUser.get().getRegistries().size());
		Registry registry = optUser.get().getRegistries().get(0);
		if(registry != null) {
			assertNotNull(registry.getApplication());
			assertNotNull(registry.getApplication().getId());
			assertNotNull(registry.getUser());
		}		
	}

}
