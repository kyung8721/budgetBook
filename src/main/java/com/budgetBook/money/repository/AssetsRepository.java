package com.budgetBook.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.money.domain.Assets;

public interface AssetsRepository extends JpaRepository<Assets, Integer>{

}
