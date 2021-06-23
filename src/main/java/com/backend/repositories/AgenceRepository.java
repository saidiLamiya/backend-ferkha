package com.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.*;


public interface AgenceRepository extends JpaRepository<Agence, Long> {
	
	Optional<Agence> findByNom(String username);

	Optional<Agence> findByTelephone(String telephone);

	Optional<Agence> findByFax(String fax);

}
