package com.budgetBook.mail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetBook.mail.domain.CertificationNumber;

public interface CertificationNumberRepository extends JpaRepository<CertificationNumber, Integer>{
	Optional<CertificationNumber> findByEmail(String email);
}
