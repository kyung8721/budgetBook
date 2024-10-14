package com.budgetBook.money.dto;

import jakarta.persistence.Column;
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
	private String color;
	private String memo;
}
