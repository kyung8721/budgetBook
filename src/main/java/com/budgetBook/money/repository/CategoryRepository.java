package com.budgetBook.money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.budgetBook.money.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	List<Category> findAllByUserId(int userId);
	List<Category> findAllByUserIdAndClassification(int userId, String classification);
	List<Category> findAllByUserIdAndCategoryNameContaining(int userId, String categoryInputKeyword);
	
	
	@Transactional
	@Modifying
	@Query(value="DELETE FROM category WHERE userId = :userId", nativeQuery=true)
	void deleteByUserId(@Param("userId") int userId);
	
	int countByUserId(int userId);
}
