package com.security.jwt.entities;

import com.security.jwt.utils.ApplicationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name="applications")
@NaturalIdCache
public class Application {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "application_type", nullable = false)
	private ApplicationType application;

	@OneToMany(
	        mappedBy = "application",
	        cascade = {CascadeType.ALL},
	        orphanRemoval = true,
	        fetch = FetchType.LAZY
	    )
	private List<Registry> registries = new ArrayList<Registry>();

	public Application(ApplicationType applicationType) {
		this.application = applicationType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application app = (Application) o;
        return this.application.name().equals(app.application.name())
        		&& this.id.equals(app.getId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.application);
	}
	
}
