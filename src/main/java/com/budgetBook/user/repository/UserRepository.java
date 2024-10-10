package com.budgetBook.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.user.domain.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
}
