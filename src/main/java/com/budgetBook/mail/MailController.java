package com.budgetBook.mail;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budgetBook.mail.service.MailService;
import com.budgetBook.user.service.UserService;

@RestController
@RequestMapping("/budgetBook/user/mail")
public class MailController {
	
	private MailService mailService;
	private UserService userService;
	
	public MailController(MailService mailService,UserService userService) {
		this.mailService = mailService;
		this.userService = userService;
	}
	
	// 메일 전송
	@GetMapping("/send")
	public Map<String, String> emailSend(@RequestParam("mail")String mail) {
		int number = mailService.sendMail(mail);
		String num = Integer.toString(number);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(num != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 이메일로 인증번호 확인
	@GetMapping("/check")
	public Map<String, String> numberCheck(@RequestParam("mail")String email, @RequestParam("number")String number){
		
		boolean result = mailService.checkNumberNewUser(email, number);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 인증번호 확인 후 임시 비밀번호 생성
	@GetMapping("/check/createNewPassword")
	public Map<String, String> numberCheckAndCreateNewPassword(@RequestParam("loginId")String loginId, @RequestParam("number")String number){
		
		boolean result = mailService.checkNumber(loginId, number);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result) {
			String password = userService.randomPassword(loginId);
			resultMap.put("result", "success");
			resultMap.put("password", password);
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
}
