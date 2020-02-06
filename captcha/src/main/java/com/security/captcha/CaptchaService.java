package com.security.captcha;

public interface CaptchaService {

	/**
	 * Verifies the reCaptcha response.
	 * 
	 * @param response
	 * @throws InvalidRecaptchaException
	 * @throws ReCaptchaInvalidException
	 */
	void processResponse(String response) throws InvalidRecaptchaException, ReCaptchaInvalidException;
	String getReCaptchaSecret();
	String getReCaptchaSite();
	
}
