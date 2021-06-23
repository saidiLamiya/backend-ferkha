package com.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.*;

public interface OperateurRepository extends JpaRepository<Operateur, Long> {
	
	Optional<Operateur> findByUsername(String username);

	Optional<Operateur> findByCin(String username);

}
