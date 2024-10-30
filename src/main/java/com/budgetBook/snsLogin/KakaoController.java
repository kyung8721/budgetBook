package com.budgetBook.snsLogin;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.budgetBook.snsLogin.kakaoService.KakaoService;
import com.budgetBook.user.dto.UserDto;
import com.budgetBook.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/budgetBook/kakao")
public class KakaoController {
	
	private KakaoService kakaoService;
	private UserService userService;
	
	public KakaoController(KakaoService kakaoService, UserService userService) {
		this.kakaoService = kakaoService;
		this.userService = userService;
	}
	
	@PostMapping("/login") // 리다이렉트로 등록한 url
	public void kakaoLogin(@RequestParam(required = false) String code, HttpServletRequest request) {
		
        // URL에 포함된 code를 이용하여 액세스 토큰 발급
        String accessToken = kakaoService.getKakaoAccessToken(code);

        // 액세스 토큰을 이용하여 카카오 서버에서 유저 정보(닉네임, 이메일) 받아오기
        Map<String, Object> userInfo = kakaoService.getUserInfo(accessToken);

        // 만일, DB에 해당 email을 가지는 유저가 없으면 회원가입 시키고 User 객체 반환
        if(userService.findUserByEmail(String.valueOf(userInfo.get("email"))) == null) {
            // 이메일 없음 - 회원가입
        	// 이메일로 회원가입 시키기
        	
        	
        } else {
            // 아니면 기존 유저의 로그인으로 판단
        	UserDto userDto = userService.findUserByEmail(String.valueOf(userInfo.get("email"))); // 이메일로 로그인 아이디 찾기
            HttpSession session = request.getSession();
			session.setAttribute("userId", userDto.getUserId());
			session.setAttribute("loginId", userDto.getLoginId());
            
        }
	    
	}
}

