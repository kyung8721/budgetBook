package com.budgetBook.user.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import com.budgetBook.common.HashingEncoder;
import com.budgetBook.common.SHA256HashingEncoder;
import com.budgetBook.user.domain.User;
import com.budgetBook.user.repository.UserRepository;

@Service
public class UserService {
	
	private UserRepository userRepository;
	private HashingEncoder encoder;
	
	UserService(UserRepository userRepository, HashingEncoder encoder){
		this.userRepository = userRepository;
		this.encoder = encoder;
	}
	
	public User addUser(User user) {
		
		// 비밀번호 암호화
		HashingEncoder encoder = new SHA256HashingEncoder();
		String encryptPassword = encoder.encode(user.getPassword());
		
		// 개인 salt 추가
		SecureRandom secureRandom = new SecureRandom();
		byte[] salt = new byte[20];
		
		
		// 비밀번호 user에 암호화된 것으로 교체
		user.setPassword(encryptPassword + salt);
		// salt DB에 저장
		user = User.builder()
				.salt(salt);
		
		// user 저장
		return userRepository.save(user);
		
	}
}
