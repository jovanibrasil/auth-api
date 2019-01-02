package com.jwt.security.dto;

public class JwtAuthenticationDto {

	private String email;
	private String password;
	
	public JwtAuthenticationDto() {}
	
	public JwtAuthenticationDto(String email, String password) {
		super();
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
	
}
