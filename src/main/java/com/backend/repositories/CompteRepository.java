package com.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.entities.*;

public interface CompteRepository extends JpaRepository<Compte, Long> {

	Optional<Compte> findByNumero(String numero);
	
}
