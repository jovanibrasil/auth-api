package com.jwt.security.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.dto.UserDto;
import com.security.jwt.entities.Application;
import com.security.jwt.entities.Registry;
import com.security.jwt.entities.User;
import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.exceptions.UserServiceException;
import com.security.jwt.integration.Integration;
import com.security.jwt.services.UserService;
import com.security.jwt.utils.ApplicationType;
import com.security.validators.UserEmailValidator;
import com.security.validators.UserEmailValidatorImpl;
import com.security.validators.UserNameValidatorImpl;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UserService userService;
	
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
	
	@Test
	public void testCreateUser() throws Exception {
		BDDMockito.given(userService.findByUserName(Mockito.any()))
		.willReturn(Optional.empty());
		BDDMockito.given(this.userService.save(Mockito.any())).willReturn(user);
		
		Integration integration = mock(Integration.class);
		BDDMockito.doNothing().when(integration)
			.sendEmail(BDDMockito.any());
		
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	public void testCreateUserUserNameAlreadyExists() throws Exception {
		BDDMockito.given(userService.findByUserName(Mockito.any()))
		.willReturn(Optional.of(user));
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message", equalTo("This user name already exists.")));
	}
	
	@Test
	public void testCreateUserEmailAlreadyExists() throws Exception {
		BDDMockito.given(userService.findUserByEmail(Mockito.any()))
			.willReturn(Optional.of(user));
			
		mvc.perform(MockMvcRequestBuilders.post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.errors[0].errors[0].message", equalTo("This email already exists.")));
	}
	
	@Test
	public void testUpdateUser() throws Exception {
		User updatedUser = new User();
		updatedUser.setId(1L);
		updatedUser.setEmail("newtest@gmail.com");
		updatedUser.setUserName("newtest");
		updatedUser.setPassword("password");
		user.setRegistries(Arrays.asList(
				new Registry(new Application(ApplicationType.BLOG_APP), user)));
		userDto.setEmail("newtest@gmail.com");
		userDto.setUserName("newtest");
		
		BDDMockito.given(this.userService.updateUser(Mockito.any())).willReturn(updatedUser);
		
		mvc.perform(MockMvcRequestBuilders.put("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(userDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.data.userName", equalTo("newtest")))
			.andExpect(jsonPath("$.data.email", equalTo("newtest@gmail.com")));
	}
		
	@Test
	public void testDeleteUser() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/users/test")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	public void testDeleteInvalidUser() throws Exception {
		doThrow(new UserServiceException("The user does not exist.")).when(this.userService).deleteUser(Mockito.anyString());
		mvc.perform(MockMvcRequestBuilders.delete("/users/java")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", equalTo("The user does not exist.")));
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

}
