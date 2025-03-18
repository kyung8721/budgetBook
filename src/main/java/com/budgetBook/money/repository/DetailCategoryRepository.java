package com.budgetBook.money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.budgetBook.money.domain.DetailCategory;

public interface DetailCategoryRepository extends JpaRepository<DetailCategory, Integer>{
	List<DetailCategory> findAllByUserId(int userId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM detailCategory WHERE userId = :userId", nativeQuery=true)
	void deleteByUserId(@Param("userId") int userId);
	
	int countByUserId(int userId);
}
