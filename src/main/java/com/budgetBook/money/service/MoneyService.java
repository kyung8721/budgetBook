package com.budgetBook.money.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.budgetBook.money.domain.FixedCost;
import com.budgetBook.money.repository.FixedCostRepository;

@Service
public class MoneyService {
	
	private FixedCostRepository fixedCostRepository;
	
	public MoneyService(FixedCostRepository fixedCostRepository) {
		this.fixedCostRepository = fixedCostRepository;
	}
	
	// 고정비 저장 및 수정
	public FixedCost addFixedCost(FixedCost fixedCostObject) {
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
}
