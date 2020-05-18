package com.security.web.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.security.web.ScenariosFactory;
import com.security.web.domain.Application;
import com.security.web.domain.Registry;
import com.security.web.domain.User;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApplicationRepository applicationRepository;
	
	private User user;
	private Application blogApplication;
	private Application notesApplication;
	
	@Before
	public void setUp() {
		blogApplication = applicationRepository.save(ScenariosFactory.createBlogApplication());
		notesApplication = applicationRepository.save(ScenariosFactory.createNotesApplication());
		user = ScenariosFactory.createUser();
	}
	
	@Test
	public void testFindValidUserByName() {
		User savedUser = userRepository.save(user);
		assertEquals(savedUser.getUsername(), userRepository.findByUsername("test").get().getUsername());
	}
	
	@Test
	public void testFindValidUserByEmail() {
		User savedUser = userRepository.save(user);
		assertEquals(savedUser.getEmail(), userRepository.findByEmail("test@gmail.com").get().getEmail());
	}
	
	@Test
	public void testFindInvalidUserByName() {
		assertFalse(userRepository.findByUsername("test@gmail.com").isPresent());
	}
	
	@Test
	public void testFindInvalidUserByEmail() {
		assertFalse(userRepository.findByEmail("test").isPresent());
	}

	/**
	 * Delete a valid user.
	 * 
	 */
	@Test
	public void testDeleteUser() {
		user.addApplication(blogApplication);
		User savedUser = userRepository.save(user);
		userRepository.delete(savedUser);
		Optional<User> optUser = userRepository.findById(savedUser.getId());
		
		assertFalse(optUser.isPresent());
		assertTrue(applicationRepository.findById(blogApplication.getId()).isPresent());
	}
	
	/**
	 * Save a blog user.
	 * 
	 */
	@Test
	public void testSaveUserBlogApplication() {
		user.addApplication(blogApplication);
		user = userRepository.save(user);
		
		assertNotNull(user.getId());
		assertEquals(1, user.getRegistries().size());
		assertEquals(blogApplication.getType(), user.getRegistries().get(0).getApplication().getType());
	}

	/**
	 * Save a user without application.
	 * 
	 */
	@Test
	public void testSaveUserWithoutApplication() {
		user = userRepository.save(user);
		
		assertNotNull(user.getId());
		assertEquals(0, user.getRegistries().size());
	}
	
	/**
	 * Add application to an existent registered user.
	 * 
	 */
	@Test
	public void testUpdateAddUserApplication() {
		user.addApplication(blogApplication);
		user = userRepository.save(user);
		
		user.addApplication(notesApplication);
		
		User savedUser = userRepository.findById(user.getId()).get();
		assertEquals(2, savedUser.getRegistries().size());
	}
	
	/**
	 * Delete application blog registry from an existent registered user.
	 * 
	 */
	@Test
	public void testDeleteApplicationRegistryFromExistentUser() {
		user.addApplication(blogApplication);
		user.addApplication(notesApplication);
		user = userRepository.save(user);
		
		User savedUser = userRepository.findById(user.getId()).get();
		Registry registry = savedUser.getRegistries().get(0);
		savedUser.getRegistries().remove(0);
		userRepository.save(user);
		
		savedUser = userRepository.findById(user.getId()).get();
		assertEquals(1, savedUser.getRegistries().size());
		// the application is not affected 
		assertTrue(applicationRepository.findById(registry.getApplication().getId()).isPresent());
	}
	
}
