package com.security.jwt.integration;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.security.jwt.entities.User;
import com.security.jwt.exceptions.implementations.MicroServiceIntegrationException;
import com.security.jwt.utils.ApplicationType;

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
	
	@Value("${urls.email.server.url}")
	private String emailServerUrl;

	@Autowired
	private Token token;
	
	private final Logger log = LoggerFactory.getLogger(Integration.class);
	
	public void createServiceUser(User user) {
		log.info("Creating the user in the remote server ...");
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			String finalToken = "Bearer " + token.getToken();
			headers.add("Authorization", finalToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			JSONObject request = new JSONObject();
			request.put("userId", user.getId());
			request.put("userName", user.getUserName());
			request.put("email", user.getEmail());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

			String url = user.hasRegistry(ApplicationType.BLOG_APP) ? url = createBlogUser : createNotesUser;
			
			log.info("Connecting with service ... {}", url);
			// send request and parse result
			ResponseEntity<String> loginResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.CREATED)) {
				log.info("A resposta do servidor foi um valor inesperado.");
				//log.info("It was not possible to create the user. Service integration error");
				throw new MicroServiceIntegrationException(
						"It was not posssible to create the user. Status code: " + loginResponse.getStatusCode(), null);
			}
			log.info("The user was successfully created by the remote service.");
		} catch (Exception e) {
			log.info("It was not posssible to create the user in the remote server.");
			throw new MicroServiceIntegrationException("It was not posssible to create the user.", e);
		}
	}

	public void deleteServiceUser(User user) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", "Bearer " + token.getToken());
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			String url = user.hasRegistry(ApplicationType.BLOG_APP) ? url = deleteBlogUser : deleteNotesUser;
			// send request and parse result
			ResponseEntity<String> loginResponse = restTemplate.exchange(url + "/" + user.getUserName(), HttpMethod.DELETE, entity, String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.OK)) {
				throw new MicroServiceIntegrationException(
						"It was not posssible to create the user. Status code: " + loginResponse.getStatusCode(), null);
			}
			log.info("The user was successfully deleted by the remote service.");
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new MicroServiceIntegrationException("It was not posssible to delete the user.", e);
		}
	}
	
	public void sendEmail(EmailMessage emailMessage) {
		log.info("Send remote server ...");
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			String finalToken = "Bearer " + token.getToken();
			headers.add("Authorization", finalToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			JSONObject request = new JSONObject();
			request.put("text", emailMessage.getText());
			request.put("textType", emailMessage.getTextType());
			request.put("to", emailMessage.getTo());
			request.put("from", emailMessage.getFrom());
			request.put("title", emailMessage.getTitle());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

			log.info("Connecting with service ... {}", emailServerUrl);
			// send request and parse result
			ResponseEntity<String> loginResponse = restTemplate.exchange(emailServerUrl, 
					HttpMethod.POST, entity, String.class);

			if (!loginResponse.getStatusCode().equals(HttpStatus.OK)) {
				log.info("A resposta do servidor foi um valor inesperado.");
				//log.info("It was not possible to create the user. Service integration error");
				throw new MicroServiceIntegrationException(
						"It was not possible to send the email. Status code: " + loginResponse.getStatusCode(), null);
			}
			log.info("The email was successfully sended by the remote service.");
		} catch (Exception e) {
			log.info("It was not posssible to send the email via remote server.");
			throw new MicroServiceIntegrationException("It was not posssible to send the email via remote server.", e);
		}
	}

}
