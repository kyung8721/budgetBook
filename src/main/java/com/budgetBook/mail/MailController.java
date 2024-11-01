package com.budgetBook.mail;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budgetBook.mail.service.MailService;

@RestController
@RequestMapping("/budgetBook/user/mail")
public class MailController {
	
	private MailService mailService;
	
	public MailController(MailService mailService) {
		this.mailService = mailService;
	}
	
	@GetMapping("/send")
	public String emailSend(@RequestParam("mail")String mail) {
		int number = mailService.sendMail(mail);
		String num = Integer.toString(number);
		
		return num;
	}
	
	@GetMapping("/check")
	public Map<String, String> numberCheck(String loginId, String number){
		
		boolean result = mailService.checkNumber(loginId, number);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
}
