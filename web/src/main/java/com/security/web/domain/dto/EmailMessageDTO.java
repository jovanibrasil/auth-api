package com.security.web.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailMessageDTO {

	private String text;
	private String textType;
	private String to;
	private String from;
	private String title;

}
