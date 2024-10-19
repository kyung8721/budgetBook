package com.budgetBook.money.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AssetsDto {
	
	private int id;
	private int userId;
	private String assetsName;
	private int balance; // 자산 잔액
	private Integer lastBalance;
	private String balanceDifference;
	private String color;
	private String memo;
}
