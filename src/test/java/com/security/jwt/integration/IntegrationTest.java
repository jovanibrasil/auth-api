package com.security.jwt.integration;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.security.jwt.exceptions.handlers.RestTemplateResponseErrorHandler;
import com.security.jwt.exceptions.implementations.MicroServiceIntegrationException;

@RunWith(SpringRunner.class)
@RestClientTest
public class IntegrationTest {

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private RestTemplateBuilder builder;
	
	@Value("${urls.blog.createuser}")
	private String createBlogUser;

	@Value("${urls.notes.createuser}")
	private String createNotesUser;

	@Value("${urls.blog.deleteuser}")
	private String deleteBlogUser;

	@Value("${urls.notes.deleteuser}")
	private String deleteNotesUser;

	@Value("${urls.email.server.url}")
	private String emailServerUrl;

	/**
	 * Tests creation of an invalid user (invalid email) in the notes-app.
	 * 
	 */
	@Test(expected = MicroServiceIntegrationException.class)
	public void creatingNotesInavlidUser() {
		assertNotNull(this.builder);
		assertNotNull(this.server);

		RestTemplate restTemplate = this.builder.errorHandler(new RestTemplateResponseErrorHandler()).build();

		JSONObject request = new JSONObject();
		try {
			request.put("userId", "1");
			request.put("userName", "jovani");
			request.put("email", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpEntity<String> entity = new HttpEntity<String>(request.toString());
		
		this.server.expect(ExpectedCount.once(), requestTo(createNotesUser))
			.andExpect(method(HttpMethod.POST))
			.andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));
		restTemplate.exchange(createNotesUser, HttpMethod.POST, entity, RequestEntity.class);
		this.server.verify();
		
	}
	
	

}
