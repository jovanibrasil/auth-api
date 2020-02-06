package com.security.jwt.model;

import com.security.jwt.enums.ProfileEnum;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class User {

	private Long id;
	private String userName;
	private String email;
	private String password;
	private ProfileEnum profile;
	private LocalDateTime signUpDateTime;

}
