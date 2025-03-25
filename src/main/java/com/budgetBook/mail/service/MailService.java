package com.budgetBook.mail.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.budgetBook.mail.domain.CertificationNumber;
import com.budgetBook.mail.repository.CertificationNumberRepository;
import com.budgetBook.user.domain.User;
import com.budgetBook.user.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	private final JavaMailSender javaMailSender;
    private static final String senderEmail= "${spring.mail.username}";
    private static int number;
    private CertificationNumberRepository certificationNumberRepository;
    private UserRepository userRepository;
    
    public MailService(JavaMailSender javaMailSender, CertificationNumberRepository certificationNumberRepository, UserRepository userRepository) {
    	this.javaMailSender = javaMailSender;
    	this.certificationNumberRepository = certificationNumberRepository;
    	this.userRepository = userRepository;
    }

    // 랜덤 인증번호 생성
    public static void createNumber(){
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }
	
    // 메일 내용 및 설정 생성
    public MimeMessage CreateMail(String mail){
        createNumber(); // 인증번호 생성
        
        Optional<CertificationNumber> optionalSaveNumber = certificationNumberRepository.findByEmail(mail);
        CertificationNumber saveNumber = optionalSaveNumber.orElse(null);
        
        if(saveNumber == null) {
        	// 인증번호 저장
            saveNumber = CertificationNumber.builder()
            		.email(mail)
            		.number(Integer.toString(number))
            		.build();
        }else {
        	saveNumber.setNumber(Integer.toString(number));
        }
        
        certificationNumberRepository.save(saveNumber);
        
        // 이메일 생성
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail); //이메일 보낼 메일 주소
            message.setRecipients(MimeMessage.RecipientType.TO, mail); // 받는 사람 설정
            message.setSubject("이메일 인증"); // 이메일 제목
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html"); // 메일 내용
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }
    
    // 메일 보내기
    public int sendMail(String mail){
        MimeMessage message = CreateMail(mail);
        javaMailSender.send(message);

        return number;
    }
    
    // 메일로 보낸 인증번호 확인 - 기존 사용자의 경우
    public boolean checkNumber(String LoginId, String number) {
    	
    	// 입력한 로그인 아이디로 이메일 찾기
    	Optional<User> optionaluser = userRepository.findByLoginId(LoginId);
    	User user = optionaluser.orElse(null);
    	
    	// 이메일로 인증번호 찾기
    	Optional<CertificationNumber> optionalSaveNumber = certificationNumberRepository.findByEmail(user.getEmail());
    	CertificationNumber saveNumber = optionalSaveNumber.orElse(null);
    	
    	// 저장된 인증번호
    	String saveNum = saveNumber.getNumber();
    	
    	// 시간 확인
    	LocalDateTime saveTime = saveNumber.getCreatedAt(); // 인증번호가 만들어진 시간
    	LocalDateTime now = LocalDateTime.now(); // 현재 시간
    	
    	// 시간 차 확인
    	Duration diff = Duration.between(saveTime, now);
    	long diffMin = diff.toMinutes();
    	if(diffMin >= 3) {
    		return false;
    	}else {
    		// 인증번호 확인
    		if(saveNum.equals(number)) {
    			// 인증번호 삭제
    			certificationNumberRepository.delete(saveNumber);
    			return true;
    		}else {
    			return false;
    		}
    	}
    	
    }
    
    // 메일로 보낸 인증번호 확인 - 사용자 가입 시
    public boolean checkNumberNewUser(String email, String number) {
    	
    	// 이메일로 인증번호 찾기
    	Optional<CertificationNumber> optionalSaveNumber = certificationNumberRepository.findByEmail(email);
    	CertificationNumber saveNumber = optionalSaveNumber.orElse(null);
    	
    	// 저장된 인증번호
    	String saveNum = saveNumber.getNumber();
    	
    	// 시간 확인
    	LocalDateTime saveTime = saveNumber.getCreatedAt(); // 인증번호가 만들어진 시간
    	LocalDateTime now = LocalDateTime.now(); // 현재 시간
    	
    	// 시간 차 확인
    	Duration diff = Duration.between(saveTime, now);
    	long diffMin = diff.toMinutes();
    	if(diffMin >= 3) {
    		return false;
    	}else {
    		// 인증번호 확인
    		if(saveNum.equals(number)) {
    			certificationNumberRepository.deleteById(saveNumber.getId()); // 인증번호 삭제
    			return true;
    		}else {
    			return false;
    		}
    	}
    }
    
    // 메일 인증번호 불러오기
    public List<CertificationNumber> certificationNumberList(){
    	return certificationNumberRepository.findAll();
    }
    
    // 메일 인증번호 삭제하기
    public void deleteCertificationNumber(CertificationNumber c) {
    	certificationNumberRepository.delete(c);
    }
}
