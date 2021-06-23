package com.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.Beneficiaire;
import com.backend.entities.Client;
import com.backend.entities.Compte;

public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, Long> {
	 
	Optional<Beneficiaire> findByNumeroCompte(String numeroCompte);
}