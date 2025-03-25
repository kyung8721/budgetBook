package com.budgetBook.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.user.domain.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByLoginId(String loginId);
	Optional<User> findByEmail(String email);
	int countById(int userId);
}
