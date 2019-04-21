package com.jwt.security.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jwt.security.entities.User;
import com.jwt.security.enums.ProfileEnum;
import com.jwt.security.services.impl.UserServiceException;
import com.jwt.utils.ApplicationType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;
	
	User user;
	
	@Before
	public void setUp() {
		user = new User();
		user.setEmail("test@gmail.com");
		user.setUserName("test");
		user.setPassword("password");
		user.setProfile(ProfileEnum.ROLE_USER);
		user.setSignUpDate(new Date());
		user.setMyApplications(Arrays.asList(ApplicationType.BLOG_APP));
		this.userRepository.save(user);	
	}
	
	@After
	public void tearDown() {
		this.userRepository.deleteAll();
	}
	
	@Test
	public void testFindValidUserByName() {
		User user = this.userRepository.findUserByUserName("test");
		assertNotNull(user);
	}
	
	@Test
	public void testFindValidUserByNameVerifyApplications() {
		User user = this.userRepository.findUserByUserName("test");
		assertEquals(ApplicationType.BLOG_APP, user.getMyApplications().get(0));
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
		this.userRepository.delete(this.user);
		User user = this.userRepository.findUserByEmail("test");
		assertNull(user);
	}
	
}
