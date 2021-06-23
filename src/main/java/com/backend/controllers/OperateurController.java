package com.backend.controllers;

import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.services.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class OperateurController {
	
	
	OperateurService service;
	
	@Autowired
	public OperateurController(OperateurService service) {
		
		this.service=service;
	}
	
	//GET
			@GetMapping("/operateurs")
			@ResponseStatus(HttpStatus.OK)
			public List<Operateur> getOperateurs(@RequestParam(name="id", required=false) Long id) throws NotFoundException
			{
				return service.getOperateurs(id);
			}
			
			
			@GetMapping("/operateur/username/{username}")
			@ResponseStatus(HttpStatus.OK)
			public Operateur getByUsername(@PathVariable(name="username") String username)
			{
				return service.getByUsername(username);
			}
			
			
			
			@GetMapping("/operateur/{id}/comptes")
			@ResponseStatus(HttpStatus.OK)
			public List<Compte> getComptes(@PathVariable(name="id") Long id) throws NotFoundException
			{
				return service.getComptes(id);
			}
			
		
		
		//POST
			
			@PostMapping("/operateurs")
			@ResponseStatus(HttpStatus.CREATED)
			public void addOperateur(@RequestBody Operateur operateur)  throws AlreadyExistsException
			{
				service.addOperateur(operateur);
			}
		
		
		
		//PUT
			
			@PutMapping("/operateur/{id}")
			@ResponseStatus(HttpStatus.OK)
			public void updateOperateur(@PathVariable Long id , @RequestBody(required=false) Operateur operateur)  throws NotFoundException, AlreadyExistsException
			{
				service.updateOperateur(id,operateur);
			}
	
		
			
		//DELETE
			
			@DeleteMapping("/operateur/{id}")
			@ResponseStatus(HttpStatus.OK)
			public void deleteOperateur(@PathVariable Long id) throws NotFoundException
			{
				service.removeOperateur(id);
			}
			
	

}

