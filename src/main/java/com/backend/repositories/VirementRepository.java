package com.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.*;

public interface VirementRepository extends JpaRepository<Virement, Long> {


}
