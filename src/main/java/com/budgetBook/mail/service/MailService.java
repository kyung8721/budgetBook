package com.budgetBook.mail.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	private final JavaMailSender javaMailSender;
    private static final String senderEmail= "${spring.mail.username}";
    private static int number;
    
    public MailService(JavaMailSender javaMailSender) {
    	this.javaMailSender = javaMailSender;
    }

    // 랜덤 인증번호 생성
    public static void createNumber(){
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }
	
    // 메일 내용 및 설정 생성
    public MimeMessage CreateMail(String mail){
        createNumber(); // 인증번호 생성
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
}
