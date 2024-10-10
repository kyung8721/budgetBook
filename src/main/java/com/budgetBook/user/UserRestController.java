package com.budgetBook.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budgetBook.user.domain.User;
import com.budgetBook.user.service.UserService;

@RestController
@RequestMapping("/budgetBook/user")
public class UserRestController {
	
	private UserService userService;
	
	UserRestController(UserService userService){
		this.userService = userService;
	}
	
	// 회원가입
	@PostMapping("/join")
	public Map<String, String> join(
			@RequestParam("loginId") String loginId
			, @RequestParam("password") String password
			, @RequestParam("email") String email
			, @RequestParam("snsLogin") String snsLogin) {
		
		User user = User.builder()
				.loginId(loginId)
				.password(password)
				.email(email)
				.snsLogin(snsLogin)
				.build();
		
		User result = userService.addUser(user);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "false");
		}
		
		return resultMap;
		
	}
}
