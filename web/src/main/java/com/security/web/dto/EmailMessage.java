package com.security.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailMessage {

	private String text;
	private String textType;
	private String to;
	private String from;
	private String title;

}
