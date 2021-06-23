package com.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.*;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	
	Optional<Admin> findByUsername(String username);

	Optional<Admin> findByCin(String cin);

}
