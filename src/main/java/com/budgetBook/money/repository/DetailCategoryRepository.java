package com.budgetBook.money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.DetailCategory;

public interface DetailCategoryRepository extends JpaRepository<DetailCategory, Integer>{
	List<DetailCategory> findAllByUserId(int userId);
}
