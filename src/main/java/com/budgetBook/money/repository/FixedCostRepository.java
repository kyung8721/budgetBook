package com.budgetBook.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.FixedCost;

public interface FixedCostRepository extends JpaRepository<FixedCost, Integer>{

}
