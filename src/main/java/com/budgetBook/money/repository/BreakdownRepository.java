package com.budgetBook.money.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.Breakdown;

public interface BreakdownRepository extends JpaRepository<Breakdown, Integer>{
	List<Breakdown> findAllByUserIdAndRealTimePredictionAndDateBetweenOrderByDate(int userId, int RealTimePrediction, LocalDateTime startDate, LocalDateTime endDate);
	List<Breakdown> findAllByUserIdAndRealTimePredictionAndClassificationAndDateBetween(int userId, int realTimePrediction, String classification, LocalDateTime startDate, LocalDateTime endDate);
	List<Breakdown> findAllByCategoryIdAndRealTimePredictionAndDateBetween(int categoryId, int realTimePrediction,  LocalDateTime startDate, LocalDateTime endDate);
	List<Breakdown> findAllByUserIdAndClassificationAndRealTimePredictionAndDateBetween(int userId, String classification, int RealTimePrediction, LocalDateTime startDate, LocalDateTime endDate);
	List<Breakdown> findAllByUserIdAndRealTimePredictionAndClassificationAndCategoryIdAndDateBetweenOrderByDate(int userId, int realTimePrediction, String classification, int categoryId, LocalDateTime startDate, LocalDateTime endDate);
	List<Breakdown> findAllByUserIdAndRealTimePredictionAndClassificationAndDateBetweenOrderByDate(int userId, int realTimePrediction, String classification, LocalDateTime startDate, LocalDateTime endDate);
	List<Breakdown> findAllBySelectBreakdown(int id);
	List<Breakdown> findByUserIdAndRealTimePredictionAndBreakdownNameContainingAndDateBetween(int userId, int realTimePrediction, String inputKeyword, LocalDateTime startDate, LocalDateTime endDate);
	List<Breakdown> findAllByUserIdAndAssetsIdAndRealTimePredictionAndDateBetween(int userId, int assetsId, int RTP, LocalDateTime startDay, LocalDateTime lastDay);
}
