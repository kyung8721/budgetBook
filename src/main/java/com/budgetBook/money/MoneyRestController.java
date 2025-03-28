package com.budgetBook.money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budgetBook.money.domain.Assets;
import com.budgetBook.money.domain.Breakdown;
import com.budgetBook.money.domain.Category;
import com.budgetBook.money.domain.DetailCategory;
import com.budgetBook.money.domain.FixedCost;
import com.budgetBook.money.dto.AssetsDto;
import com.budgetBook.money.dto.CategoryDto;
import com.budgetBook.money.dto.DetailCategoryDto;
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
			, @RequestParam(value = "categoryId", required = false) Integer categoryId
			, @RequestParam(value = "detailCategoryId") Integer detailCategoryId
			, @RequestParam("fixedCostName") String fixedCostName
			, @RequestParam("fixedCost") int fixedCost
			, @RequestParam(value = "memo") String memo
			, @RequestParam(value = "fixedCostId", required = false) Integer fixedCostId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		if(categoryId == 0) {
			categoryId = null;
		}
		
		if(detailCategoryId == 0) {
			detailCategoryId = null;
		}
		FixedCost result = moneyService.saveFixedCost(userId, classification, period, assetsId, categoryId, detailCategoryId, fixedCostName, fixedCost, memo, fixedCostId);
		
		
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
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
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 자산 생성 및 수정
	@PostMapping("/assets/save")
	public Map<String, String> assetsSave(
			@RequestParam("assetsName")String assetsName
			, @RequestParam("balance")int balance
			, @RequestParam("color")String color
			, @RequestParam(value = "memo", required = false)String memo
			, @RequestParam(value = "assetsId", required = false) Integer assetsId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		Assets result = moneyService.saveAssets(userId, assetsName, balance, color, memo, assetsId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
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
			resultMap.put("result", "fail");
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
			, @RequestParam(value = "memo", required = false)String memo
			, @RequestParam(value = "categoryId", required = false) Integer categoryId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		Category result = moneyService.saveCategory(userId, classification, categoryName, amount, color, memo, categoryId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
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
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 세부 예산 카테고리 생성, 수정
	@PostMapping("/detailCategory/create")
	public Map<String, String> detailCategoryCreate(
			@RequestParam("categoryId") int categoryId
			, @RequestParam("detailCategoryName") String detailCategoryName
			, @RequestParam(value = "memo", required = false) String memo
			, @RequestParam(value = "detailCategoryId", required = false) Integer detailCategoryId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		DetailCategory result = moneyService.saveDetailCategory(userId, categoryId, detailCategoryName, memo, detailCategoryId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 세부 예산 카테고리 삭제
	@DeleteMapping("/detailCategory/delete")
	public Map<String, String> detailCategoryDelete(
			@RequestParam("detailCategoryId")int detailCategoryId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		boolean result = moneyService.deleteDetailCategory(userId, detailCategoryId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 내역 작성 및 수정(실제 사용내역 및 예측 사용내역)
	@PostMapping("/breakdown/create")
	public Map<String, String> breakdownSave(
			@RequestParam("realTimePrediction") int realTimePrediction
			, @RequestParam("classification") String classification
			, @RequestParam("date") String date
			, @RequestParam("assetsId") int assetsId
			, @RequestParam(value = "categoryId", required = false)Integer categoryId
			, @RequestParam(value = "detailCategoryId", required = false)Integer detailCategoryId
			, @RequestParam("breakdownName")String breakdownName
			, @RequestParam("cost")int cost
			, @RequestParam(value = "memo", required = false)String memo
			, @RequestParam(value = "breakdownId", required = false)Integer breakdownId
			, @RequestParam(value = "memoImagePath", required = false)String memoImagePath
			, HttpSession session){
		
		int userId = (Integer)session.getAttribute("userId");
		
		// 날짜 String to LocalDate
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // LocalDate 날짜 패턴 지정
		LocalDateTime selectDate = LocalDate.parse(date, formatter).atStartOfDay();
		
		// 0으로 들어온 id를 null로 바꿔주기
		if(categoryId == 0) {
			categoryId = null;
		}
		
		if(detailCategoryId == 0) {
			detailCategoryId = null;
		}
		
		Breakdown result = moneyService.saveBreakdown(userId, realTimePrediction, classification, selectDate, assetsId, categoryId, detailCategoryId, breakdownName, cost, memo, memoImagePath, breakdownId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		if(result != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 내역 삭제(실제 사용내역 및 예측 사용내역)
	@DeleteMapping("/breakdown/delete")
	public Map<String, String> breakdownDelete(
			@RequestParam("breakdownId")int breakdownId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		boolean result = moneyService.deleteBreakdown(userId, breakdownId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 내역 예측 수정 - 자산
	@PutMapping("/breakdown/assets/update")
	public Map<String, String> breakdownAssetsUpdate(
			@RequestParam("assetsId") int assetsId
			, @RequestParam("breakdownId") int breakdownId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		Breakdown breakdown = moneyService.saveBreakdownbyAssetsId(userId, breakdownId, assetsId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(breakdown != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 내역 예측 수정 - 카테고리
	@PutMapping("/breakdown/category/update")
	public Map<String, String> breakdownCategoryUpdate(
			@RequestParam("categoryId") int categoryId
			, @RequestParam("breakdownId") int breakdownId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		Breakdown breakdown = moneyService.saveBreakdownbyCategoryId(userId, breakdownId, categoryId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(breakdown != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 고정비 예측 수정 - 자산
	@PutMapping("/fixedCost/assets/update")
	public Map<String, String> fixedCostAssetsUpdate(
			@RequestParam("assetsId") int assetsId
			, @RequestParam("fixedCostId") int fixedCostId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		FixedCost fixedCost = moneyService.saveFixedCostbyAssetsId(userId, fixedCostId, assetsId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(fixedCost != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// DB에 내역 예측에서 선택된 사용 완료한 내역 저장 
	@PostMapping("/breakdown/select")
	public Map<String, String> breakdownSelect(
			@RequestParam("selectBreakdowns")int[] selectBreakdowns
			, @RequestParam("breakdownId")int breakdownId){
		
		boolean result = moneyService.partUseSave(selectBreakdowns, breakdownId);
		
		Map<String, String> resultMap = new HashMap<>();
		
		
		if(result) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 고정비 예측 수정 - 카테고리
	@PutMapping("/fixedCost/category/update")
	public Map<String, String> fixedCostCategoryUpdate(
			@RequestParam("categoryId") int categoryId
			, @RequestParam("fixedCostId") int fixedCostId
			, HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		FixedCost fixedCost = moneyService.saveFixedCostbyCategoryId(userId, fixedCostId, categoryId);
		
		Map<String, String> resultMap = new HashMap<>();

		if(fixedCost != null) {
			resultMap.put("result", "success");
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 차트 데이터 보내기
	@PostMapping("/chart/data")
	public Map<String, List<Map<String, Object>>> chartData(HttpSession session
			, @RequestParam("categoryId")Integer categoryId
			, @RequestParam("classification")String classification
			, @RequestParam(value = "yearMonth", required = false)String yearMonth){
		int userId = (Integer)session.getAttribute("userId");
		
		List<Map<String, Object>> resultList = moneyService.chartDataService(categoryId, userId, classification, yearMonth);
		
		Map<String, List<Map<String, Object>>> resultMap = new HashMap<>();
		if(resultList != null) {
			resultMap.put("result", resultList);
		}else {
			resultMap.put("result", null);
		}
		
		return resultMap;
	}
	
	// 달력에 표시할 수입, 지출 내역
	@PostMapping("/calendar/dayList")
	public List<Map<String, String>> dayList(HttpSession session
			, @RequestParam("startDate")String startDate
			, @RequestParam("endDate")String endDate){
		int userId = (Integer)session.getAttribute("userId");
		
		// 날짜를 LocalDateTime으로 변경
		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.ZZZZZ");
		LocalDateTime startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		LocalDateTime endDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		
		// 해당 달의 내역 합계 불러오기
		List<Map<String, String>> result = moneyService.calculateDayList(userId, 1, startDateTime, endDateTime);
		
		return result;
		
	}
	
	// 카테고리 리스트 새로고침
	@GetMapping("/detail/categoryList")
	public Map<String, Object> reloadCategoryDto(HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		List<CategoryDto> categoryDtoList = moneyService.callCategoryDtoByUserId(userId);
		
		
		
		Map<String, Object> resultMap = new HashMap<>();
		if(categoryDtoList != null) {
			resultMap.put("result", "success");
			resultMap.put("categories", categoryDtoList);
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 세부 카테고리 리스트 새로고침
	@GetMapping("/detail/detailCategoryList")
	public Map<String, Object> reloadDetailCategoryDto(HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		List<DetailCategoryDto> detailCategoryDtoList = moneyService.callDetailCategoryDtoList(userId);
		
		
		
		Map<String, Object> resultMap = new HashMap<>();
		if(detailCategoryDtoList != null) {
			resultMap.put("result", "success");
			resultMap.put("detailCategories", detailCategoryDtoList);
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	
	// 자산 리스트 새로고침
	@GetMapping("/detail/assetsList")
	public Map<String, Object> reloadAssetsDto(HttpSession session){
		int userId = (Integer)session.getAttribute("userId");
		
		List<AssetsDto> assetsDtoList = moneyService.callAssetsDtoByUserId(userId);
		
		
		
		Map<String, Object> resultMap = new HashMap<>();
		if(assetsDtoList != null) {
			resultMap.put("result", "success");
			resultMap.put("assets", assetsDtoList);
		}else {
			resultMap.put("result", "fail");
		}
		
		return resultMap;
	}
	

	
}
