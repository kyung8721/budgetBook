package com.budgetBook.money.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.Breakdown;

public interface BreakdownRepository extends JpaRepository<Breakdown, Integer>{
	List<Breakdown> findAllByUserIdAndRealTimePredictionAndDateBetween(int userId, int RealTimePrediction, LocalDateTime startDate, LocalDateTime endDate);
}
