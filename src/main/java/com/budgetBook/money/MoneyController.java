package com.budgetBook.money;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/budgetBook/money")
public class MoneyController {
	
	@GetMapping("/main-view")
	public String mainView() {
		return "money/main";
	}
	
	@GetMapping("/fixedCost-view")
	public String fixedCostView() {
		return "money/fixedCost";
	}
	
	// 상세 내역 모달
	@GetMapping("/detailList")
	public String detailListView() {
		return "money/detail";
	}
	
	// 상세 내역 모달
	@GetMapping("/breakdown-view")
	public String breakdownView() {
		return "money/detailView";
	}
}
