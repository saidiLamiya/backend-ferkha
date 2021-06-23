package com.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.*;

public interface ClientRepository extends JpaRepository<Client, Long> {
	
	Optional<Client> findByUsername(String username);

	Optional<Client> findByCin(String username);

}
