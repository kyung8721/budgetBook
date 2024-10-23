package com.budgetBook.money.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.budgetBook.money.domain.Assets;
import com.budgetBook.money.domain.Breakdown;
import com.budgetBook.money.domain.Category;
import com.budgetBook.money.domain.DetailCategory;
import com.budgetBook.money.domain.FixedCost;
import com.budgetBook.money.dto.AssetsDto;
import com.budgetBook.money.dto.BreakdownDto;
import com.budgetBook.money.dto.CategoryDto;
import com.budgetBook.money.dto.DetailCategoryDto;
import com.budgetBook.money.dto.FixedCostDto;
import com.budgetBook.money.repository.AssetsRepository;
import com.budgetBook.money.repository.BreakdownRepository;
import com.budgetBook.money.repository.CategoryRepository;
import com.budgetBook.money.repository.DetailCategoryRepository;
import com.budgetBook.money.repository.FixedCostRepository;
import com.budgetBook.user.dto.UserDto;
import com.budgetBook.user.service.UserService;

@Service
public class MoneyService {
	
	private FixedCostRepository fixedCostRepository;
	private UserService userService;
	private AssetsRepository assetsRepository;
	private CategoryRepository categoryRepository;
	private DetailCategoryRepository detailCategoryRepository;
	private BreakdownRepository breakdownRepository;
	
	public MoneyService(
			FixedCostRepository fixedCostRepository
			, UserService userService
			, AssetsRepository assetsRepository
			, CategoryRepository categoryRepository
			, DetailCategoryRepository detailCategoryRepository
			, BreakdownRepository breakdownRepository) {
		this.fixedCostRepository = fixedCostRepository;
		this.userService = userService;
		this.assetsRepository = assetsRepository;
		this.categoryRepository = categoryRepository;
		this.detailCategoryRepository = detailCategoryRepository;
		this.breakdownRepository = breakdownRepository;
	}
	
	
	// 고정비 저장 및 수정
	public FixedCost saveFixedCost(FixedCost fixedCostObject) {
		return fixedCostRepository.save(fixedCostObject);
	}
	
	// 고정비 삭제
	public boolean deleteFixedCost(int userId, int fixedCostId) {
		Optional<FixedCost> optionalFixedCost = fixedCostRepository.findById(fixedCostId);
		FixedCost fixedCost = optionalFixedCost.orElse(null);
		
		// 사용자와 삭제할 고정비의 작성자가 같으면 삭제 진행  
		if(userId == fixedCost.getUserId()) {
			fixedCostRepository.delete(fixedCost);
			return true;
		}else {
			return false;
		}
	}
	
	// 유저 정보 불러오기
	public UserDto callUserData(int userId) {
		return userService.userData(userId);
	}
	
	// fixedCost -> fixedCostDto
	public FixedCostDto fixedCostToDto(FixedCost i) {
		String period;
		// 반복주기
		if(i.getPeriod().startsWith("M")) {
			// 매월
			period = "매월" + i.getPeriod().substring(1);
		}else if(i.getPeriod().startsWith("W")) {
			// 매주
			String periodWeek = "";
			switch(i.getPeriod().substring(1)) {
			case "Mon":
				periodWeek = "월요일";
				break;
			case "Tue":
				periodWeek = "화요일";
				break;
			case "Wed":
				periodWeek = "수요일";
				break;
			case "Thu":
				periodWeek = "목요일";
				break;
			case "Fri":
				periodWeek = "금요일";
				break;
			case "Sat":
				periodWeek = "토요일";
				break;
			case "Sun":
				periodWeek = "일요일";
				break;
			};
			period = "매주" + periodWeek;
		}else if(i.getPeriod().startsWith("Daily")) {
			// 매일
			period = "매일";
		}else {
			// 오류
			return null;
		}
		// 자산명
		AssetsDto assetsDto = callAssetsDto(i.getAssetsId());
		String assetsName = assetsDto.getAssetsName();
		
		// 카테고리명
		String categoryName;
		if(i.getCategoryId() != null) {
			CategoryDto categoryDto = callCategoryDto(i.getCategoryId());
			categoryName = categoryDto.getCategoryName();
		}else {
			categoryName = null;
		}
		
		
		// 세부 카테고리명
		String detailCategoryName;
		if(i.getDetailCategoryId() != null) {
			DetailCategoryDto detailCategoryDto = callDetailCategoryDto(i.getDetailCategoryId());
			detailCategoryName = detailCategoryDto.getDetailCategoryName();
		}else {
			detailCategoryName = null;
		}
		
		
		FixedCostDto fixedCostDto = FixedCostDto.builder()
				.id(i.getId())
				.userId(i.getUserId())
				.classification(i.getClassification())
				.period(period)
				.assetsName(assetsName)
				.categoryName(categoryName)
				.detailCategoryName(detailCategoryName)
				.fixedCostName(i.getFixedCostName())
				.fixedCost(i.getFixedCost())
				.memo(i.getMemo())
				.build();
		
		return fixedCostDto;
	}
	
