package com.backend.controllers;

import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.services.*;

@CrossOrigin(origins = "*")
@RestController
public class VirementController {
	
	
	VirementService service;
	
	@Autowired
	public VirementController(VirementService service) {
		
		this.service=service;
	}
	
	//GET
			@GetMapping("/virements")
			@ResponseStatus(HttpStatus.OK)
			public List<Virement> getVirements(@RequestParam(name="id", required=false) Long id) throws NotFoundException
			{
				return service.getVirements(id);
			}
			
			
			@GetMapping(value="/virementPDF/{id}", produces = "application/pdf")
			@ResponseStatus(HttpStatus.OK)
			public ResponseEntity<InputStreamResource> getRecuVirementPDF(@PathVariable(name="id") Long id) throws IOException
			{
				return service.getRecuVirementPDF(id);
			}
		
		//POST
			
			@PostMapping("/virements")
			@ResponseStatus(HttpStatus.CREATED)
			public void addVirement(@RequestBody List<Virement> virements)  throws Exception, AlreadyExistsException
			{
				service.addVirement(virements);
			}
}

