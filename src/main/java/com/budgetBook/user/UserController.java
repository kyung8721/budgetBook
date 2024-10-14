package com.budgetBook.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.budgetBook.user.dto.UserDto;
import com.budgetBook.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/budgetBook/user")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/login-view")
	public String loginView() {
		return "/user/login";
	}
	
	@GetMapping("/join-view")
	public String joinView() {
		return "/user/join";
	}
	
	@GetMapping("/profile-view")
	public String profileView(HttpSession session, Model model) {
		
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = userService.userData(userId);
		
		model.addAttribute("user", userDto);
		
		
		return "/user/profile";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		// session 내의 내용을 삭제
		HttpSession session = request.getSession();
		
		session.removeAttribute("userId");
		session.removeAttribute("loginId");
		
		return "redirect:/budgetBook/user/login-view";
	}
	
	
}
