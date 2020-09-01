package com.security.web.domain.dto;

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
public class UserDTO {

	private Long id;
	private String email;
	private String userName;
	private ApplicationType application;

}