	// fixedCostId로 특정 고정비 내역 불러오기
	public FixedCostDto callFixedCostDtoById(int id) {
		
		Optional<FixedCost> optionalFixedCost = fixedCostRepository.findById(id);
		FixedCost fixedCost = optionalFixedCost.orElse(null) ;
		
		if(fixedCost != null) {
			FixedCostDto fixedCostDto = fixedCostToDto(fixedCost);
			return fixedCostDto;
		}else {
			return null;
		}
		
	}
	
	
	// 사용자의 고정비 내역 불러오기
	public List<FixedCostDto> callFixedCost(int userId) {
		List<FixedCost> fixedCostList = fixedCostRepository.findAllByUserId(userId);
		
		List<FixedCostDto> fixedCostDtoList = new ArrayList<>();
		
		if(fixedCostList !=null) {
			for(FixedCost i : fixedCostList) {
				FixedCostDto fixedCostDto = fixedCostToDto(i);
				
				fixedCostDtoList.add(fixedCostDto);
			}
		
		}else {
			fixedCostDtoList = null;
		}
		
		return fixedCostDtoList;
	}
	
	// 자산 추가 및 수정
	public Assets saveAssets(int userId, String assetsName, int balance, String color, String memo, Integer assetsId) {
		Assets assets;
		if(assetsId == null) {
			assets = Assets.builder()
					.userId(userId)
					.assetsName(assetsName)
					.balance(balance)
					.color(color)
					.memo(memo)
					.build();
		}else {
			// 수정 시 userId와 해당 자산의 userId가 동일한지 검사
			Optional<Assets> optionalAssets = assetsRepository.findById(assetsId);
			Assets assetsCheck = optionalAssets.orElse(null);
			if(userId == assetsCheck.getUserId()) {
				assets = Assets.builder()
						.id(assetsId)
						.userId(userId)
						.assetsName(assetsName)
						.balance(balance)
						.color(color)
						.memo(memo)
						.build();
			}else {
				assets = null;
			}
		}
		
		return assetsRepository.save(assets);
	}
	
	// 자산 삭제
	public boolean deleteAssets(int userId, int assetsId) {
		Optional<Assets> optionalAssets = assetsRepository.findById(assetsId);
		Assets assets = optionalAssets.orElse(null);
		
		if(userId == assets.getUserId()){
			// 사용자와 자산 작성자의 id가 동일하면 진행
			assetsRepository.delete(assets);
			return true;
		}else {
			return false;
		}
	}
	
