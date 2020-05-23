package com.security.web.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.security.web.exception.implementation.MicroServiceIntegrationException;

import java.io.IOException;

@Slf4j
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || 
				response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			// handle SERVER_ERROR
			log.info("Server error. Code {}. Message: {}", response.getStatusCode(), response.getStatusText());
			
		} else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
			// handle CLIENT_ERROR
			log.info("Client error. Code {}. Message: {}", response.getStatusCode(), response.getStatusText());
			if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
				//throw new NotFoundException();
			}
		}
		throw new MicroServiceIntegrationException("It was not possible to create the user. Code" +
				response.getStatusCode() + " Message:" + response.getStatusText(), null);
	}

}
