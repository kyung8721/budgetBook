package com.budgetBook.money.dto;

import java.time.LocalDateTime;

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
	private int balance; // 자산 잔액(or 업데이트된 날 이후부터 계산된 자산 잔액)
	private int balancePrediction;
	private String color;
	private String memo;
	private LocalDateTime updatedAt; //자산이 업데이트 된 날
}
