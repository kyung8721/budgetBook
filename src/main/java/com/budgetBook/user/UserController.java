package com.budgetBook.user;

import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${kakao.client_id}")
    private String client_id;
	
	@Value("${kakao.redirect_uri}")
    private String redirect_uri;
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/login-view")
	public String loginView(Model model) {
		
		String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;
        model.addAttribute("kakaoLocation", location); // 카카오 로그인 버튼 클릭 시 보여줄 화면
		
		return "/user/login";
	}
	
	@GetMapping("/join-view")
	public String joinView(Model model) {
		
		String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;
        model.addAttribute("kakaoLocation", location); // 카카오 로그인 버튼 클릭 시 보여줄 화면
        
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
	
	// 아이디 찾기 페이지
	@GetMapping("/findId-view")
	public String findIdView() {
		
		return "/user/findId";
	}
	
	// 비밀번호 찾기 페이지
	@GetMapping("/findPassword-view")
	public String findPasswordView() {
		
		return "/user/findPassword";
	}
	
	
}
