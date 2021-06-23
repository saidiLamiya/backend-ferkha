package com.backend.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;

import com.backend.entities.*;

public interface DeviseRepository extends JpaRepository<Devise, Long> {

	
	Optional<Devise> findByCode(String code);
	
	void deleteByCode(String code);
	
	//recherche par code ISO, langue et code Banque 
	Optional<Devise> findByIsoCodeAndBankCodeAndLangue(String isoCode, String bankCode, String langue);
	
	//recherche par code ALPHA, langue et code Banque 
	Optional<Devise> findByAlphaCodeAndBankCodeAndLangue(String alphaCode, String bankCode, String langue);

	
	
	
}
