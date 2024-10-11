package com.budgetBook.user.service;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.budgetBook.common.FileManager;
import com.budgetBook.common.HashingEncoder;
import com.budgetBook.common.SHA256HashingEncoder;
import com.budgetBook.user.domain.Profile;
import com.budgetBook.user.domain.User;
import com.budgetBook.user.dto.UserDto;
import com.budgetBook.user.repository.ProfileRepository;
import com.budgetBook.user.repository.UserRepository;

@Service
public class UserService {
	
	private UserRepository userRepository;
	private HashingEncoder encoder;
	private ProfileRepository profileRepository;
	
	UserService(UserRepository userRepository, HashingEncoder encoder, ProfileRepository profileRepository){
		this.userRepository = userRepository;
		this.encoder = encoder;
	}
	
	// 회원가입
	public User addUser(User user) {
		// 개인 salt 추가
		SecureRandom secureRandom = new SecureRandom();
		byte[] byteSalt = new byte[20];
		// 난수 생성
		secureRandom.nextBytes(byteSalt);
		// byte to String
		StringBuffer sb = new StringBuffer();
		for(byte b : byteSalt) {
			sb.append(String.format("%02x", b));
		}
		String salt = sb.toString();
		
		//// 비밀번호 암호화
		HashingEncoder encoder = new SHA256HashingEncoder();
		String encryptPassword = encoder.encode(user.getPassword() + salt);
		
		
		
		// 비밀번호 user에 암호화된 것으로 교체
		user.setPassword(encryptPassword);
		// salt DB에 저장
		user.setSalt(salt);
		
		// user 저장
		return userRepository.save(user);
		
	}
	
	// 로그인 아이디 중복 확인
	public User findByLoginId(String loginId) {
		
		Optional<User> optionalUser = userRepository.findByLoginId(loginId);
		User user = optionalUser.orElse(null);
		
		return user;
		
	}
	
	// 이메일 중복 확인
	public User findByEmail(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		User user = optionalUser.orElse(null);
		
		return user;
	}
	
	// 로그인 서비스
	public UserDto loginService(String loginId, String password) {
		
		Optional<User> optionalUser = userRepository.findByLoginId(loginId);
		User user = optionalUser.orElse(null);
		
		if(user != null) {
			// 사용자가 있으면
			// 개인 salt 불러오기
			String salt = user.getSalt();
			// 입력받은 비밀번호+salt 암호화
			HashingEncoder encoder = new SHA256HashingEncoder();
			String encryptPassword = encoder.encode(password + salt);
			
			// DB에 저장된 비밀번호 소환
			String DBPassword = user.getPassword();
			
			// 비밀번호끼리 비교
			if(encryptPassword == DBPassword) {
				// 비밀번호 동일 : UserDto 리턴
				Profile profile = profileRepository.findByUserId(user.getId());
				UserDto userDto = UserDto.builder()
						.userId(user.getId())
						.profileId(profile.getId())
						.loginId(user.getLoginId())
						.email(user.getEmail())
						.snsLogin(user.getSnsLogin())
						.profileImagePath(profile.getProfileImagePath())
						.build();
				return userDto;
			}else {
				// 비밀번호 일치 안 함
				return null;
			}
		}else {
			// 사용자가 없으면
			return null;
		}
		
	}
	
	// 프로필 이미지 저장
	public String profileImageSave(int userId, MultipartFile img) {
		String urlPath = FileManager.saveFile(userId, img);
		return urlPath;
	}
	
	// 프로필 이미지 업데이트
	public Profile updateProfileImage(int userId, String ProfileImagePath) {
		Profile profile = profileRepository.findByUserId(userId);
		profile.setProfileImagePath(ProfileImagePath);
		
		return profileRepository.save(profile);
		
		
		
	}
}