	// 자산 DTO로 불러오기
	public AssetsDto callAssetsDto(int id) {
		Optional<Assets> optionalAssets = assetsRepository.findById(id);
		Assets assets = optionalAssets.orElse(null);
		
		// 자산 차이 계산
		String balanceDifference;
		
		if(assets.getLastBalance() != null) {
			int intBalanceDifference = assets.getBalance() - assets.getLastBalance();
			if(intBalanceDifference < 0) {
				balanceDifference = "감소" + Math.abs(intBalanceDifference);
			}else if(intBalanceDifference > 0){
				balanceDifference = "증가" + intBalanceDifference;
			}else {
				balanceDifference = "변함없음";
			}
		}else {
			balanceDifference = null;
		}
		
		AssetsDto assetsDto = AssetsDto.builder()
				.id(id)
				.userId(assets.getUserId())
				.assetsName(assets.getAssetsName())
				.balance(assets.getBalance())
				.lastBalance(assets.getLastBalance())
				.balanceDifference(balanceDifference)
				.color(assets.getColor())
				.memo(assets.getMemo())
				.build();
		return assetsDto;
	}
	
	// 자산 DTO UserId로 불러오기
	public List<AssetsDto> callAssetsDtoByUserId(int userId) {
		List<Assets> assetsList = assetsRepository.findAllByUserId(userId);
		
		
		
		List<AssetsDto> assetsDtoList = new ArrayList<>();
		if(assetsList != null) {
			for(Assets i : assetsList) {
				
				// 자산 차이 계산
				String balanceDifference;
				
				if(i.getLastBalance() != null) {
					int intBalanceDifference = i.getBalance() - i.getLastBalance();
					if(intBalanceDifference < 0) {
						balanceDifference = "감소" + Math.abs(intBalanceDifference);
					}else if(intBalanceDifference > 0){
						balanceDifference = "증가" + intBalanceDifference;
					}else {
						balanceDifference = "변함없음";
					}
				}else {
					balanceDifference = null;
				}
				
				AssetsDto assetsDto = AssetsDto.builder()
						.id(i.getId())
						.userId(userId)
						.assetsName(i.getAssetsName())
						.balance(i.getBalance())
						.lastBalance(i.getLastBalance())
						.balanceDifference(balanceDifference)
						.color(i.getColor())
						.memo(i.getMemo())
						.build();
				assetsDtoList.add(assetsDto);
			}
			
		}else {
			assetsDtoList = null;
		}
		
		return assetsDtoList;
	}
	
	// 예산 카테고리 작성 및 수정
	public Category saveCategory(int userId, String classification, String categoryName, int amount, String color, String memo, Integer categoryId) {
		Category category;
		if(categoryId == null) {
			category = Category.builder()
					.userId(userId)
					.classification(classification)
					.categoryName(categoryName)
					.amount(amount)
					.color(color)
					.memo(memo)
					.build();
		}else {
			category = Category.builder()
					.id(categoryId)
					.userId(userId)
					.classification(classification)
					.categoryName(categoryName)
					.amount(amount)
					.color(color)
					.memo(memo)
					.build();
		}
		
		return categoryRepository.save(category);
		
	}
	
	// 예산 카테고리 삭제
	public boolean deleteCategory(int userId, int categoryId){
		Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
		Category category = optionalCategory.orElse(null);
		
		if(userId == category.getUserId()){
			// 사용자와 예산 카테고리 작성자의 id가 동일하면 진행
			categoryRepository.delete(category);
			return true;
		}else {
			return false;
		}
	}
	
	// 예산 카테고리 Dto 불러오기
	public CategoryDto callCategoryDto(int id) {
		Optional<Category> optionalCategory = categoryRepository.findById(id);
		Category category = optionalCategory.orElse(null);
		CategoryDto categoryDto = CategoryDto.builder()
				.id(id)
				.userId(category.getUserId())
				.classification(category.getClassification())
				.categoryName(category.getCategoryName())
				.amount(category.getAmount())
				.color(category.getColor())
				.memo(category.getMemo())
				.build();
		return categoryDto;
	}
	
