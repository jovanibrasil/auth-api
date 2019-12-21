package com.security.jwt.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor
@ToString
@Entity(name = "Registry")
@Table(name = "registries")
public class Registry {

	@EmbeddedId
	private RegistryId id; // primary key

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("userId")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("applicationId")
	private Application application;

	@Column(name = "registered_at")
	private LocalDateTime registeredAt;

	public Registry(Application application, User user) {
		this.id = new RegistryId(user.getId(), application.getId());
		this.application = application;
		this.user = user;
		this.registeredAt = LocalDateTime.now();
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

}
