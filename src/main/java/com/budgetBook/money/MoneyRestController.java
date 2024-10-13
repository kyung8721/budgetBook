package com.budgetBook.money;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budgetBook.money.domain.FixedCost;
import com.budgetBook.money.service.MoneyService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/budgetBook/money")
public class MoneyRestController {
	
	private MoneyService moneyService;
	
	public MoneyRestController(MoneyService moneyService) {
		this.moneyService = moneyService;
	}
	
	// 고정비 작성 및 수정
	@PostMapping("/fixedCost/create")
	public Map<String, String> fixedCostCreate(
			@RequestParam("classification")String classification
			, @RequestParam("period") String period
			, @RequestParam("assetsId") int assetsId
			, @RequestParam(value = "categoryId", required = false) int categoryId
			, @RequestParam(value = "detailCategoryId") int detailCategoryId
			, @RequestParam("fixedCost") int fixedCost
			, @RequestParam(value = "memo") String memo
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		FixedCost fixedCostObject = FixedCost.builder()
				.userId(userId)
				.classification(classification)
				.period(period)
				.assetsId(assetsId)
				.categoryId(categoryId)
				.detailCategoryId(detailCategoryId)
				.fixedCost(fixedCost)
				.memo(memo)
				.build();
		
		FixedCost result = moneyService.addFixedCost(fixedCostObject);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "false");
		}
		
		return resultMap;
		
	}
	
	// 고정비 삭제
	@DeleteMapping("/fixedCost/delete")
	public Map<String, String> fixedCostDelete(
			@RequestParam("fixedCostId") int fixedCostId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		boolean result = moneyService.deleteFixedCost(userId, fixedCostId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "false");
		}
		
		return resultMap;
	}
}
