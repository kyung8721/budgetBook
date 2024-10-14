package com.budgetBook.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
