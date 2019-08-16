package com.jwt.security.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity(name = "Registry")
@Table(name = "registries")
public class Registry {

	@EmbeddedId
	private RegistryId id; // primary key

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	//@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("applicationId")
	//@JoinColumn(name = "application_id", insertable = false, updatable = false)
	private Application application;

	@Column(name = "registered_at")
	private LocalDateTime registeredAt;

	public Registry() {}

	public Registry(Application application, User user) {
		this.id = new RegistryId(user.getId(), application.getId());
		this.application = application;
		this.user = user;
		this.registeredAt = LocalDateTime.now();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
	public RegistryId getId() {
		return id;
	}

	public void setId(RegistryId id) {
		this.id = id;
	}

	public LocalDateTime getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(LocalDateTime registeredAt) {
		this.registeredAt = registeredAt;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registry app = (Registry) o;
        return Objects.equals(this.id.getUserId(), app.getId().getUserId()) 
        		&& Objects.equals(this.id.getApplicationId(),
        				app.getId().getApplicationId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id.getApplicationId(), this.id.getUserId());
	}
	
	@Override
	public String toString() {
		return "Registry [id=" + id + ", user=" + user + ", application=" + application + ", registeredAt="
				+ registeredAt + "]";
	}

}
