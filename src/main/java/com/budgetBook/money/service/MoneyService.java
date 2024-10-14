package com.budgetBook.money.service;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.budgetBook.money.domain.Assets;
import com.budgetBook.money.domain.Category;
import com.budgetBook.money.domain.FixedCost;
import com.budgetBook.money.dto.FixedCostDto;
import com.budgetBook.money.repository.AssetsRepository;
import com.budgetBook.money.repository.CategoryRepository;
import com.budgetBook.money.repository.FixedCostRepository;
import com.budgetBook.user.dto.UserDto;
import com.budgetBook.user.service.UserService;

@Service
public class MoneyService {
	
	private FixedCostRepository fixedCostRepository;
	private UserService userService;
	private AssetsRepository assetsRepository;
	private CategoryRepository categoryRepository;
	
	public MoneyService(FixedCostRepository fixedCostRepository, UserService userService, AssetsRepository assetsRepository, CategoryRepository categoryRepository) {
		this.fixedCostRepository = fixedCostRepository;
		this.userService = userService;
		this.assetsRepository = assetsRepository;
		this.categoryRepository = categoryRepository;
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
	public FixedCostDto callFixedCost(int userId) {
		Optional<FixedCost> optionalFixedCost = fixedCostRepository.findByUserId(userId);
		FixedCost fixedCost = optionalFixedCost.orElse(null);
		
		if(fixedCost !=null) {
			String period;
			// 반복주기
			if(fixedCost.getPeriod().startsWith("M")) {
				// 매월
				period = "매월" + fixedCost.getPeriod().substring(1);
			}else if(fixedCost.getPeriod().startsWith("W")) {
				// 매주
				String periodWeek;
				switch(fixedCost.getPeriod().substring(1)) {
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
			}else if(fixedCost.getPeriod().startsWith("Daily")) {
				// 매일
				period = "매일";
			}else {
				// 오류
				return null;
			}
			// 자산명
			
			// 카테고리명
			
			// 세부 카테고리명
			
			FixedCostDto fixedCostDto = FixedCostDto.builder()
					.id(fixedCost.getId())
					.userId(userId)
					.classification(fixedCost.getClassification())
					.period(period)
					.assets()
					.category()
					.detailCategory()
					.fixedCost(fixedCost.getFixedCost())
					.memo(fixedCost.getMemo())
					.build();
			
			return fixedCostDto;
		}else {
			return null;
		}
	}
	
	// 자산 추가
	public Assets addAssets(int userId, String assetsName, int balance, String color, String memo, Integer assetsId) {
		Assets assets;
		if(assetsId == null) {
			// 자산 추가
			assets = Assets.builder()
					.userId(userId)
					.assetsName(assetsName)
					.balance(balance)
					.color(color)
					.memo(memo)
					.build();
		}else {
			// 자산 수정
			assets = Assets.builder()
					.id(assetsId)
					.userId(userId)
					.assetsName(assetsName)
					.balance(balance)
					.color(color)
					.memo(memo)
					.build();
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
	
	// 예산 카테고리 작성 및 수정
	public Category addCategory(int userId, String classification, String categoryName, int amount, String color, String memo, Integer categoryId) {
		Category category;
		if(categoryId == null) {
			// 예산 카테고리 저장
			category = Category.builder()
					.userId(userId)
					.classification(classification)
					.categoryName(categoryName)
					.amount(amount)
					.color(color)
					.memo(memo)
					.build();
			return categoryRepository.save(category);
		}else {
			// 예산 카테고리 수정
			category = Category.builder()
					.id(categoryId)
					.userId(userId)
					.classification(classification)
					.categoryName(categoryName)
					.amount(amount)
					.color(color)
					.memo(memo)
					.build();
			return categoryRepository.save(category);
		}
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
}
