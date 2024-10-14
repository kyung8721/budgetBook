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
	private int assets;
	private int category;
	private int detailCategory;
	private int fixedCost;
	private String memo;
}
