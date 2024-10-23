package com.budgetBook.money.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BreakdownDto {
	private int id;
	private int userId;
	private int realTimePrediction;
	private String classification;
	private String date;
	private String listDate;
	private String assetsName;
	private String assetsColor;
	private String categoryName;
	private String categoryColor;
	private String detailCategoryName;
	private String breakdownName;
	private int cost;
	private String memoImagePath;
	private String memo;
}
