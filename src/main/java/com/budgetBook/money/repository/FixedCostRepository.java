package com.budgetBook.money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.FixedCost;

public interface FixedCostRepository extends JpaRepository<FixedCost, Integer>{
	List<FixedCost> findAllByUserId(int userId);
	List<FixedCost> findAllByUserIdAndFixedCostNameContaining(int userId, String inputKeyword);
	void deleteByUserId(int userId);
	int countByUserId(int userId);
}
