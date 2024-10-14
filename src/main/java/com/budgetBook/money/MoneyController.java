package com.budgetBook.money;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.budgetBook.money.service.MoneyService;
import com.budgetBook.user.dto.UserDto;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/budgetBook/money")
public class MoneyController {
	
	private MoneyService moneyService;
	
	public MoneyController(MoneyService moneyService) {
		this.moneyService = moneyService;
	}
	
	@GetMapping("/main-view")
	public String mainView(HttpSession session, Model model) {
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		return "money/main";
	}
	
	// 고정비 작성 페이지
	@GetMapping("/fixedCost-view")
	public String fixedCostView() {
		return "money/fixedCost";
	}
	
	// 상세 내역 모달
	@GetMapping("/detailModal")
	public String detailListView() {
		return "money/detail";
	}
	
	// 고정비 작성 모달
	@GetMapping("/fixedCostModal")
	public String fixedCostModalView() {
		return "money/fixedCostModal";
	}
	
	// 내역 페이지
	@GetMapping("/breakdown-view")
	public String breakdownView() {
		return "money/detailView";
	}
	
	// 예산 예측 페이지
	@GetMapping("/budgetPrediction-view")
	public String budgetPredictionView() {
		return "money/budgetPrediction";
	}
	
	// 카테고리 추가 모달
	@GetMapping("/categoryModal")
	public String categoryModalView() {
		return "money/categoryModal";
	}
	
	// 자산 페이지
	@GetMapping("/assets-view")
	public String assetView() {
		return "money/assetView";
	}
	
	// 자산 추가 모달 페이지
	@GetMapping("/assetModal")
	public String assetModalView() {
		return "money/assetModal";
	}
	
	// 통계 - 지출 페이지
	@GetMapping("/statics/outgoing-view")
	public String staticOutGoingView() {
		return "money/staticOutGoingView";
	}
	
	// 통계 - 수입 페이지
	@GetMapping("/statics/income-view")
	public String staticinComeView() {
		return "money/staticinComeView";
	}
}
