package com.budgetBook.money.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FixedCostDto {
	
	private int id;
	private int userId;
	private String classification;
	private String period;
	private String assetsName;
	private String assetsColor;
	private String categoryName;
	private String categoryColor;
	private String detailCategoryName;
	private String fixedCostName;
	private int fixedCost;
	private String memo;
}
