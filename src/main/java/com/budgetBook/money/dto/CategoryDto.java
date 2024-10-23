package com.budgetBook.money.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryDto {
	private int id;
	private int userId;
	private String classification;
	private String categoryName;
	private int amount; // 예산금액
	private double proportion; // 예산금액 / 카테고리 예산
	private int categoryCost; // 카테고리별 지출
	private String color;
	private String memo;
}
