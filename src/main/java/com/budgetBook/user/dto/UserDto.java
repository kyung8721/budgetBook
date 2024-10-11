package com.budgetBook.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
	private int userId;
	private int profileId;
	
	private String loginId;
	private String email;
	
	private String snsLogin;
	
	
	private String profileImagePath;
}