	// 예산 카테고리 DTO userId로 불러오기
	public List<CategoryDto> callCategoryDtoByUserId(int userId) {
		List<Category> categoryList = categoryRepository.findAllByUserId(userId);
		List<CategoryDto> categoryDtoList = new ArrayList<>();
		
		if(categoryList != null) {
			for(Category i : categoryList) {
				
				// DTO에 저장
				CategoryDto categoryDto = CategoryDto.builder()
						.id(i.getId())
						.userId(userId)
						.classification(i.getClassification())
						.categoryName(i.getCategoryName())
						.amount(i.getAmount())
						.color(i.getColor())
						.memo(i.getMemo())
						.build();
				categoryDtoList.add(categoryDto);
			}
			
		}else {
			categoryDtoList = null;
		}
		
		return categoryDtoList;
	}
	
	// 카테고리 DTO userId로 불러오기 - proportion(사용한 내역 / 카테고리 예산 비율)
	public List<CategoryDto> callCategoryDtoByUserIdAndRealTimePredictionAndDate(int userId, int realTimePrediction, LocalDateTime selectMonth, LocalDateTime nextMonth){
		List<CategoryDto> categoryDtoList = callCategoryDtoByUserId(userId);
		
		for(CategoryDto i : categoryDtoList) {
			float proportion = categoryProportion(i.getId(), realTimePrediction, selectMonth, nextMonth);
			i.setProportion(proportion);
		}
		
		return categoryDtoList;
		
	}
	
	// 세부 예산 카테고리 작성 및 수정
	public DetailCategory saveDetailCategory(int userId, int categoryId, String detailCategoryName, String memo, Integer detailCategoryId) {
		DetailCategory detailCategory;
		if(detailCategoryId == null) {
			detailCategory = DetailCategory.builder()
					.userId(userId)
					.categoryId(categoryId)
					.detailCategoryName(detailCategoryName)
					.memo(memo)
					.build();
		}else {
			detailCategory = DetailCategory.builder()
					.id(detailCategoryId)
					.userId(userId)
					.categoryId(categoryId)
					.detailCategoryName(detailCategoryName)
					.memo(memo)
					.build();
		}
		
		return detailCategory;
	}
	
	// 세부 예산 카테고리 삭제
	public boolean deleteDetailCategory(int userId, int detailCategoryId) {
		Optional<DetailCategory> optionalDetailCategory = detailCategoryRepository.findById(detailCategoryId);
		DetailCategory detailCategory = optionalDetailCategory.orElse(null);
		
		if(userId == detailCategory.getUserId()){
			// 사용자와 예산 카테고리 작성자의 id가 동일하면 진행
			detailCategoryRepository.delete(detailCategory);
			return true;
		}else {
			return false;
		}
	}
	
	// 세부 예산 카테고리 Dto 조회
	public DetailCategoryDto callDetailCategoryDto(int id) {
		Optional<DetailCategory> optionalDetailCategory = detailCategoryRepository.findById(id);
		DetailCategory detailCategory = optionalDetailCategory.orElse(null);
		
		CategoryDto categoryDto = callCategoryDto(detailCategory.getCategoryId());
		
		DetailCategoryDto detailCategoryDto = DetailCategoryDto.builder()
				.id(id)
				.userId(detailCategory.getUserId())
				.categoryName(categoryDto.getCategoryName())
				.detailCategoryName(detailCategory.getDetailCategoryName())
				.memo(detailCategory.getMemo())
				.build();
		return detailCategoryDto;
	}
	
	// 세부 예산 카테고리 DTO userId로 조회
	public List<DetailCategoryDto> callDetailCategoryDtoList(int userId){
		List<DetailCategory> detailCategoryList = detailCategoryRepository.findAllByUserId(userId);
		List<DetailCategoryDto> detailCategoryDtoList = new ArrayList<>();
		DetailCategoryDto detailCategoryDto;
		
		if(detailCategoryList != null) {
			for(DetailCategoryDto i : detailCategoryDtoList) {
				detailCategoryDto = DetailCategoryDto.builder()
						.id(i.getId())
						.userId(userId)
						.categoryName(i.getCategoryName())
						.detailCategoryName(i.getDetailCategoryName())
						.memo(i.getMemo())
						.build();
				detailCategoryDtoList.add(detailCategoryDto);
			}
		}else {
			detailCategoryDtoList = null;
		}
		return detailCategoryDtoList;
	}
	
