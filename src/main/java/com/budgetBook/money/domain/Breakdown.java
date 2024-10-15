package com.budgetBook.money.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name="breakdown")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Breakdown {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="userId")
	private int userId;
	
	@Column(name="RealTimePrediction")
	private int RealTimePrediction;
	
	private String classification;
	private LocalDateTime date;
	
	@Column(name="assetsId")
	private int assetsId;
	
	@Column(name="categoryId")
	private int categoryId;
	
	@Column(name="detailCategoryId")
	private int detailCategoryId;
	
	@Column(name="breakdownName")
	private String breakdownName;
	
	private int cost;
	
	@Column(name="memoImagePath")
	private String memoImagePath;
	
	private String memo;
	
	@Column(name="createdAt")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name="updatedAt")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
