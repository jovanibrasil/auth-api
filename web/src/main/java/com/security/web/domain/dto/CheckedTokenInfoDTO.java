package com.security.web.domain.dto;

import com.security.jwt.model.enums.ProfileEnum;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class CheckedTokenInfoDTO {

	private String name;
	private ProfileEnum role;

}
