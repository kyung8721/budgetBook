package com.budgetBook.money.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DetailCategoryDto {
	private int id;
	private int userId;
	private int category; // 상위 카테고리명
	private String detailCategoryName;
	private String color;
	private String memo;
}
