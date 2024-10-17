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
@Table(name="detailCategory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailCategory {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="userId")
	private int userId;
	
	@Column(name="categoryId")
	private int categoryId;
	
	@Column(name="detailCategoryName")
	private String detailCategoryName;
	
	private String memo;
	
	@Column(name="createdAt")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name="updatedAt")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
