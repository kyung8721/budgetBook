package com.budgetBook.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/budgetBook/user")
public class UserController {
	
	@GetMapping("/login-view")
	public String loginView() {
		return "/user/login";
	}
	
	@GetMapping("/join-view")
	public String joinView() {
		return "/user/join";
	}
}
