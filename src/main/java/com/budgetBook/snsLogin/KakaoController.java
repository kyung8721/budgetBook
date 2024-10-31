package com.budgetBook.snsLogin;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/login") // 리다이렉트로 등록한 url
	public String kakaoLogin(@RequestParam(value="code", required = false) String code, HttpServletRequest request) {
		
        // URL에 포함된 code를 이용하여 액세스 토큰 발급
        String accessToken = kakaoService.getKakaoAccessToken(code);

        // 액세스 토큰을 이용하여 카카오 서버에서 유저 정보(이메일) 받아오기
        Map<String, Object> userInfo = kakaoService.getUserInfo(accessToken);
        
        // email이 null이면 에러 회원가입 안되도록.
        if(String.valueOf(userInfo.get("email")) == null) {
        	return "redirect:/budgetBook/user/login-view";
        }

        // 만일, DB에 해당 email을 가지는 유저가 없으면 회원가입 시키고 User 객체 반환
        if(userService.findUserByEmail(String.valueOf(userInfo.get("email"))) == null) {
            // 이메일 없음 - 회원가입
        	// 이메일로 회원가입 시키기
        	UserDto userDto = userService.addUserByEmail(String.valueOf(userInfo.get("email")), "kakao" , String.valueOf(userInfo.get("profileImagePath")));
        	
        	HttpSession session = request.getSession();
			session.setAttribute("userId", userDto.getUserId());
			session.setAttribute("loginId", userDto.getLoginId());
        } else {
            // 아니면 기존 유저의 로그인으로 판단
        	UserDto userDto = userService.findUserByEmail(String.valueOf(userInfo.get("email"))); // 이메일로 로그인 아이디 찾기
            HttpSession session = request.getSession();
			session.setAttribute("userId", userDto.getUserId());
			session.setAttribute("loginId", userDto.getLoginId());
            
        }
	 
        return "redirect:/budgetBook/money/main-view";
	}
}

