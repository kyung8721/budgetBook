package com.budgetBook.money.domain;

import java.time.LocalDateTime;

import com.budgetBook.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="fixedCost")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedCost {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="userId")
	private int userId;
	
	private String classification;
	private String period;
	
	@Column(name="assetsId")
	private int assetsId;
	
	@Column(name="categoryId")
	private int categoryId;
	
	@Column(name="detailCategoryId")
	private int detailCategoryId;
	
	@Column(name="fixedCost")
	private int fixedCost;
	
	private String memo;
	
	@Column(name="createdAt")
	private LocalDateTime createdAt;
	
	@Column(name="updatedAt")
	private LocalDateTime updatedAt;
}
