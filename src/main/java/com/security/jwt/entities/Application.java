package com.security.jwt.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalIdCache;

import com.security.jwt.utils.ApplicationType;

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
