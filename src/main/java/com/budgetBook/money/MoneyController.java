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
}
