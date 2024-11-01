package com.budgetBook.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.budgetBook.common.CreateSalt;
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
		this.profileRepository = profileRepository;
	}
	
	// 회원가입
	public User addUser(User user) {
		
		String salt = CreateSalt.CreateSaltToString();
		
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
	
	// 이메일로 회원가입
	public UserDto addUserByEmail(String email, String snsLogin, String profileImagePath) {
		
		// sns 가입 시 비밀번호는 랜덤으로 생성 - 암호화를 여러번 돌린다.
		String salt = CreateSalt.CreateSaltToString();
		
		//// 비밀번호 암호화
		HashingEncoder encoder = new SHA256HashingEncoder();
		String encryptPassword = encoder.encode(encoder.encode(encoder.encode(salt))); // 암호화를 3번 반복
		
		User user = User.builder()
				.loginId(email)
				.password(encryptPassword)
				.email(email)
				.snsLogin(snsLogin)
				.salt("-")
				.build();
		
		// user 저장
		User userSave = userRepository.save(user);
		
		// profile 저장
		Profile profile = addProfile(userSave.getId(), profileImagePath);
		
		// userDto 변환
		UserDto userDto = UserDto.builder()
				.userId(user.getId())
				.profileId(profile.getId())
				.loginId(user.getLoginId())
				.email(user.getEmail())
				.snsLogin(user.getSnsLogin())
				.profileImagePath(profile.getProfileImagePath())
				.build();
		
		return userDto;
		
	}
	
	// 프로필 생성
	public Profile addProfile(int userId, String profileImagePath) {
		
		if(profileImagePath != null) {
			Profile profile = Profile.builder()
					.userId(userId)
					.profileImagePath(profileImagePath)
					.build();
			return profileRepository.save(profile);
		}else {
			Profile profile = Profile.builder()
					.userId(userId)
					.build();
			return profileRepository.save(profile);
		}
		
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
			if(encryptPassword.equals(DBPassword)) {
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
	
	// 이메일로 로그인 아이디 검색
	public User findLoginIdByEmail(String email) {
		Optional<User> optionalUser =  userRepository.findByEmail(email);
		User user = optionalUser.orElse(null);
		
		if(user != null) {
			return user;
		}else {
			return null;
		}
	}
	
	// 비밀번호 랜덤으로 변경
	public User randomPassword(int userId) {
		Optional<User> optionalUser =  userRepository.findById(userId);
		User user = optionalUser.orElse(null);
		
		if(user != null) {
			// 비밀번호 재 설정 후 user 리턴
			
			// salt 재 설정
			String salt = CreateSalt.CreateSaltToString();
			// 랜덤 비밀번호 설정
			String newPassword = CreateSalt.CreateSaltToString();
			String encryptPassword = encoder.encode(newPassword + salt);
			
			
			// 비밀번호 user에 저장
			user.setPassword(encryptPassword);
			// salt DB에 저장
			user.setSalt(salt);
			
			return userRepository.save(user);
		}else {
			// user가 없으면 null 리턴
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
	
	// 프로필 사진 삭제 후 기본 이미지로 돌아가기
	public boolean deleteProfileImage(int userId) {
		Profile profile = profileRepository.findByUserId(userId);
		
		if(profile != null) {
			// 프로필 DB에 저장된 경로의 사진 및 폴더 삭제
			FileManager.deleteImageFile(profile.getProfileImagePath());
			// 프로필 DB update
			profile.setProfileImagePath("https://cdn.pixabay.com/photo/2016/11/14/17/39/person-1824144_1280.png");
			profileRepository.save(profile);
			
			return true;
		}else {
			return false;
		}
		
	}
	
	// 사용자 정보 조회 및 전달
	public UserDto userData(int userId) {
		Optional<User> optionalUser = userRepository.findById(userId);
		User user = optionalUser.orElse(null);
		Profile profile = profileRepository.findByUserId(userId);
		
		if(user == null || profile == null) {
			return null;
		}else {
			UserDto userDto = UserDto.builder()
					.userId(user.getId())
					.profileId(profile.getId())
					.loginId(user.getLoginId())
					.email(user.getEmail())
					.snsLogin(user.getSnsLogin())
					.profileImagePath(profile.getProfileImagePath())
					.build();
			return userDto;
		}
	}
	

	// 이메일을 로그인 아이디로 해서 사용자 찾기 -> UserDto로 내보내기
	public UserDto findUserByEmail(String email) {
		User user = findByLoginId(email);
		
		if(user != null) {
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
			return null;
		}
	}
}
