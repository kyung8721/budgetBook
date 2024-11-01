package com.budgetBook.money;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
		String yearMonth = null;
		
		UserDto userDto = moneyService.callUserData(userId);
		
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth); // 월 구별
		// 카테고리
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserIdAndRealTimePredictionAndDate(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"), "지출");
		
		// 자산
		List<AssetsDto> assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		
		// 전체 예산 대비 내역 비율
		Map<String, Float> allProportionCategory = moneyService.allProportion(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		// 고정비 내역 불러오기
		List<FixedCostDto> fixedCostDtoList = moneyService.callFixedCost(userId);
		
		// 예측 내역
		List<BreakdownDto> breakdownDtoPreList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, 2, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		// 내역
		List<BreakdownDto> breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		
		model.addAttribute("user", userDto);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("assetsList", assetsDtoList);
		model.addAttribute("allProportion", allProportionCategory);
		model.addAttribute("fixedCostList", fixedCostDtoList);
		model.addAttribute("breakdownPreList", breakdownDtoPreList);
		model.addAttribute("breakdownList", breakdownDtoList);
		
		return "money/main";
	}
	
	// 메인 - 일부사용 모달
	@GetMapping("/main/detailModal-view")
	public String mainDetailModalView(HttpSession session, Model model
			, @RequestParam("breakdownId")int breakdownId) {
		int userId = (Integer)session.getAttribute("userId");
		String yearMonth = null; // null을 넣으면 현재 달 선택
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth); // 월 구별
		
		// 내역
		List<BreakdownDto> breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		
		model.addAttribute("breakdownList", breakdownDtoList);
		model.addAttribute("selectBreakdownId", breakdownId);
		
		return "money/maindetailModal";
	}
	
	// 고정비 작성 페이지
	@GetMapping("/fixedCost-view")
	public String fixedCostView(HttpSession session, Model model, @RequestParam(value="inputKeyword", required=false)String inputKeyword) {
		int userId = (Integer)session.getAttribute("userId");
		
		// 고정비 DTO 리스트 초기화
		List<FixedCostDto> fixedCostDtoList;
		if(inputKeyword != null) {
			// 검색어가 있으면 해당하는 리스트만 출력
			fixedCostDtoList = moneyService.searchFixedCost(userId, inputKeyword);
		}else {
			// 검색어가 없으면 전체 고정비 리스트 출력
			fixedCostDtoList = moneyService.callFixedCost(userId);
		}
		// 사용자 정보
		UserDto userDto = moneyService.callUserData(userId);
		
		
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
			, @RequestParam(value = "yearMonth", required = false)String yearMonth  // yearMonth : 20241001
			, @RequestParam(value = "inputKeyword", required = false)String inputKeyword) {
		int userId = (Integer)session.getAttribute("userId");
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth); // 월 구별
		
		// BreakdownDto List 초기화
		List<BreakdownDto> breakdownDtoList;
		if(inputKeyword != null) {
			// 검색 키워드가 있으면 검색 결과 출력
			breakdownDtoList = moneyService.searchBreakdown(userId, 1, inputKeyword , distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		}else{
			// 검색 키워드가 없으면 전체 내역 출력
			breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		}
		
		// 사용자 정보
		UserDto userDto = moneyService.callUserData(userId);
		
		// 총 수입 지출 차액 계산
		int incomeSum = moneyService.incomeSumService(userId, yearMonth, 1);
		int outGoingSum = moneyService.outGoingSumService(userId, yearMonth, 1);
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
			, @RequestParam(value = "yearMonth", required = false)String yearMonth
			, @RequestParam(value="categoryInputKeyword", required=false)String categoryInputKeyword
			, @RequestParam(value="breakdownInputKeyword", required=false)String breakdownInputKeyword) {
		int userId = (Integer)session.getAttribute("userId");
		UserDto userDto = moneyService.callUserData(userId);
		model.addAttribute("user", userDto);
		
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth); // 월 구별
		List<CategoryDto> categoryProportionList = moneyService.callCategoryDtoByUserIdAndRealTimePredictionAndDate(userId, 2, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"), "All");
		
		// 리스트 초기화
		List<CategoryDto> categoryDtoList;
		List<BreakdownDto> breakdownDtoList;
		
		// 검색 - 카테고리
		if(categoryInputKeyword != null) {
			// 카테고리 검색 키워드가 있으면 해당 리스트 출력
			categoryDtoList = moneyService.searchCategory(userId, categoryInputKeyword);
		}else{
			// 카테고리 검색 키워드가 없으면 전체 카테고리 리스트 출력
			categoryDtoList = moneyService.callCategoryDtoByUserId(userId);
		}
		
		// 검색 - 예측 사용 내역
		if(breakdownInputKeyword != null) {
			// 검색 키워드가 있으면 검색 결과 출력
			breakdownDtoList = moneyService.searchBreakdown(userId, 2, breakdownInputKeyword , distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		}else{
			// 검색 키워드가 없으면 전체 내역 출력
			breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndYearMonth(userId, 2, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		}
		
		
		// 전체 예산 대비 내역 비율
		Map<String, Float> allProportionCategory = moneyService.allProportion(userId, 2, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		model.addAttribute("user", userDto);
		model.addAttribute("breakdownList", breakdownDtoList);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("categoryProportionList", categoryProportionList);
		model.addAttribute("allProportion", allProportionCategory);
		
		return "money/budgetPrediction";
	}
	
	// 카테고리 추가 모달
	@GetMapping("/categoryModal")
	public String categoryModalView() {
		return "money/categoryModal";
	}
	
	// 카테고리 내역 클릭 시 나오는 모달
	@GetMapping("/categoryEditModal")
	public String categoryEditModalView(Model model, HttpSession session
			, @RequestParam("categoryId")int categoryId) {
		int userId = (Integer)session.getAttribute("userId");
		
		// 카테고리 내역 불러오기
		CategoryDto cateogoryDto = moneyService.callCategoryDto(categoryId);
		
		model.addAttribute("category", cateogoryDto);
		
		
		return "money/categoryEditModal";
	}
	
	// 세부 카테고리 추가 모달
	@GetMapping("/detailCategoryModal")
	public String detailCategoryModalView(Model model, HttpSession session) {
		
		int userId = (Integer)session.getAttribute("userId");
		
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserId(userId);
		model.addAttribute("categoryList", categoryDtoList);
		
		return "money/detailCategoryModal";
	}
	
	// 자산 페이지
	@GetMapping("/assets-view")
	public String assetView(HttpSession session, Model model
			, @RequestParam(value="inputKeyword", required=false)String inputKeyword) {
		int userId = (Integer)session.getAttribute("userId");
		
		// 초기화
		List<AssetsDto> assetsDtoList;
		// 검색어
		if(inputKeyword != null) {
			// 검색어가 있다면 해당 리스트 출력
			assetsDtoList = moneyService.searchAssets(userId, inputKeyword);
		}else {
			assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		}
		
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
	public String staticOutGoingView(HttpSession session, Model model
			, @RequestParam(value = "yearMonth", required = false) String yearMonth
			, @RequestParam(value = "categoryId", required = false)Integer categoryId) {
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		// 카테고리 별 지출 내역 가져오기
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth); // 월 구별
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserIdAndRealTimePredictionAndDate(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"), "지출");
		
		if(categoryId == null) {
			categoryId = 0;
		}
		// 지출 내역 가져오기
		List<BreakdownDto> breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndClassificationAndYearMonthAndCategoryId(userId, "지출", 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"), categoryId);
		
		model.addAttribute("user", userDto);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("breakdownList", breakdownDtoList);
		return "money/staticOutGoingView";
	}
	
	// 통계 - 수입 페이지
	@GetMapping("/statics/income-view")
	public String staticinComeView(HttpSession session, Model model
			, @RequestParam(value = "yearMonth", required = false) String yearMonth
			, @RequestParam(value = "categoryId", required = false)Integer categoryId) {
		
		int userId = (Integer)session.getAttribute("userId");
		
		UserDto userDto = moneyService.callUserData(userId);
		
		// 카테고리 별 수입 내역 가져오기
		Map<String, LocalDateTime> distinguishMonthMap = moneyService.distinguishMonth(userId, yearMonth); // 월 구별
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserIdAndRealTimePredictionAndDate(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"), "수입");
		
		if(categoryId == null) {
			categoryId = 0;
		}
		// 수입 내역 가져오기
		List<BreakdownDto> breakdownDtoList = moneyService.callBreakdownDtoByUserIdAndClassificationAndYearMonthAndCategoryId(userId, "수입", 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"), categoryId);
		
		model.addAttribute("user", userDto);
		model.addAttribute("categoryList", categoryDtoList);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("breakdownList", breakdownDtoList);
		
		return "money/staticinComeView";
	}
}