	// 내역 작성 및 수정(실제 사용내역 및 예측 사용내역)
	public Breakdown saveBreakdown(int userId, int RealTimePrediction, String classification, LocalDateTime date, int assetsId, Integer categoryId, Integer detailCategoryId, String breakdownName, int cost, String memo, String memoImagePath, Integer breakdownId) {
		Breakdown breakdown;
		if(breakdownId == null) {
			breakdown = Breakdown.builder()
					.userId(userId)
					.realTimePrediction(RealTimePrediction)
					.classification(classification)
					.date(date)
					.assetsId(assetsId)
					.categoryId(categoryId)
					.detailCategoryId(detailCategoryId)
					.breakdownName(breakdownName)
					.cost(cost)
					.memoImagePath(memoImagePath)
					.memo(memo)
					.build();
		}else {
			breakdown = Breakdown.builder()
					.id(breakdownId)
					.userId(userId)
					.realTimePrediction(RealTimePrediction)
					.classification(classification)
					.date(date)
					.assetsId(assetsId)
					.categoryId(categoryId)
					.detailCategoryId(detailCategoryId)
					.breakdownName(breakdownName)
					.cost(cost)
					.memoImagePath(memoImagePath)
					.memo(memo)
					.build();
		}
		
		return breakdownRepository.save(breakdown);
	}
	
	// 내역 삭제(실제 사용내역 및 예측 사용내역)
	public boolean deleteBreakdown(int userId, int breakdownId) {
		Optional<Breakdown> optionalBreakdown = breakdownRepository.findById(breakdownId);
		Breakdown breakdown = optionalBreakdown.orElse(null);
		
		if(userId == breakdown.getUserId()){
			// 사용자와 예산 카테고리 작성자의 id가 동일하면 진행
			breakdownRepository.delete(breakdown);
			return true;
		}else {
			return false;
		}
	}
	
	// 내역 조회(해당 달 정보만)
	public List<BreakdownDto> callBreakdownDtoByUserIdAndYearMonth(int userId, int realTimePrediction, LocalDateTime yearMonth, LocalDateTime nextMonth){
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndDateBetween(userId, realTimePrediction, yearMonth, nextMonth);
		List<BreakdownDto> breakdownDtoList = new ArrayList<>();
		BreakdownDto breakdownDto;
		for(Breakdown i : breakdownList) {
			
			String date = i.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String listDate = i.getDate().format(DateTimeFormatter.ofPattern("MM월 dd일"));
			String assetsName =  callAssetsDto(i.getAssetsId()).getAssetsName();
			
			// 카테고리명
			String categoryName;
			if(i.getCategoryId() != null) {
				CategoryDto categoryDto = callCategoryDto(i.getCategoryId());
				categoryName = categoryDto.getCategoryName();
			}else {
				categoryName = null;
			}
			
			
			// 세부 카테고리명
			String detailCategoryName;
			if(i.getDetailCategoryId() != null) {
				DetailCategoryDto detailCategoryDto = callDetailCategoryDto(i.getDetailCategoryId());
				detailCategoryName = detailCategoryDto.getDetailCategoryName();
			}else {
				detailCategoryName = null;
			}
			
			
			// DTO에 저장
			breakdownDto = BreakdownDto.builder()
					.id(i.getId())
					.userId(userId)
					.realTimePrediction(i.getRealTimePrediction())
					.classification(i.getClassification())
					.date(date)
					.listDate(listDate)
					.assetsName(assetsName)
					.categoryName(categoryName)
					.detailCategoryName(detailCategoryName)
					.breakdownName(i.getBreakdownName())
					.cost(i.getCost())
					.memoImagePath(i.getMemoImagePath())
					.memo(i.getMemo())
					.build();
			breakdownDtoList.add(breakdownDto);
		}
		
		return breakdownDtoList;
	}
	
