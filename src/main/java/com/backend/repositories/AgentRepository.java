package com.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.*;

public interface AgentRepository extends JpaRepository<Agent, Long> {
		
	Optional<Agent> findByUsername(String username);

	Optional<Agent> findByCin(String username);

}
