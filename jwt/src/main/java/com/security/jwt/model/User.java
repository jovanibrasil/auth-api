package com.security.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.security.jwt.model.enums.ProfileEnum;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private Long id;
	private String userName;
	private String email;
	private String password;
	private ProfileEnum profile;
	private LocalDateTime signUpDateTime;

}
