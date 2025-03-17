package com.budgetBook.user.service;

import org.springframework.stereotype.Service;

import com.budgetBook.money.service.MoneyService;

@Service
public class UserDeleteService {
	private MoneyService moneyService;
	private UserService userService;
	
	UserDeleteService(MoneyService moneyService, UserService userService){
		this.moneyService = moneyService;
		this.userService = userService;
	}
	
	// 자산 삭제
	public boolean deleteUserAssets(int userId) {
		return moneyService.deleteAssetsByUserId(userId);
	}
	
	// 카테고리 삭제
	public boolean deleteUserCategory(int userId) {
		return moneyService.deleteCategoryByUserId(userId);
	}
	
	// 세부 카테고리 삭제
	public boolean deleteUserDetailCategory(int userId) {
		return moneyService.deleteDetailCategoryByUserId(userId);
	}
	
	// 내역 삭제
	public boolean deleteUserBreakdown(int userId) {
		return moneyService.deleteBreakdownByUserId(userId);
	}
	
	// 고정비 삭제
	public boolean deleteUserFixedCost(int userId) {
		return moneyService.deleteFixedCostByUserId(userId);
	}
	
	// 사용자 삭제
	public boolean deleteUser(int userId) {
		// 자산 삭제
		boolean assets = deleteUserAssets(userId);
		
		// 카테고리 삭제
		boolean category = deleteUserCategory(userId);
		
		// 세부 카테고리 삭제
		boolean detailCategory = deleteUserDetailCategory(userId);
		
		// 내역 삭제
		boolean breakdown = deleteUserBreakdown(userId);
		
		// 고정비 삭제
		boolean fixedCost = deleteUserFixedCost(userId);
		
		// 프로필 삭제
		boolean profile = userService.deleteProfileImageByUserId(userId);
		
		// 사용자 삭제
		boolean user = userService.deleteUser(userId);
		
		
		// 삭제 되었는지 확인
		if(assets && category && detailCategory && breakdown && fixedCost && profile && user) {
			return true;
		}else {
			return false;
		}
		
	}
	
}
