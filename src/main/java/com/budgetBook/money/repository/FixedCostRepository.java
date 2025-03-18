package com.budgetBook.money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.budgetBook.money.domain.FixedCost;

public interface FixedCostRepository extends JpaRepository<FixedCost, Integer>{
	List<FixedCost> findAllByUserId(int userId);
	List<FixedCost> findAllByUserIdAndFixedCostNameContaining(int userId, String inputKeyword);


	@Transactional
	@Modifying
	@Query(value = "DELETE FROM fixedCost WHERE userId = :userId", nativeQuery = true)
	void deleteByUserId(@Param("userId") int userId);
	
	int countByUserId(int userId);
}
