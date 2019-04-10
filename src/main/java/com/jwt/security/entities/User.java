package com.jwt.security.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.jwt.security.enums.ProfileEnum;

@Entity
@Table(name="users")
public class User {

	@Id
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
	private Date signUpDate;

	@Column(name="application_name", nullable=false)
	@ElementCollection(targetClass=String.class, fetch = FetchType.EAGER)
	@CollectionTable(name="applications", joinColumns = @JoinColumn(name = "user_name"))
	private List<String> myApplications;

	public User() {
		this.myApplications = new ArrayList<String>();
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
	
	public Date getSignUpDate() {
		return signUpDate;
	}

	public void setSignUpDate(Date signUpDate) {
		this.signUpDate = signUpDate;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getMyApplications() {
		return myApplications;
	}

	public void setMyApplications(List<String> myApplications) {
		this.myApplications = myApplications;
	}
	
}
