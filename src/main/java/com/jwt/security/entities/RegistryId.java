package com.jwt.security.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * Composite key
 * 
 * @author Jovani Brasil
 *
 */
@Embeddable
public class RegistryId implements Serializable {

	private static final long serialVersionUID = 6528912294242523812L;

	@Column(name="application_id")
	private Long applicationId;
	
	@Column(name="user_id")
	private Long userId;

	public RegistryId() {}
	
	public RegistryId(Long userId, Long applicationId) {
		this.userId = userId;
		this.applicationId = applicationId;
	}
	
	public Long getApplicationId() {
		return applicationId;
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		 
        if (o == null || getClass() != o.getClass())
            return false;
 
        RegistryId that = (RegistryId) o;
        return userId.equals(that.userId) && applicationId.equals(applicationId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.userId, this.applicationId);
	}
	
}
