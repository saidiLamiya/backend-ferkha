package com.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.Operation;

public interface OperationRepository extends JpaRepository<Operation, Long> {

}
