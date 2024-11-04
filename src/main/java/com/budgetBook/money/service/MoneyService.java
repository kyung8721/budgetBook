package com.budgetBook.money.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
	public FixedCost saveFixedCost(int userId, String classification, String period, int assetsId, Integer categoryId, Integer detailCategoryId, String fixedCostName, int fixedCost, String memo, Integer fixedCostId) {
		FixedCost fixedCostObject;
		
		// period에 들어온 문자가 M05가 아닌 M3이런 식일 때
		if(period.startsWith("M") && period.substring(1).length() < 2) {
			String periodNum = period.substring(1);
			
			period = "M0" + periodNum;
		}
		
		if(fixedCostId == null) {
			// 고정비 작성
			fixedCostObject = FixedCost.builder()
					.userId(userId)
					.classification(classification)
					.period(period)
					.assetsId(assetsId)
					.categoryId(categoryId)
					.detailCategoryId(detailCategoryId)
					.fixedCostName(fixedCostName)
					.fixedCost(fixedCost)
					.memo(memo)
					.build();
		}else {
			// 고정비 수정
			fixedCostObject = FixedCost.builder()
					.id(fixedCostId)
					.userId(userId)
					.classification(classification)
					.period(period)
					.assetsId(assetsId)
					.categoryId(categoryId)
					.detailCategoryId(detailCategoryId)
					.fixedCostName(fixedCostName)
					.fixedCost(fixedCost)
					.memo(memo)
					.build();
		}
		
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
		
		// 자산 색
		String assetsColor = assetsDto.getColor();
		
		// 카테고리명, 카테고리 색
		String categoryName;
		String categoryColor = "#00BFFF";
		if(i.getCategoryId() != null) {
			CategoryDto categoryDto = callCategoryDto(i.getCategoryId());
			categoryName = categoryDto.getCategoryName();
			categoryColor = categoryDto.getColor();
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
				.assetsColor(assetsColor)
				.categoryName(categoryName)
				.categoryColor(categoryColor)
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
	
	// 고정비 수정 - 자산
	public FixedCost saveFixedCostbyAssetsId(int userId, int fixedCostId, int assetsId){
		Optional<FixedCost> optionalfixedCost = fixedCostRepository.findById(fixedCostId);
		FixedCost fixedCost = optionalfixedCost.orElse(null);
		
		if(fixedCost != null && fixedCost.getUserId() == userId) {
			fixedCost.setAssetsId(assetsId);
			return fixedCostRepository.save(fixedCost);
		}else {
			return null;
		}
	}
	
	// 고정비 수정 - 카테고리
	public FixedCost saveFixedCostbyCategoryId(int userId, int fixedCostId, int categoryId){
		Optional<FixedCost> optionalFixedCost = fixedCostRepository.findById(fixedCostId);
		FixedCost fixedCost = optionalFixedCost.orElse(null);
		
		if(fixedCost != null && fixedCost.getUserId() == userId) {
			fixedCost.setCategoryId(categoryId);
			return fixedCostRepository.save(fixedCost);
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
	
	// Assets -> AssetsDto
	public AssetsDto AssetsToDto(Assets i) {
		// 자산 차이 계산
		String balanceDifference;
		
		// 지난 달과 비교
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
				.userId(i.getUserId())
				.assetsName(i.getAssetsName())
				.balance(i.getBalance())
				.lastBalance(i.getLastBalance())
				.balanceDifference(balanceDifference)
				.color(i.getColor())
				.memo(i.getMemo())
				.build();
		return assetsDto;
	}
	
	// 자산 DTO로 불러오기
	public AssetsDto callAssetsDto(int id) {
		Optional<Assets> optionalAssets = assetsRepository.findById(id);
		Assets assets = optionalAssets.orElse(null);
		
		
		AssetsDto assetsDto = AssetsToDto(assets);
		
		return assetsDto;
	}
	
	// 자산 DTO UserId로 불러오기
	public List<AssetsDto> callAssetsDtoByUserId(int userId) {
		List<Assets> assetsList = assetsRepository.findAllByUserId(userId);
		
		List<AssetsDto> assetsDtoList = new ArrayList<>();
		if(assetsList != null) {
			for(Assets i : assetsList) {
				
				AssetsDto assetsDto = AssetsToDto(i);
				assetsDtoList.add(assetsDto);
			}
			
		}else {
			assetsDtoList = null;
		}
		
		return assetsDtoList;
	}
	
	// 자산 DTO UserId, yearMonth, realTimePrediction으로 불러오기
	public List<AssetsDto> callAssetsDtoByUserIdAndYearMonthAndRTP(int userId, String yearMonth, int RTP) {
		List<Assets> assetsList = assetsRepository.findAllByUserId(userId);
		
		List<AssetsDto> assetsDtoList = new ArrayList<>();
		if(assetsList != null) {
			for(Assets i : assetsList) {
				// assets -> assetsDto
				AssetsDto assetsDto = AssetsToDto(i);
				// 내역 합계 추가
				int costSum = sumOfCategory(i.getUserId(), yearMonth, i.getId(), RTP);
				assetsDto.setBalancePrediction(costSum);
				
				// 리스트에 저장
				assetsDtoList.add(assetsDto);
			}
			
		}else {
			assetsDtoList = null;
		}
		
		return assetsDtoList;
	}
	
	// 자산 별 합계 계산
	public int sumOfCategory(int userId, String yearMonth, int assetsId, int RTP){
		//// 선택된 달, 해당 자산Id에 해당하는 내역만 불러오기
		// 선택된 달 날짜 불러오기
		Map<String, LocalDateTime> month = distinguishMonth(userId, yearMonth);
		
		// 내역 불러오기
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndAssetsIdAndRealTimePredictionAndDateBetween(userId, assetsId, RTP, month.get("startDay"), month.get("lastDay"));
		
		// 내역 합계 - 수입은 더하고 지출은 빼고
		int costSum = 0;
		for(Breakdown i : breakdownList) {
			if(i.getClassification() == "수입") {
				// 수입
				costSum += i.getCost();
			}else {
				// 지출
				costSum -= i.getCost();
			}
		}
		return costSum;
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
	
	// Category -> CategoryDto
	public CategoryDto CategoryToDto(Category i) {
		CategoryDto categoryDto = CategoryDto.builder()
				.id(i.getId())
				.userId(i.getUserId())
				.classification(i.getClassification())
				.categoryName(i.getCategoryName())
				.amount(i.getAmount())
				.color(i.getColor())
				.memo(i.getMemo())
				.build();
		return categoryDto;
	}
	
	// 예산 카테고리 Dto 불러오기
	public CategoryDto callCategoryDto(int id) {
		Optional<Category> optionalCategory = categoryRepository.findById(id);
		Category category = optionalCategory.orElse(null);
		CategoryDto categoryDto = CategoryToDto(category);
		return categoryDto;
	}
	
	// 예산 카테고리 DTO userId로 불러오기
	public List<CategoryDto> callCategoryDtoByUserId(int userId) {
		List<Category> categoryList = categoryRepository.findAllByUserId(userId);
		List<CategoryDto> categoryDtoList = new ArrayList<>();
		
		if(categoryList != null) {
			for(Category i : categoryList) {
				
				// DTO에 저장
				CategoryDto categoryDto = CategoryToDto(i);
				categoryDtoList.add(categoryDto);
			}
			
		}else {
			categoryDtoList = null;
		}
		
		return categoryDtoList;
	}
	
	// 카테고리 DTO userId 및 classification로 불러오기 - proportion(사용한 내역 / 카테고리 예산 비율)
	public List<CategoryDto> callCategoryDtoByUserIdAndRealTimePredictionAndDate(int userId, int realTimePrediction, LocalDateTime selectMonth, LocalDateTime nextMonth, String classification){
		List<Category> categoryList = new ArrayList<>();
		if(classification.equals("All")) {
			categoryList = categoryRepository.findAllByUserId(userId);
		}else {
			categoryList = categoryRepository.findAllByUserIdAndClassification(userId, classification);
		}
		
		List<CategoryDto> categoryDtoList = new ArrayList<>();
		
		if(categoryList != null) {
			for(Category i : categoryList) {
				
				// 카테고리 내역 사용 비율
				double proportion = categoryProportion(i.getId(), realTimePrediction, selectMonth, nextMonth, classification);
				
				// 카테고리별 내역 합계
				int categoryCost = 0;
				List<Breakdown> breakdownList = breakdownRepository.findAllByCategoryIdAndRealTimePredictionAndDateBetween(i.getId(), realTimePrediction, selectMonth, nextMonth);
				for(Breakdown j : breakdownList) {
					categoryCost += j.getCost();
				}
				
				// DTO에 저장
				CategoryDto categoryDto = CategoryDto.builder()
						.id(i.getId())
						.userId(userId)
						.classification(i.getClassification())
						.categoryName(i.getCategoryName())
						.amount(i.getAmount())
						.color(i.getColor())
						.memo(i.getMemo())
						.proportion(proportion)
						.categoryCost(categoryCost)
						.build();
				categoryDtoList.add(categoryDto);
			}
			
		}else {
			categoryDtoList = null;
		}
		
		return categoryDtoList;
		
	}
	
	// 예산(카테고리) 합계, 내역 합계, 둘 사이의 비율 계산
	public Map<String, Float> allProportion(int userId, int realTimePrediction, LocalDateTime selectMonth, LocalDateTime nextMonth){
		// 카테고리 불러오기
		List<Category> categoryList = categoryRepository.findAllByUserIdAndClassification(userId, "지출");
		
		// 예산 합계(지출만)
		float categorySum = 0;
		for(Category i : categoryList) {
			categorySum += i.getAmount();
		}
		
		// 내역 합계(지출만)
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndClassificationAndRealTimePredictionAndDateBetween(userId, "지출", realTimePrediction, selectMonth, nextMonth);
		float breakdownSum = 0;
		for(Breakdown i : breakdownList) {
			breakdownSum += i.getCost();
		}
		
		// 비율
		float proportion = (float) ((breakdownSum / (float)categorySum) * 100.0);
		
		// 차이
		float CategoryMinusBreakdown = categorySum - breakdownSum;
		
		// map에 저장
		Map<String, Float> resultMap = new HashMap<>();
		resultMap.put("categorySum", categorySum);
		resultMap.put("breakdownSum", breakdownSum);
		resultMap.put("proportion", proportion);
		resultMap.put("CMB", CategoryMinusBreakdown);
		
		return resultMap;
		
		
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
		
		
		return detailCategoryRepository.save(detailCategory);
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
			for(DetailCategory i : detailCategoryList) {
				
				detailCategoryDto = DetailCategoryDto.builder()
						.id(i.getId())
						.userId(userId)
						.categoryId(i.getCategoryId())
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
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndDateBetweenOrderByDate(userId, realTimePrediction, yearMonth, nextMonth);
		List<BreakdownDto> breakdownDtoList = new ArrayList<>();
		BreakdownDto breakdownDto;
		for(Breakdown i : breakdownList) {
			
			String date = i.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String listDate = i.getDate().format(DateTimeFormatter.ofPattern("MM월 dd일"));
			
			// 자산 명, 자산 색
			String assetsName =  callAssetsDto(i.getAssetsId()).getAssetsName();
			String assetsColor = callAssetsDto(i.getAssetsId()).getColor();
			
			// 카테고리명, 색
			String categoryName;
			String categoryColor = "#00BFFF";
			if(i.getCategoryId() != null) {
				CategoryDto categoryDto = callCategoryDto(i.getCategoryId());
				categoryName = categoryDto.getCategoryName();
				categoryColor = categoryDto.getColor();
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
			
			// 남은 금액 저장
			int selectCostSum = 0;
			List<Breakdown> selectBreakdownList = breakdownRepository.findAllBySelectBreakdown(i.getId());
			for(Breakdown BD : selectBreakdownList) {
				if(BD.getClassification().equals("수입")) {
					// 수입 시 예산이 늘어나게
					selectCostSum -= BD.getCost();
				}else if(BD.getClassification().equals("지출")) {
					// 지출 시 예산이 줄어들게
					selectCostSum += BD.getCost();
				}
				
			}
			int remainCost = i.getCost() - selectCostSum;
			
			
			// DTO에 저장
			breakdownDto = BreakdownDto.builder()
					.id(i.getId())
					.userId(userId)
					.realTimePrediction(i.getRealTimePrediction())
					.classification(i.getClassification())
					.date(date)
					.listDate(listDate)
					.assetsName(assetsName)
					.assetsColor(assetsColor)
					.categoryName(categoryName)
					.categoryColor(categoryColor)
					.detailCategoryName(detailCategoryName)
					.breakdownName(i.getBreakdownName())
					.cost(i.getCost())
					.memoImagePath(i.getMemoImagePath())
					.memo(i.getMemo())
					.remainCost(remainCost)
					.selectBreakdown(i.getSelectBreakdown())
					.build();
			breakdownDtoList.add(breakdownDto);
		}
		
		return breakdownDtoList;
	}
	
	// // 내역 조회(해당 달 정보만, 해당 카테고리 id만)
	public List<BreakdownDto> callBreakdownDtoByUserIdAndClassificationAndYearMonthAndCategoryId(int userId, String classification, int realTimePrediction, LocalDateTime yearMonth, LocalDateTime nextMonth, Integer categoryId){
		
		// 저장 리스트 초기화
		List<Breakdown> breakdownList = new ArrayList<>();
		
		// 카테고리 아이디 유무에 따라 내역 받아오기
		if(categoryId == 0) {
			// 카테고리 아이디가 0이면 전체 지출이나 수입 내역 받아오기
			breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndClassificationAndDateBetweenOrderByDate(userId, realTimePrediction, classification, yearMonth, nextMonth);
		}else {
			// 카테고리 아이디가 있으면 해당 지출이나 수입 내역 받아오기
			breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndClassificationAndCategoryIdAndDateBetweenOrderByDate(userId, realTimePrediction, classification, categoryId, yearMonth, nextMonth);
		}
		
		// 받아온 내역을 categoryDto로 저장
		List<BreakdownDto> breakdownDtoList = new ArrayList<>();
		BreakdownDto breakdownDto;
		for(Breakdown i : breakdownList) {
			
			String date = i.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String listDate = i.getDate().format(DateTimeFormatter.ofPattern("MM월 dd일"));
			
			// 자산 명, 자산 색
			String assetsName =  callAssetsDto(i.getAssetsId()).getAssetsName();
			String assetsColor = callAssetsDto(i.getAssetsId()).getColor();
			
			// 카테고리명, 색
			String categoryName;
			String categoryColor = "#00BFFF";
			if(i.getCategoryId() != null) {
				CategoryDto categoryDto = callCategoryDto(i.getCategoryId());
				categoryName = categoryDto.getCategoryName();
				categoryColor = categoryDto.getColor();
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
					.assetsColor(assetsColor)
					.categoryName(categoryName)
					.categoryColor(categoryColor)
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
	public Map<String, LocalDateTime> distinguishMonth(int userId, String yearMonth){ // yearmonth : 202410
		
		LocalDateTime startDay = null; // 선택된 달의 첫째 날
		LocalDateTime lastDay = null; // 선택된 달의 마지막 날 마지막 시간
		
		if(yearMonth == null) {
			// 선택된 달이 없을 때
			LocalDate now = LocalDate.now(); // 현재 날짜
			
			startDay = now.withDayOfMonth(1).atStartOfDay();
			lastDay = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);
			
			
		}else {
			// 선택된 달이 있을 때
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 날짜 패턴 지정
			LocalDate yearMonthlocaldate = LocalDate.parse(yearMonth.substring(0,4) + "-" + yearMonth.substring(4,6) + "-01", formatter); // 선택한 날짜의 달을 지정
			
			startDay = yearMonthlocaldate.withDayOfMonth(1).atStartOfDay();
			lastDay = yearMonthlocaldate.withDayOfMonth(yearMonthlocaldate.lengthOfMonth()).atTime(LocalTime.MAX);
		}
		
		Map<String, LocalDateTime> distinguishMonthMap = new HashMap<>();
		distinguishMonthMap.put("startDay", startDay);
		distinguishMonthMap.put("lastDay", lastDay);
		
		return distinguishMonthMap;
	}
	
	// 카테고리 별 전체 사용내역 금액 계산
	public double categoryProportion(int categoryId, int realTimePrediction, LocalDateTime selectMonth, LocalDateTime nextMonth, String classification) {
		// 내역 리스트 불러오기(조건 : 카테고리 아이디, 실제 사용내역인지 예측인지, 지출/수출, 정해진 달만)
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
		double proportion = (breakdownSum / (double)category.getAmount()) * 100.0;
		
		return proportion;
	}
	
	// 내역 수정 - 자산
	public Breakdown saveBreakdownbyAssetsId(int userId, int breakdownId, int assetsId){
		Optional<Breakdown> optionalBreakdown = breakdownRepository.findById(breakdownId);
		Breakdown breakdown = optionalBreakdown.orElse(null);
		
		if(breakdown != null && breakdown.getUserId() == userId) {
			breakdown.setAssetsId(assetsId);
			return breakdownRepository.save(breakdown);
		}else {
			return null;
		}
	}
	
	// 내역 수정 - 카테고리
	public Breakdown saveBreakdownbyCategoryId(int userId, int breakdownId, int categoryId){
		Optional<Breakdown> optionalBreakdown = breakdownRepository.findById(breakdownId);
		Breakdown breakdown = optionalBreakdown.orElse(null);
		
		if(breakdown != null && breakdown.getUserId() == userId) {
			breakdown.setCategoryId(categoryId);
			return breakdownRepository.save(breakdown);
		}else {
			return null;
		}
	}
	
	// 차트 데이터 받아오기
	public List<Map<String, Object>> chartDataService(Integer categoryId, int userId, String classification, String yearMonth){
		
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		Map<String, LocalDateTime> distinguishMonthMap = distinguishMonth(userId, yearMonth); // 월 구별
		
		if(categoryId == 0) {
			// 카테고리 아이디가 없으면 전체 카테고리 내역 받아가기
			// [{"categoryName" : 카테고리명}, {"cost" : 지출액}, {"color" : 컬러} ]
			// 카테고리
			List<CategoryDto> categoryDtoList = callCategoryDtoByUserIdAndRealTimePredictionAndDate(userId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"), classification);
			for(CategoryDto i : categoryDtoList) {
				String name = i.getCategoryName(); // 카테고리명
				int cost = i.getCategoryCost(); // 지출액
				String color = i.getColor(); // 컬러
				
				Map<String, Object> map = new HashMap<>();
				map.put("categoryName", name);
				map.put("cost", cost);
				map.put("color", color);
				
				resultList.add(map);
			}
			
		}else {
			// 카테고리 아이디가 있으면 해당 카테고리 내역만 받아가기
			// [{"categoryName" : 카테고리명}, {"cost" : 지출액}, {"color" : 컬러} ]
			Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
			Category category = optionalCategory.orElse(null);
			String name = category.getCategoryName(); // 카테고리명
			String color = category.getColor(); // 컬러
			
			// 카테고리 사용 내역 합계
			int categoryCost = 0;
			 	// 카테고리 Id에 해당하는 수입/지출 내역 불러오기(현재 달만)
			List<Breakdown> breakdownList = breakdownRepository.findAllByCategoryIdAndRealTimePredictionAndDateBetween(categoryId, 1, distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
			for(Breakdown j : breakdownList) {
				categoryCost += j.getCost();
			}
			
			
			Map<String, Object> map = new HashMap<>();
			map.put("categoryName", name);
			map.put("cost", categoryCost);
			map.put("color", color);
			
			resultList.add(map);
		}
		return resultList;
		
	}
	
	//// 총 수입 지출 이체 계산
	// 수입
	public int incomeSumService(int userId, String yearMonth, int realTimePrediction) {
		// 해당 월 첫째날~마지막날 계산
		Map<String, LocalDateTime> distinguishMonthMap = distinguishMonth(userId, yearMonth);
		// 조건 : userId, classification("수입"), 첫째날~마지막날
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndClassificationAndDateBetween(userId, realTimePrediction, "수입",  distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		// 합계 계산
		int incomeSum = 0;
		for(Breakdown i : breakdownList) {
			incomeSum += i.getCost();
		}
		return incomeSum;
		
	}
	
	// 지출
	public int outGoingSumService(int userId, String yearMonth, int realTimePrediction) {
		// 해당 월 첫째날~마지막날 계산
		Map<String, LocalDateTime> distinguishMonthMap = distinguishMonth(userId, yearMonth);
		// 조건 : userId, classification("지출"), 첫째날~마지막날
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndClassificationAndDateBetween(userId, realTimePrediction, "지출",  distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		// 합계 계산
		int outgoingSum = 0;
		for(Breakdown i : breakdownList) {
			outgoingSum += i.getCost();
		}
		return outgoingSum;
	}
	
	// 이체
	public int transferSumService(int userId,  String yearMonth, int realTimePrediction) {
		// 해당 월 첫째날~마지막날 계산
		Map<String, LocalDateTime> distinguishMonthMap = distinguishMonth(userId, yearMonth);
		// 조건 : userId, classification("이체"), 첫째날~마지막날
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndClassificationAndDateBetween(userId, realTimePrediction, "이체",  distinguishMonthMap.get("startDay"), distinguishMonthMap.get("lastDay"));
		
		// 합계 계산
		int transferSum = 0;
		for(Breakdown i : breakdownList) {
			transferSum += i.getCost();
		}
		return transferSum;
	}
	
	//DB에 내역 예측에서 선택된 사용 완료한 내역 저장 
	public boolean partUseSave(int[] selectBreakdowns, int breakdownId){
		
		for(int i : selectBreakdowns) {
			Optional<Breakdown> optionalBreakdown = breakdownRepository.findById(i);
			Breakdown breakdown = optionalBreakdown.orElse(null);
			
			if(breakdown != null) {
				breakdown.setSelectBreakdown(breakdownId);
				
				// 다시 저장
				breakdownRepository.save(breakdown);
			}else {
				return false;
			}
		}
		
		return true;
		
	}

	// 검색 기능 구현 - breakdown
	public List<BreakdownDto> searchBreakdown(int userId, int RTP, String inputKeyword , LocalDateTime selectMonth, LocalDateTime nextMonth){
		List<Breakdown> breakdownList = breakdownRepository.findByUserIdAndRealTimePredictionAndBreakdownNameContainingAndDateBetween(userId, RTP, inputKeyword, selectMonth, nextMonth);
		List<BreakdownDto> breakdownDtoList = new ArrayList<>();
		if(breakdownList != null) {
			BreakdownDto breakdownDto;
			for(Breakdown i : breakdownList) {
				breakdownDto = callBreakdownById(userId, i.getId());
				breakdownDtoList.add(breakdownDto);
			}
		}else {
			breakdownDtoList = null;
		}
		
		return breakdownDtoList;
	}
	
	// 검색 기능 구현 - fixedCost
	public List<FixedCostDto> searchFixedCost(int userId, String inputKeyword){
		List<FixedCost> fixedCostList = fixedCostRepository.findAllByUserIdAndFixedCostNameContaining(userId, inputKeyword);
		
		// 초기화
		List<FixedCostDto> fixedCostDtoList = new ArrayList<>();
		FixedCostDto fixedCostDto;
		
		// FixedCost -> FixedCostDto
		for(FixedCost i : fixedCostList) {
			fixedCostDto = fixedCostToDto(i);
			fixedCostDtoList.add(fixedCostDto);
		}
		
		return fixedCostDtoList;
	}
	
	//
	public List<CategoryDto> searchCategory(int userId, String categoryInputKeyword){
		List<Category> categoryList = categoryRepository.findAllByUserIdAndCategoryNameContaining(userId, categoryInputKeyword);
		
		// 초기화
		List<CategoryDto> categoryDtoList = new ArrayList<>();
		CategoryDto categoryDto;
		
		// Category -> CategoryDto
		for(Category i : categoryList) {
			categoryDto = CategoryToDto(i); 
			categoryDtoList.add(categoryDto);
		}
		
		return categoryDtoList;
	}
	
	public List<AssetsDto> searchAssets(int userId, String inputKeyword){
		List<Assets> assetsList = assetsRepository.findAllByUserIdAndAssetsNameContaining(userId, inputKeyword);
		
		// 초기화
		List<AssetsDto> assetsDtoList = new ArrayList<>();
		AssetsDto assetsDto;
		
		// Category -> CategoryDto
		for(Assets i : assetsList) {
			assetsDto = AssetsToDto(i); 
			assetsDtoList.add(assetsDto);
		}
		
		
		return assetsDtoList;
	}
	
	public List<Map<String, String>> calculateDayList(int userId, int RTP, LocalDateTime startDate, LocalDateTime endDate){
		
		
		/*
		title : "수입 " + "415,454" + "원" -> 반복마다 바뀜
		, start : "2024-10-28" -> 반복마다 바뀜
		, backgroundColor : "#FFFFFF" -> 반복마다 바뀜 x
		, borderColor : "#FFFFFF" -> 반복마다 바뀜 x
		, textColor : "#01DF01" -> 반복마다 바뀜 x /  수입 지출에 따라 다름
		*/
		
		
		
		// 해당 월 모든 사용내역 조회
		List<Breakdown> breakdownList = breakdownRepository.findAllByUserIdAndRealTimePredictionAndDateBetweenOrderByDate(userId, RTP, startDate, endDate);
		
		LocalDate incomeDay = LocalDate.of(0000,01,01); // 수입 날짜 기본값
		LocalDate outgoingDay = LocalDate.of(0000,01,01); // 치줄 날짜 기본값
		LocalDate transferDay = LocalDate.of(0000,01,01); // 이체 날짜 기본값
		int incomeCostSum = 0;  // 일자별 수입 합계 저장할 변수
		int outgoingCostSum = 0;  // 일자별 지출 합계 저장할 변수
		int transferCostSum = 0;  // 일자별 이체 합계 저장할 변수
		int repeat = 1; // 반복 횟수
		
		// 초기화
		List<Map<String, String>> BreaKdownMapList = new ArrayList<>();
		// 수입
		Map<String, String> incomeCalendarEventMap = new HashMap<>();
		incomeCalendarEventMap.put("backgroundColor", "#FFFFFF");
		incomeCalendarEventMap.put("borderColor", "#FFFFFF");
		incomeCalendarEventMap.put("textColor", "#01DF01");
		// 지출
		Map<String, String> outgoingCalendarEventMap = new HashMap<>();
		outgoingCalendarEventMap.put("backgroundColor", "#FFFFFF");
		outgoingCalendarEventMap.put("borderColor", "#FFFFFF");
		outgoingCalendarEventMap.put("textColor", "#FF0000");
		// 이체
		Map<String, String> transferCalendarEventMap = new HashMap<>();
		transferCalendarEventMap.put("backgroundColor", "#FFFFFF");
		transferCalendarEventMap.put("borderColor", "#FFFFFF");
		transferCalendarEventMap.put("textColor", "#000000");
		
		boolean dayChange = false;
		
		// 내역 반복하면서 일자마다 합치기
		for(Breakdown i : breakdownList) {
			if(i.getClassification().equals("수입")) {
				
				if(incomeDay.isBefore(i.getDate().toLocalDate())) { // LocalDate로 변경하여 날짜 비교
					 // 내역의 날짜가 저장된 day보다 큼
					if(repeat == 1) {
						// 반복의 처음이면 map 초기화 없음
					}else {
						// 반복의 처음이 아니고 날짜가 넘어갔다면 map을 초기화
						// 초기화
						incomeCalendarEventMap = new HashMap<>();
						incomeCalendarEventMap.put("backgroundColor", "#FFFFFF");
						incomeCalendarEventMap.put("borderColor", "#FFFFFF");
						incomeCalendarEventMap.put("textColor", "#01DF01");
					}
					// day 저장 후 넘어가기
					// 날짜 string으로 변환
					incomeDay = i.getDate().toLocalDate();
					String date = incomeDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					
					// map에 날짜 저장
					incomeCalendarEventMap.put("start", date);
					
					incomeCostSum = 0; // 일자별 합계 저장할 변수 초기화
					
					dayChange = true; // 날짜 변경됨
				}
				
				incomeCostSum += i.getCost();
				repeat += 1;
				
				incomeCalendarEventMap.put("title", "수입 " + incomeCostSum +"원");
				if(dayChange == true) {
					BreaKdownMapList.add(incomeCalendarEventMap); // map이 초기화 되는 게 아닌 이상 같은 자리에 덮어쓰기 되므로 반복 끝날 때마다 새로 리스트 저장
					dayChange = false;
				};
				
			}else if(i.getClassification().equals("지출")){
				
				if(outgoingDay.isBefore(i.getDate().toLocalDate())) { // LocalDate로 변경하여 날짜 비교
					 // 내역의 날짜가 저장된 day보다 큼
					if(repeat == 1) {
						// 반복의 처음이면 map 초기화 없음
					}else {
						// 반복의 처음이 아니고 날짜가 넘어갔다면 map을 초기화
						// 초기화
						outgoingCalendarEventMap = new HashMap<>();
						outgoingCalendarEventMap.put("backgroundColor", "#FFFFFF");
						outgoingCalendarEventMap.put("borderColor", "#FFFFFF");
						outgoingCalendarEventMap.put("textColor", "#FF0000");
					}
					// 날짜 string으로 변환
					outgoingDay = i.getDate().toLocalDate();
					String date = outgoingDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					
					// map에 날짜 저장
					outgoingCalendarEventMap.put("start", date);
					
					outgoingCostSum = 0; // 일자별 합계 저장할 변수 초기화
					
					dayChange = true; // 날짜 변경됨
				}
				outgoingCostSum += i.getCost();
				repeat += 1;
				
				outgoingCalendarEventMap.put("title", "지출 " + outgoingCostSum +"원");
				if(dayChange == true) {
					BreaKdownMapList.add(outgoingCalendarEventMap); // map이 초기화 되는 게 아닌 이상 같은 자리에 덮어쓰기 되므로 반복 끝날 때마다 새로 리스트 저장
					dayChange = false;
				};
				
			}else{
				
				if(transferDay.isBefore(i.getDate().toLocalDate())) { // LocalDate로 변경하여 날짜 비교
					 // 내역의 날짜가 저장된 day보다 큼
					if(repeat == 1) {
						// 반복의 처음이면 map 초기화 없음
					}else {
						// 반복의 처음이 아니고 날짜가 넘어갔다면 map을 초기화
						// 초기화
						transferCalendarEventMap = new HashMap<>();
						transferCalendarEventMap.put("backgroundColor", "#FFFFFF");
						transferCalendarEventMap.put("borderColor", "#FFFFFF");
						transferCalendarEventMap.put("textColor", "#000000");
					}
					// 날짜 string으로 변환
					transferDay = i.getDate().toLocalDate();
					String date = transferDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					
					// map에 날짜 저장
					transferCalendarEventMap.put("start", date);
					
					transferCostSum = 0; // 일자별 합계 저장할 변수 초기화
					dayChange = true; // 날짜 변경됨
				}
				transferCostSum += i.getCost();
				repeat += 1;
				
				transferCalendarEventMap.put("title", "이체 " + transferCostSum +"원");
				if(dayChange == true) {
					BreaKdownMapList.add(transferCalendarEventMap); // map이 초기화 되는 게 아닌 이상 같은 자리에 덮어쓰기 되므로 반복 끝날 때마다 새로 리스트 저장
					dayChange = false;
				};
				
				
			}
			
		} // 반복문 종료
		
		
		return BreaKdownMapList;
	}
	
	
}
