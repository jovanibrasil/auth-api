package com.security.web.domain;

import com.security.jwt.enums.ProfileEnum;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class TempUser {

	private String name;
	private ProfileEnum role;

}