	// 내역 DTO id로 조회
	public BreakdownDto callBreakdownById(int userId, int breakdownId) {
		Optional<Breakdown> optionalBreakdown = breakdownRepository.findById(breakdownId);
		Breakdown breakdown = optionalBreakdown.orElse(null);
		BreakdownDto breakdownDto;
		
		if(userId == breakdown.getUserId()){
			// 사용자와 예산 카테고리 작성자의 id가 동일하면 진행
			String date = breakdown.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String listDate = breakdown.getDate().format(DateTimeFormatter.ofPattern("MM월 dd일"));
			String assetsName =  callAssetsDto(breakdown.getAssetsId()).getAssetsName();
			
			// 카테고리명
			String categoryName;
			if(breakdown.getCategoryId() != null) {
				CategoryDto categoryDto = callCategoryDto(breakdown.getCategoryId());
				categoryName = categoryDto.getCategoryName();
			}else {
				categoryName = null;
			}
			
			
			// 세부 카테고리명
			String detailCategoryName;
			if(breakdown.getDetailCategoryId() != null) {
				DetailCategoryDto detailCategoryDto = callDetailCategoryDto(breakdown.getDetailCategoryId());
				detailCategoryName = detailCategoryDto.getDetailCategoryName();
			}else {
				detailCategoryName = null;
			}
			
			breakdownDto = BreakdownDto.builder()
					.id(breakdown.getId())
					.userId(userId)
					.realTimePrediction(breakdown.getRealTimePrediction())
					.classification(breakdown.getClassification())
					.date(date)
					.listDate(listDate)
					.assetsName(assetsName)
					.categoryName(categoryName)
					.detailCategoryName(detailCategoryName)
					.breakdownName(breakdown.getBreakdownName())
					.cost(breakdown.getCost())
					.memoImagePath(breakdown.getMemoImagePath())
					.memo(breakdown.getMemo())
					.build();
		}else {
			breakdownDto = null;
		}
		
		return breakdownDto;
	}
	
	// 내역 - 해당 월 구분
	public Map<String, LocalDateTime> distinguishMonth(int userId, String yearMonth){
		
		LocalDateTime selectMonth = null; // 선택된 달
		String NextMonthString = null; // 선택된 달 + 1;
		LocalDateTime nextMonth = null; // 다음달 LocalDateTime
		
		if(yearMonth == null) {
			// 선택된 달이 없을 때
			LocalDateTime now = LocalDateTime.now(); // 현재 날짜
			String selectMonthString = Integer.toString(now.getYear()) + "-" + Integer.toString(now.getMonthValue()) + "-01";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // LocalDate 날짜 패턴 지정
			
			if(now.getMonthValue() == 12) {
				// 현재 날짜가 12월일 때
				NextMonthString =  Integer.toString(now.getYear() + 1) +"-01-01"; // 현재 날짜의 내년 1월 : 2025-01-01
			}else{
				// 현재 날짜가 12월이 아닐 때
				NextMonthString =  Integer.toString(now.getYear()) +"-" + Integer.toString(now.getMonthValue() + 1) + "-01"; // 현재 날짜의 다음 달 : 2024-11-01
			}
			// 만든 날짜를 LocalDateTime으로 변형
			selectMonth = LocalDate.parse(selectMonthString, formatter).atStartOfDay();
			nextMonth = LocalDate.parse(NextMonthString, formatter).atStartOfDay();
		}else {
			// 선택된 달이 있을 때
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 날짜 패턴 지정
			String yearMonthString = yearMonth.substring(0,4) + "-" + yearMonth.substring(4,6) + "-01"; // 선택한 날짜의 달을 지정
			
			if(Integer.parseInt(yearMonth.substring(4,6)) == 12) {
				// 선택된 달이 12월일 때
				NextMonthString = Integer.toString((Integer.parseInt(yearMonth.substring(0,4))+1)) + "-01" + "-01";
			}else{
				// 선택된 달이 12월이 아닐 떄
				NextMonthString = yearMonth.substring(0,4) + "-" + Integer.toString((Integer.parseInt(yearMonth.substring(4,6))+1)) + "-01";
			}
			
			// 만든 날짜를 LocalDateTime으로 변형
			selectMonth = LocalDate.parse(yearMonthString, formatter).atStartOfDay();
			nextMonth = LocalDate.parse(NextMonthString, formatter).atStartOfDay();
		}
		
		Map<String, LocalDateTime> distinguishMonthMap = new HashMap<>();
		distinguishMonthMap.put("selectMonth", selectMonth);
		distinguishMonthMap.put("nextMonth", nextMonth);
		
		return distinguishMonthMap;
	}
	
