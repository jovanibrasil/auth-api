package com.security.web.domain;

import com.security.jwt.enums.ProfileEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name="users")
public class User implements UserDetails {

	private static final long serialVersionUID = -2057090119466308446L;

	@Id
	@GeneratedValue(strategy = 	GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_name", nullable=false)
	private String username;
	@Column(name="email", nullable=false)
	private String email;
	@Column(name="password", nullable=false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(name="profile", nullable=false)
	private ProfileEnum profile;
	@Column(name="date", nullable=false)
	private LocalDateTime signUpDateTime;
	
	@OneToMany(
	        mappedBy = "user",
	        cascade = {CascadeType.ALL},
	       	orphanRemoval = true,
	       	fetch = FetchType.EAGER
	    )
	private List<Registry> registries = new ArrayList<>();

	public User(String userName, String email, String password) {
		super();
		this.username = userName;
		this.email = email;
		this.password = password;
	}

	public boolean hasRegistry(ApplicationType aplication) {
		for (Registry reg : getRegistries()) {
			if(reg.getApplication().getType().equals(aplication)) {
				return true;
			}
		}
		return false;
	}
	
	public void addApplication(Application application) {
		Registry registry = new Registry(application, this);
		registries.add(registry);
		application.getRegistries().add(registry);
	}
	
	public void removeApplication(Application app) {
        
		for (Iterator<Registry> iterator = registries.iterator(); iterator.hasNext(); ) {
            Registry reg = iterator.next();
 
            if (reg.getUser().equals(this) &&
                    reg.getApplication().equals(app)) {
                iterator.remove();
                reg.getApplication().getRegistries().remove(reg);
                reg.setUser(null);
                reg.setApplication(null);
            }
        }
    }
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && this.username.equals(user.getUsername())
        		&& this.email.equals(user.getEmail());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

	@Override
	public String toString(){
		return username + " - " + email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(this.profile);
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
