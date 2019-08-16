package com.jwt.integration;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jwt.exceptions.MicroServiceIntegrationException;
import com.jwt.security.entities.User;
import com.jwt.utils.ApplicationType;

@Service
public class Integration {

	@Value("${urls.blog.createuser}")
	private String createBlogUser;

	@Value("${urls.notes.createuser}")
	private String createNotesUser;

	@Value("${urls.blog.deleteuser}")
	private String deleteBlogUser;

	@Value("${urls.notes.deleteuser}")
	private String deleteNotesUser;

	public void createServiceUser(User user) {
		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			JSONObject request = new JSONObject();
			request.put("userId", user.getId());
			request.put("userName", user.getUserName());
			request.put("email", user.getEmail());
			// headers.set("Authorization", token);
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

			String url = createNotesUser;
			if (user.hasRegistry(ApplicationType.BLOG_APP)) {
				url = createBlogUser;
			}

			// send request and parse result
			ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.OK)) {
				throw new MicroServiceIntegrationException(
						"It was not posssible to create the user. Status code: " + loginResponse.getStatusCode(), null);
			}

		} catch (Exception e) {
			throw new MicroServiceIntegrationException("It was not posssible to create the user.", e);
		}
	}

	public void deleteServiceUser(User user) {
		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(headers);

			String url = deleteNotesUser;
			if (user.hasRegistry(ApplicationType.BLOG_APP)) {
				url = deleteBlogUser;
			}

			// send request and parse result
			ResponseEntity<String> loginResponse = restTemplate.exchange(url + "/" + user.getUserName(), HttpMethod.DELETE, entity, String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.OK)) {
				throw new MicroServiceIntegrationException(
						"It was not posssible to create the user. Status code: " + loginResponse.getStatusCode(), null);
			}

		} catch (Exception e) {
			throw new MicroServiceIntegrationException("It was not posssible to delete the user.", e);
		}
	}

}
