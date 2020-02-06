package com.security.captcha;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RecaptchaAttemptService {

	private final int MAX_ATTEMPT = 4;
	private LoadingCache<String, Integer> attemptsCache;
	
	public RecaptchaAttemptService() {
		super();
		// Limit the client to MAX_ATTEMPT failed captcha responses
		this.attemptsCache = CacheBuilder.newBuilder()
				.expireAfterWrite(4, TimeUnit.HOURS)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(String key) throws Exception {
						return 0;
					}
				});
	}
	
	/**
	 * Resets the attempt cache for the specified user.
	 * 
	 * @param key
	 */
	public void recaptchaSucceeded(String key) {
		attemptsCache.invalidate(key);
	}
	
	/**
	 * Increment the attempts cache for the specified user.
	 * 
	 * @param key
	 */
	public void recaptchaFailed(String key) {
		int attempts = attemptsCache.getUnchecked(key);
		attemptsCache.put(key, ++attempts);
	}
	
	/**
	 * Verifies if a client is blocked. A client is blocked when he failed
	 * MAX_ATTEMPT captcha responses.
	 * 
	 * @param key
	 * @return
	 */
	public boolean isBlocked(String key) {
		return attemptsCache.getUnchecked(key) >= MAX_ATTEMPT;
	}
	
}
