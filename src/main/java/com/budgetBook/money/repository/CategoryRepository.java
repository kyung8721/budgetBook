package com.budgetBook.money.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	List<Category> findAllByUserId(int userId);
}
