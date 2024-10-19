package com.budgetBook.money.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BreakdownDto {
	private int id;
	private int userId;
	private int RealTimePrediction;
	private String classification;
	private String date;
	private String assetsName;
	private String categoryName;
	private String detailCategoryName;
	private String breakdownName;
	private int cost;
	private String memoImagePath;
	private String memo;
}
