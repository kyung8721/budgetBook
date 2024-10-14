package com.budgetBook.money;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budgetBook.money.domain.Assets;
import com.budgetBook.money.domain.Category;
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
	@PostMapping("/fixedCost/save")
	public Map<String, String> fixedCostSave(
			@RequestParam("classification")String classification
			, @RequestParam("period") String period
			, @RequestParam("assetsId") int assetsId
			, @RequestParam(value = "categoryId", required = false) int categoryId
			, @RequestParam(value = "detailCategoryId") int detailCategoryId
			, @RequestParam("fixedCost") int fixedCost
			, @RequestParam(value = "memo") String memo
			, @RequestParam(value = "fixedCostId", required = false) Integer fixedCostId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		FixedCost result;
		
		if(fixedCostId == null) {
			// 고정비 작성
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
			result = moneyService.saveFixedCost(fixedCostObject);
		}else {
			// 고정비 수정
			FixedCost fixedCostObject = FixedCost.builder()
					.id(fixedCostId)
					.userId(userId)
					.classification(classification)
					.period(period)
					.assetsId(assetsId)
					.categoryId(categoryId)
					.detailCategoryId(detailCategoryId)
					.fixedCost(fixedCost)
					.memo(memo)
					.build();
			result = moneyService.saveFixedCost(fixedCostObject);
		}
		
		
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
	
	// 자산 생성 및 수정
	@PostMapping("/assets/save")
	public Map<String, String> assetsSave(
			@RequestParam("assetsName")String assetsName
			, @RequestParam("balance")int balance
			, @RequestParam("color")String color
			, @RequestParam("memo")String memo
			, @RequestParam("assetsId") Integer assetsId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		Assets result = moneyService.addAssets(userId, assetsName, balance, color, memo, assetsId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "false");
		}
		
		return resultMap;
		
	}
	
	// 자산 삭제
	@DeleteMapping("/assets/delete")
	public Map<String, String> assetsDelete(
			@RequestParam("assetsId")int assetsId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		boolean result = moneyService.deleteAssets(userId, assetsId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "false");
		}
		
		return resultMap;
	}
	
	// 예산 카테고리 작성 및 수정
	@PostMapping("/category/save")
	public Map<String, String> categorySave(
			@RequestParam("classification")String classification
			, @RequestParam("categoryName")String categoryName
			, @RequestParam("amount")int amount
			, @RequestParam("color") String color
			, @RequestParam("memo")String memo
			, @RequestParam("categoryId") Integer categoryId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		Category result = moneyService.addCategory(userId, classification, categoryName, amount, color, memo, categoryId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "false");
		}
		
		return resultMap;
	}
	
	// 예산 카테고리 삭제
	@DeleteMapping("/category/delete")
	public Map<String, String> cateogoryDelete(
			@RequestParam("categoryId")int categoryId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		boolean result = moneyService.deleteCategory(userId, categoryId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "false");
		}
		
		return resultMap;
	}
}

