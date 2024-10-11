package com.budgetBook.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.user.domain.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Integer>{
	public Profile findByUserId(int userId);
}
