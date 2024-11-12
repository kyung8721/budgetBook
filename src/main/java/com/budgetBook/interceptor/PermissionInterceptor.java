package com.budgetBook.interceptor;

import java.io.IOException;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class PermissionInterceptor implements HandlerInterceptor{
	@Override
	public boolean preHandle(
			HttpServletRequest request
			, HttpServletResponse response
			, Object handler) throws IOException {
		
		HttpSession session = request.getSession();
		Integer userId = (Integer)session.getAttribute("userId");
		
		// 요청된 url을 받아옴
		String uri = request.getRequestURI();
		
		// 로그인이 안된 상태라면 로그인 화면으로 이동
		if(userId == null) {
			if(uri.startsWith("/budgetBook/money/")) {
				// url이 money로 시작된다면
				response.sendRedirect("/budgetBook/user/login-view");
				return false;
			}else if(uri.startsWith("/budgetBook/user/profile-view")){
				// url이 user로 시작되는 프로필 수정 페이지라면
				response.sendRedirect("/budgetBook/user/login-view");
				return false;
			}
		}else {
			// 로그인된 상태라면 로그인 화면에 접근하지 못 하도록 설정
			if(uri.startsWith("/budgetBook/user/")
					&& !(uri.startsWith("/budgetBook/user/profile-view"))
					&& !(uri.startsWith("/budgetBook/user/join/email-check"))
					&& !(uri.startsWith("/budgetBook/user/emailChange"))
					&& !(uri.startsWith("/budgetBook/user/loginIdChange"))
					&& !(uri.startsWith("/budgetBook/user/join/loginId-check"))
					&& !(uri.startsWith("/budgetBook/user/passwordChange"))
					&& !(uri.startsWith("/budgetBook/user/passwordCheck"))
					&& !(uri.startsWith("/budgetBook/user/profileImage/delete"))
					&& !(uri.startsWith("/budgetBook/user/profileImage"))) {
				response.sendRedirect("/budgetBook/money/main-view");
				return false;
			}
		}
		
		return true;
	}
}
