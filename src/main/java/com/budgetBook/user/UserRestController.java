package com.budgetBook.user;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.budgetBook.mail.service.MailService;
import com.budgetBook.user.domain.Profile;
import com.budgetBook.user.domain.User;
import com.budgetBook.user.dto.UserDto;
import com.budgetBook.user.service.UserDeleteService;
import com.budgetBook.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/budgetBook/user")
public class UserRestController {
	
	private UserService userService;
	private MailService mailService;
	private UserDeleteService userDeleteService;
	
	UserRestController(UserService userService, MailService mailService, UserDeleteService userDeleteService){
		this.userService = userService;
		this.mailService = mailService;
		this.userDeleteService = userDeleteService;
	}
	
	// 회원가입
	@PostMapping("/join")
	public Map<String, String> join(
			@RequestParam("loginId") String loginId
			, @RequestParam("password") String password
			, @RequestParam("email") String email
			, @RequestParam(value = "snsLogin", required = false) String snsLogin
			, @RequestParam(value = "profileImagePath", required = false) String profileImagePath) {
		
		// 이메일 정규식 패턴
		Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
		        "[a-zA-Z0-9_+&*-]+)*@" +
		        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
		        "A-Z]{2,7}$");
		
		// 이메일 패턴 확인
		Matcher emailMatcher = emailPattern.matcher(email);
		
		if(!emailMatcher.find()) {
			// 이메일 형식이 아니라면
			Map<String, String> middelResultMap = new HashMap<>();
			middelResultMap.put("result", "fail");
			middelResultMap.put("emailPattern", "이메일 형식에 맞게 작성해주세요.");
		}
		
		User user = User.builder()
				.loginId(loginId)
				.password(password)
				.email(email)
				.snsLogin(snsLogin)
				.build();
		
		// 유저 저장
		User result = userService.addUser(user);
		// 유저 프로필 생성
		userService.addProfile(user.getId(), profileImagePath);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
		
	}
	
	// 로그인 아이디 중복 확인
	@GetMapping("/join/loginId-check")
	public Map<String, String> loginIdCheck(@RequestParam("loginId")String loginId){
		User user = userService.findByLoginId(loginId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user == null) {
			resultMap.put("isDuplicate", "false"); // 사용자 없음
		}else {
			resultMap.put("isDuplicate", "true"); // 사용자 있음
		}
		
		return resultMap;
	}
	
	// 이메일 중복 확인
	@GetMapping("/join/email-check")
	public Map<String, String> emailCheck(@RequestParam("email")String email){
		User user = userService.findByEmail(email);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user == null) {
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
			, @RequestParam("password") String password
			, HttpServletRequest request){
		UserDto user = userService.loginService(loginId, password);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("result", "success");
			HttpSession session = request.getSession();
			session.setAttribute("userId", user.getUserId());
			session.setAttribute("loginId", user.getLoginId());
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 비밀번호 확인
	@PostMapping("/passwordCheck")
	public Map<String, String> findPassword(
			@RequestParam("password") String password
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		boolean result = userService.passwordCheckService(userId, password);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 비밀번호 변경
	@PutMapping("/passwordChange")
	public Map<String, String> changePassword(
			@RequestParam("password") String password
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		User user = userService.changePassword(userId, password);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 아이디 변경
	@PutMapping("/loginIdChange")
	public Map<String, String> changeLoginId(
			@RequestParam("loginId") String loginId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		User user = userService.changeLoginId(userId, loginId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 이메일 변경
	@PutMapping("/emailChange")
	public Map<String, String> changeEmail(
			@RequestParam("email") String email
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		User user = userService.changeEmail(userId, email);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 아이디 찾기 - 이메일로
	@PostMapping("/findId")
	public Map<String, String> findId(@RequestParam("email")String email){
		User user = userService.findLoginIdByEmail(email);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user != null) {
			resultMap.put("result", "success");
			resultMap.put("Id", user.getLoginId());
			resultMap.put("snsLogin", user.getSnsLogin());
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 로그인 아이디로 유저를 찾고 그 유저의 이메일로 인증번호 전송
	@GetMapping("/findUser/loginId")
	public Map<String, String> findUserByLoginId(@RequestParam("loginId")String loginId){
		User user = userService.findByLoginId(loginId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(user == null) {
			resultMap.put("isDuplicate", "false"); // 사용자 없음
		}else {
			resultMap.put("isDuplicate", "true"); // 사용자 있음
			if(user.getSnsLogin() != null) {
				// sns로그인 시
				resultMap.put("sns", user.getSnsLogin() + "로 로그인 하셨습니다.");
			}else {
				String email = user.getEmail();
				int num = mailService.sendMail(email);
				if(num >= 0) {
					// 인증번호 전송 성공
					resultMap.put("result", "success");
				}else {
					// 인증번호 전송 실패
					resultMap.put("result", "fail");
				}
			}
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
	
	// 프로필 사진 삭제 후 기본 이미지로 돌아가기
	@DeleteMapping("/profileImage/delete")
	public Map<String, String> profileImageDelete(@RequestParam("userId")int userId){
		boolean result = userService.deleteProfileImage(userId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 회원탈퇴
	@PostMapping("/userDelete")
	public Map<String, String> userDelete(HttpSession session) {
		int userId = (Integer)session.getAttribute("userId");
		boolean result = userDeleteService.deleteUser(userId);
		
		// 세션 제거
		session.removeAttribute("userId");
		session.removeAttribute("loginId");
		
		
		Map<String, String> resultMap = new HashMap<>();
		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
}
