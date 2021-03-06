package com.security.web.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * 
 * Composite key
 * 
 * @author Jovani Brasil
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor 
@Embeddable
public class RegistryId implements Serializable {

	private static final long serialVersionUID = 6528912294242523812L;

	@Column(name="application_id")
	private Long applicationId;
	
	@Column(name="user_id")
	private Long userId;

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
