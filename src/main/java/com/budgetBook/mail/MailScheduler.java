package com.budgetBook.mail;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.budgetBook.mail.domain.CertificationNumber;
import com.budgetBook.mail.service.MailService;

@Component
public class MailScheduler {
	
	private MailService mailService;
	
	public MailScheduler(MailService mailService) {
    	this.mailService = mailService;
    }
	
	@Scheduled(cron = "0 0 0 * * ?") // 매일 00시 00분에 실행
	private void mailSchedule() {
		// 인증번호 목록 불러오기
		List<CertificationNumber> certificationNumberList = mailService.certificationNumberList();
		
		// 인증번호 목록 중 3분 이상 지난 번호들은 삭제하기
		for(CertificationNumber c : certificationNumberList) {
			// 시간 확인
	    	LocalDateTime saveTime = c.getCreatedAt(); // 인증번호가 만들어진 시간
	    	LocalDateTime now = LocalDateTime.now(); // 현재 시간
	    	
	    	// 시간 차 확인
	    	Duration diff = Duration.between(saveTime, now);
	    	long diffMin = diff.toMinutes();
	    	
			if(diffMin >= 3) { // 시간 차가 3분 이상일 시 삭제
				mailService.deleteCertificationNumber(c);
			}
		}
		
	}
}
