package com.security.jwt.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.security.jwt.enums.ProfileEnum;
import com.security.jwt.utils.ApplicationType;

@Entity(name="User")
@Table(name="users")
public class User {

	private static final Logger log = LoggerFactory.getLogger(User.class);
		
	@Id
	@GeneratedValue(strategy = 	GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_name", nullable=false)
	private String userName;
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
	private List<Registry> registries = new ArrayList<Registry>();
	
	public User() {}
	
	public User(String userName, String email, String password) {
		super();
		this.userName = userName;
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

	public ProfileEnum getProfile() {
		return profile;
	}

	public void setProfile(ProfileEnum profile) {
		this.profile = profile;
	}
	
	public LocalDateTime getSignUpDateTime() {
		return signUpDateTime;
	}

	public void setSignUpDateTime(LocalDateTime signUpDateTime) {
		this.signUpDateTime = signUpDateTime;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Registry> getRegistries() {
		return registries;
	}

	public void setRegistries(List<Registry> myRegistries) {
		this.registries = myRegistries;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean hasRegistry(ApplicationType aplication) {
		for (Registry reg : this.getRegistries()) {
			if(reg.getApplication().getApplication().equals(aplication)) {
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
        return Objects.equals(id, user.id) && this.userName.equals(user.getUserName())
        		&& this.email.equals(user.getEmail());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password);
    }
	
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", email=" + email
				+ ", password=" + password + ", profile=" + profile 
				+ ", signUpDate=" + signUpDateTime + ", registries="
				+ registries.toString() + "]";
	}

	
}
