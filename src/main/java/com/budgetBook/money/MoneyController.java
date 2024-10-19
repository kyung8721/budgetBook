package com.budgetBook.money;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.budgetBook.money.dto.AssetsDto;
import com.budgetBook.money.dto.BreakdownDto;
import com.budgetBook.money.dto.CategoryDto;
import com.budgetBook.money.dto.DetailCategoryDto;
import com.budgetBook.money.dto.FixedCostDto;
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
	
	// 메인 화면
	@GetMapping("/main-view")
	public String mainView(HttpSession session, Model model) {
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		return "money/main";
	}
	
	// 고정비 작성 페이지
	@GetMapping("/fixedCost-view")
	public String fixedCostView(HttpSession session, Model model) {
		int userId = (Integer)session.getAttribute("userId");
		
		// 사용자 정보
		UserDto userDto = moneyService.callUserData(userId);
		
		// 고정비 내역 불러오기
		List<FixedCostDto> fixedCostDtoList = moneyService.callFixedCost(userId);
		
		
		model.addAttribute("user", userDto);
		model.addAttribute("fixedCostList", fixedCostDtoList);
		
		
		return "money/fixedCost";
	}
	
	// 상세 내역 모달
	@GetMapping("/detailModal")
	public String detailListView() {
		return "money/detail";
	}
	
	// 고정비 작성 모달
	@GetMapping("/fixedCostModal")
	public String fixedCostModalView(Model model, HttpSession session) {
		int userId = (Integer)session.getAttribute("userId");
		
		
		List<AssetsDto> assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserId(userId);
		List<DetailCategoryDto> detailCategoryDtoList = moneyService.callDetailCategoryDtoList(userId);
		
		model.addAttribute("assetsList", assetsDtoList);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("detailCategoryList", detailCategoryDtoList);
		
		return "money/fixedCostModal";
	}
	
	// 고정비 내역 클릭 시 모달(고정비 수정 가능)
	@GetMapping("/fixedCostEditModal")
	public String fixedCostEditModalView(
			@RequestParam("fixedCostId") int fixedCostId 
			, Model model, HttpSession session) {
		int userId = (Integer)session.getAttribute("userId");
		
		// 해당 고정비 정보 불러오기
		FixedCostDto fixedCostDto = moneyService.callFixedCostDtoById(fixedCostId);
		
		
		List<AssetsDto> assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserId(userId);
		List<DetailCategoryDto> detailCategoryDtoList = moneyService.callDetailCategoryDtoList(userId);
		
		model.addAttribute("assetsList", assetsDtoList);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("detailCategoryList", detailCategoryDtoList);
		model.addAttribute("fixedCost", fixedCostDto);
		
		return "money/fixedCostEditModal";
	}
	
	// 내역 페이지
	@GetMapping("/breakdown-view")
	public String breakdownView(HttpSession session, Model model
			, @RequestParam(value = "yearMonth", required = false)String yearMonth) { // yearMonth : 20241001
		int userId = (Integer)session.getAttribute("userId");
		LocalDateTime localDateTime = null; // 선택된 달
		String NextMonth; // 선택된 달 + 1;
		LocalDateTime nextMonthlocalDateTime = null; // 다음달 LocalDateTime
		
		if(yearMonth == null) {
			// 선택된 달이 없을 때
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			yearMonth = Integer.toString(now.getYear()) + "-" + now.getMonthValue() + "-01"; // 2024-10-01
			
			if(now.getMonthValue() == 12) {
				// 12월일 때
				NextMonth =  Integer.toString(now.getYear() + 1) +"-" + (1) + "-01";
			}else{
				NextMonth =  Integer.toString(now.getYear()) +"-" + (now.getMonthValue() + 1) + "-01";
			}
			localDateTime = LocalDateTime.parse(yearMonth, formatter);
			nextMonthlocalDateTime = LocalDateTime.parse(NextMonth, formatter);
		}else {
			// 선택된 달이 있을 때
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String yearMonthFormat = yearMonth.substring(0,4) + "-" + yearMonth.substring(4,6) + "-01";
			String NextMonthFormat;
			
			if(Integer.parseInt(yearMonth.substring(4,6)) == 12) {
				// 12월일 때
				NextMonthFormat = Integer.toString((Integer.parseInt(yearMonth.substring(0,4))+1)) + "-01" + "-01";
			}else{
				NextMonthFormat = yearMonth.substring(0,4) + "-" + Integer.toString((Integer.parseInt(yearMonth.substring(4,6))+1)) + "-01";
			}
			
			localDateTime = LocalDateTime.parse(yearMonthFormat, formatter);
			nextMonthlocalDateTime = LocalDateTime.parse(NextMonthFormat, formatter);
		}
		
		List<BreakdownDto> breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, localDateTime, nextMonthlocalDateTime);
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		model.addAttribute("breakdownList", breakdownDtoList);
		
		return "money/detailView";
	}
	
	// 예산 예측 페이지
	@GetMapping("/budgetPrediction-view")
	public String budgetPredictionView(HttpSession session, Model model) {
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		
		return "money/budgetPrediction";
	}
	
	// 카테고리 추가 모달
	@GetMapping("/categoryModal")
	public String categoryModalView() {
		return "money/categoryModal";
	}
	
	// 자산 페이지
	@GetMapping("/assets-view")
	public String assetView(HttpSession session, Model model) {
		int userId = (Integer)session.getAttribute("userId");
		
		// 자산 DTO
		List<AssetsDto> assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		model.addAttribute("assetsList", assetsDtoList);
		
		return "money/assetView";
	}
	
	// 자산 추가 모달 페이지
	@GetMapping("/assetModal")
	public String assetModalView() {
		
		return "money/assetModal";
	}
	
	// 자산 내역 클릭 시 모달(자산 수정 가능)
	@GetMapping("/assetEditModal")
	public String assetEditModalView(@RequestParam("assetsId")int assetsId, HttpSession session, Model model) {
		int userId = (Integer)session.getAttribute("userId");
		
		AssetsDto assetsDto = moneyService.callAssetsDto(assetsId);
		model.addAttribute("assets", assetsDto);
			
		return "money/assetEditModal";
	}
	
	// 통계 - 지출 페이지
	@GetMapping("/statics/outgoing-view")
	public String staticOutGoingView(HttpSession session, Model model) {
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		
		return "money/staticOutGoingView";
	}
	
	// 통계 - 수입 페이지
	@GetMapping("/statics/income-view")
	public String staticinComeView(HttpSession session, Model model) {
		
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		
		return "money/staticinComeView";
	}
}