	// 카테고리 별 전체 사용내역 금액 계산
	public float categoryProportion(int categoryId, int realTimePrediction, LocalDateTime selectMonth, LocalDateTime nextMonth) {
		// 내역 리스트 불러오기(조건 : 카테고리 아이디, 실제 사용내역인지 예측인지, 정해진 달만)
		List<Breakdown> breakdownList = breakdownRepository.findAllByCategoryIdAndRealTimePredictionAndDateBetween(categoryId, realTimePrediction, selectMonth, nextMonth);
		
		// 카테고리 예산 불러오기
		Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
		Category category = optionalCategory.orElse(null);
		
		// 내역 저장
		int breakdownSum = 0; // 예산을 저장할 변수 초기화
		for(Breakdown i : breakdownList) {
			breakdownSum += i.getCost();
		}
		
		// 내역 / 카테고리 예산
		float proportion = breakdownSum / (float)category.getAmount();
		
		return proportion;
	}
	
	
	//// 총 수입 지출 이체 계산
	// 수입
	public int incomeSumService(int userId, String yearMonth) {
		// 해당 월 첫째날~마지막날 계산
		Map<String, LocalDateTime> distinguishMonthMap = distinguishMonth(userId, yearMonth);
		// 조건 : userId, classification("수입"), 첫째날~마지막날
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndClassificationAndDateBetween(userId, "수입",  distinguishMonthMap.get("selectMonth"), distinguishMonthMap.get("nextMonth"));
		
		// 합계 계산
		int incomeSum = 0;
		for(Breakdown i : breakdownList) {
			incomeSum += i.getCost();
		}
		return incomeSum;
		
	}
	
	// 지출
	public int outGoingSumService(int userId, String yearMonth) {
		// 해당 월 첫째날~마지막날 계산
		Map<String, LocalDateTime> distinguishMonthMap = distinguishMonth(userId, yearMonth);
		// 조건 : userId, classification("지출"), 첫째날~마지막날
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndClassificationAndDateBetween(userId, "지출",  distinguishMonthMap.get("selectMonth"), distinguishMonthMap.get("nextMonth"));
		
		// 합계 계산
		int outgoingSum = 0;
		for(Breakdown i : breakdownList) {
			outgoingSum += i.getCost();
		}
		return outgoingSum;
	}
	
	// 이체
	public int transferSumService(int userId,  String yearMonth) {
		// 해당 월 첫째날~마지막날 계산
		Map<String, LocalDateTime> distinguishMonthMap = distinguishMonth(userId, yearMonth);
		// 조건 : userId, classification("이체"), 첫째날~마지막날
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndClassificationAndDateBetween(userId, "이체",  distinguishMonthMap.get("selectMonth"), distinguishMonthMap.get("nextMonth"));
		
		// 합계 계산
		int transferSum = 0;
		for(Breakdown i : breakdownList) {
			transferSum += i.getCost();
		}
		return transferSum;
	}
	
	
}
