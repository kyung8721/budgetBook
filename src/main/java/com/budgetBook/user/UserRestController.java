package com.budgetBook.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.budgetBook.user.domain.Profile;
import com.budgetBook.user.domain.User;
import com.budgetBook.user.dto.UserDto;
import com.budgetBook.user.service.UserService;

import jakarta.servlet.http.HttpSession;

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
	
	// 로그인 아이디 중복 확인
	@GetMapping("/join/loginId-check")
	public Map<String, String> loginIdCheck(@RequestParam("loginId")String loginId){
		User user = userService.findByLoginId(loginId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("isDuplicate", "false");
		}else {
			resultMap.put("isDuplicate", "true");
		}
		
		return resultMap;
	}
	
	// 이메일 중복 확인
	@GetMapping("/join/email-check")
	public Map<String, String> emailCheck(@RequestParam("email")String email){
		User user = userService.findByEmail(email);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("isDuplicate", "false");
		}else {
			resultMap.put("isDuplicate", "true");
		}
		
		return resultMap;
	}
	
	// 로그인
	@PostMapping("/login")
	public Map<String, String> login(
			@RequestParam("loginId")String loginId
			, @RequestParam("password") String password){
		UserDto user = userService.loginService(loginId, password);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 프로필 사진 수정
	@PutMapping("/profileImage")
	public Map<String, String> profileImage(
			@RequestParam("profileImage")MultipartFile profileImage
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		// 프로필 사진 저장
		String profileImagePath = userService.profileImageSave(userId, profileImage);
		
		// 프로필 사진 경로 업데이트
		Profile profile = userService.updateProfileImage(userId, profileImagePath);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(profile != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
}