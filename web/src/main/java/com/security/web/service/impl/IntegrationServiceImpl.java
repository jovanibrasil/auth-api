package com.security.web.service.impl;

import com.security.web.domain.ApplicationType;
import com.security.web.domain.User;
import com.security.web.domain.dto.EmailMessageDTO;
import com.security.web.exception.implementation.MicroServiceIntegrationException;
import com.security.web.service.IntegrationService;
import com.security.web.service.TokenService;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class IntegrationServiceImpl implements IntegrationService {

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

	private final TokenService tokenService;
	private final RestTemplate restTemplate;

	public IntegrationServiceImpl(TokenService tokenService, RestTemplate restTemplate) {
		this.tokenService = tokenService;
		this.restTemplate = restTemplate;
	}

	/**
	 * Creates an user in a specific application service, like notes-app or blog-app.
	 * 
	 * @param user
	 */
	@Override
	public void createServiceUser(User user) {
		log.info("Creating the user in the remote server ...");
		try {
			HttpHeaders headers = new HttpHeaders();
			String finalToken = "Bearer " + tokenService.createToken();
			log.info("Token: {}", finalToken);
			headers.add("Authorization", finalToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			JSONObject request = new JSONObject();
			request.put("userId", user.getId());
			request.put("userName", user.getUsername());
			request.put("email", user.getEmail());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

			String url = user.hasRegistry(ApplicationType.BLOG_APP) ? url = createBlogUser : createNotesUser;

			log.info("Connecting with service ... {}", url);
			// send request and parse result
			ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.CREATED)) {
				log.info("It was not possible to create the user. Status code: " + loginResponse.getStatusCode());
				throw new MicroServiceIntegrationException(
						"It was not possible to create the user. Status code: " + loginResponse.getStatusCode(), null);
			}
			log.info("The user was successfully created by the remote service.");
		} catch (Exception e) {
			log.info("It was not possible to create the user in the remote server. {}", e.getMessage());
			throw new MicroServiceIntegrationException("It was not possible to create the user.", e);
		}
	}

	/**
	 * Deletes an user in a specific application service, like notes-app or blog-app.
	 * 
	 * @param user
	 */
	@Override
	public void deleteServiceUser(User user) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", "Bearer " + tokenService.createToken());
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String url = user.hasRegistry(ApplicationType.BLOG_APP) ? url = deleteBlogUser : deleteNotesUser;
			// send request and parse result
			log.info("Connecting with service ... {}", url);
			ResponseEntity<String> loginResponse = restTemplate.exchange(url + "/" + user.getUsername(),
					HttpMethod.DELETE, entity, String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.OK)) {
				throw new MicroServiceIntegrationException(
						"It was not possible to create the user. Status code: " + loginResponse.getStatusCode(), null);
			}
			log.info("The user was successfully deleted by the remote service.");
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new MicroServiceIntegrationException("It was not possible to delete the user.", e);
		}
	}

	/**
	 * Send an email to the email service.
	 * 
	 * @param emailMessage
	 */
	@Override
	public void sendEmail(EmailMessageDTO emailMessage) {
		log.info("Send remote server ...");
		try {
			HttpHeaders headers = new HttpHeaders();
			String finalToken = "Bearer " + tokenService.createToken();
			headers.add("Authorization", finalToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			JSONObject request = new JSONObject();
			request.put("text", emailMessage.getText());
			request.put("textType", emailMessage.getTextType());
			request.put("to", emailMessage.getTo());
			request.put("from", emailMessage.getFrom());
			request.put("title", emailMessage.getTitle());
			HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);

			log.info("Connecting with service ... {}", emailServerUrl);
			// send request and parse result
			ResponseEntity<String> loginResponse = restTemplate.exchange(emailServerUrl, HttpMethod.POST, entity,
					String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.OK)) {
				log.info("It was not possible to send the email. Status code: " + loginResponse.getStatusCode());
				throw new MicroServiceIntegrationException(
						"It was not possible to send the email. Status code: " + loginResponse.getStatusCode(), null);
			}
			log.info("The email was successfully sent by the remote service.");

		} catch (Exception e) {
			log.info("It was not possible to send the email via remote server.");
			throw new MicroServiceIntegrationException("It was not possible to send the email via remote server.", e);
		}
	}

}
