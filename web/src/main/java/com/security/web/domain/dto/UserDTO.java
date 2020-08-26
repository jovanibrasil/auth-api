package com.security.web.domain.dto;

import java.io.Serializable;

import com.security.web.domain.ApplicationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@ToString
public class UserDTO implements Serializable {

	private static final long serialVersionUID = -4139753881305795908L;

	private Long id;
	private String email;
	private String userName;
	private ApplicationType application;
	
}
