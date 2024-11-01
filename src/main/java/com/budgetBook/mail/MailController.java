package com.budgetBook.mail;

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
}
