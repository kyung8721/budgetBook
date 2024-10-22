package com.budgetBook.money;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
		
		// 내역
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth);
		List<BreakdownDto> breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, 1, distinguishMonthMap.get("selectMonth"), distinguishMonthMap.get("nextMonth"));
		
		// 사용자 정보
		UserDto userDto = moneyService.callUserData(userId);
		
		// 총 수입 지출 차액 계산
		int incomeSum = moneyService.incomeSumService(userId, yearMonth);
		int outGoingSum = moneyService.outGoingSumService(userId, yearMonth);
		int difference = incomeSum - outGoingSum;
		
		model.addAttribute("user", userDto);
		model.addAttribute("breakdownList", breakdownDtoList);
		model.addAttribute("incomeSum", incomeSum);
		model.addAttribute("outGoingSum", outGoingSum);
		model.addAttribute("difference", difference);
		
		return "money/detailView";
	}
	

	// 상세 내역 모달(내역 작성)
	@GetMapping("/detailModal")
	public String detailListView(Model model, HttpSession session, @RequestParam("realTimePrediction")int realTimePrediction) {
		
		int userId = (Integer)session.getAttribute("userId");
		
		
		List<AssetsDto> assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserId(userId);
		List<DetailCategoryDto> detailCategoryDtoList = moneyService.callDetailCategoryDtoList(userId);
		
		model.addAttribute("assetsList", assetsDtoList);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("detailCategoryList", detailCategoryDtoList);
		model.addAttribute("realTimePrediction", realTimePrediction);
		
		return "money/detail";
	}
	
	// 상세 내역 클릭시 모달
	@GetMapping("/detailEditModal")
	public String detailModalEditView(Model model, HttpSession session
			, @RequestParam("breakdownId")int breakdownId
			, @RequestParam("realTimePrediction")int realTimePrediction) {
		
		int userId = (Integer)session.getAttribute("userId");
		
		// 내역 불러오기
		BreakdownDto breakdownDto = moneyService.callBreakdownById(userId, breakdownId);
		
		List<AssetsDto> assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserId(userId);
		List<DetailCategoryDto> detailCategoryDtoList = moneyService.callDetailCategoryDtoList(userId);
		
		model.addAttribute("assetsList", assetsDtoList);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("detailCategoryList", detailCategoryDtoList);
		model.addAttribute("breakdown", breakdownDto);
		model.addAttribute("realTimePrediction", realTimePrediction);
		
		return "money/detailEditModal";
	}
	
	// 예산 예측 페이지
	@GetMapping("/budgetPrediction-view")
	public String budgetPredictionView(HttpSession session, Model model
			, @RequestParam(value = "yearMonth", required = false)String yearMonth) {
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		model.addAttribute("user", userDto);
		
		
		// 내역
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth);
		List<BreakdownDto> breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, 2, distinguishMonthMap.get("selectMonth"), distinguishMonthMap.get("nextMonth"));
		
		
		model.addAttribute("user", userDto);
		model.addAttribute("breakdownList", breakdownDtoList);
		
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
