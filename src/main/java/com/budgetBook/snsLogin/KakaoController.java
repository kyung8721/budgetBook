package com.budgetBook.snsLogin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/budgetBook/kakao")
public class KakaoController {
	@GetMapping("/login") // 리다이렉트로 등록한 url
	public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {
		// 해당 code를 https://kauth.kakao.com/oauth/token URL로 POST 요청을 보내면 토큰을 받을 수 있음.
		// 토큰 받아오는 dto 생성하기
		// 토큰으로 사용자 정보 받아오기(dto로)
		// 
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
