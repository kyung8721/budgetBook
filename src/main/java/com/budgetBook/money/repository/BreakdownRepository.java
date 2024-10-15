package com.budgetBook.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.Breakdown;

public interface BreakdownRepository extends JpaRepository<Breakdown, Integer>{

}
