package com.budgetBook.money.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DetailCategoryDto {
	private int id;
	private int userId;
	private String categoryName; // 상위 카테고리명
	private int categoryId; // 상위 카테고리 id
	private String detailCategoryName;
	private String memo;
}
