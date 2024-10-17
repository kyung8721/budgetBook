package com.budgetBook.money.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.budgetBook.money.domain.Assets;
import com.budgetBook.money.domain.Breakdown;
import com.budgetBook.money.domain.Category;
import com.budgetBook.money.domain.DetailCategory;
import com.budgetBook.money.domain.FixedCost;
import com.budgetBook.money.dto.AssetsDto;
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
	
	// 사용자의 고정비 내역 불러오기
	public List<FixedCostDto> callFixedCost(int userId) {
		List<FixedCost> fixedCostList = fixedCostRepository.findAllByUserId(userId);
		
		List<FixedCostDto> fixedCostDtoList = new ArrayList<>();
		
		if(fixedCostList !=null) {
			for(FixedCost i : fixedCostList) {
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
					period = "매월" + periodWeek;
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
						.userId(userId)
						.classification(i.getClassification())
						.period(period)
						.assetsName(assetsName)
						.categoryName(categoryName)
						.detailCategoryName(detailCategoryName)
						.fixedCostName(i.getFixedCostName())
						.fixedCost(i.getFixedCost())
						.memo(i.getMemo())
						.build();
				
				fixedCostDtoList.add(fixedCostDto);
			}
		
		}else {
			fixedCostDtoList = null;
		}
		
		return fixedCostDtoList;
	}
	
	// 자산 추가 및 수정
	public Assets saveAssets(int userId, String assetsName, int balance, String color, String memo, Integer assetsId) {
		Assets assets = Assets.builder()
					.id(assetsId)
					.userId(userId)
					.assetsName(assetsName)
					.balance(balance)
					.color(color)
					.memo(memo)
					.build();
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
		AssetsDto assetsDto = AssetsDto.builder()
				.id(id)
				.userId(assets.getUserId())
				.assetsName(assets.getAssetsName())
				.balance(assets.getBalance())
				.lastBalance(assets.getLastBalance())
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
				AssetsDto assetsDto = AssetsDto.builder()
						.id(i.getId())
						.userId(userId)
						.assetsName(i.getAssetsName())
						.balance(i.getBalance())
						.lastBalance(i.getLastBalance())
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
		Category category = Category.builder()
				.userId(userId)
				.classification(classification)
				.categoryName(categoryName)
				.amount(amount)
				.color(color)
				.memo(memo)
				.build();
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
	
	// 세부 예산 카테고리 작성 및 수정
	public DetailCategory saveDetailCategory(int userId, int categoryId, String detailCategoryName, String memo, Integer detailCategoryId) {
		DetailCategory detailCategory;
		detailCategory = DetailCategory.builder()
					.userId(userId)
					.categoryId(categoryId)
					.detailCategoryName(detailCategoryName)
					.memo(memo)
					.id(detailCategoryId)
					.build();
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
		Breakdown breakdown = Breakdown.builder()
				.userId(userId)
				.RealTimePrediction(RealTimePrediction)
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
}
