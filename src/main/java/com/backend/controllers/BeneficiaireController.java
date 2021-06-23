package com.backend.controllers;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.backend.entities.Beneficiaire;
import com.backend.entities.Compte;
import com.backend.exceptions.AlreadyExistsException;
import com.backend.exceptions.NotFoundException;
import com.backend.services.BeneficiaireService;
import com.backend.services.CompteService;
import com.itextpdf.text.DocumentException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class BeneficiaireController {

BeneficiaireService service;
	
	@Autowired
	public BeneficiaireController(BeneficiaireService service) {
		
		this.service=service;
	}
	//get
	@GetMapping("/beneficiaires")
	@ResponseStatus(HttpStatus.OK)
	public List<Beneficiaire> getBeneficiaires() throws NotFoundException
	{
		return service.getBeneficiaires();
	}
	
	//post
		@PostMapping("/beneficiaire")
		@ResponseStatus(HttpStatus.CREATED)
		public void addBeneficiaire(@RequestBody String numCompte)  throws AlreadyExistsException, DocumentException, FileNotFoundException
		{
			service.addBeneficiaire(numCompte);
		}
		
	
	//DELETE
		@DeleteMapping("/beneficiaire/{id}")
		@ResponseStatus(HttpStatus.OK)
		public void deleteBeneficiaire(@PathVariable(name="id") Long id) throws NotFoundException
		{
			service.removeBeneficiaire(id);
		}
}