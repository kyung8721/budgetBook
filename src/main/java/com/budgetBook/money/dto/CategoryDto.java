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
	private float proportion; // 예산금액 / 카테고리 예산
	private String color;
	private String memo;
}
