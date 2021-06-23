package com.backend.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.backend.entities.Devise;
import com.backend.exceptions.*;
import com.backend.services.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class DeviseController {
	
	
	DeviseService service;
	
	@Autowired
	public DeviseController(DeviseService service) {
		
		this.service=service;
	}
	
	//GET
		
		@GetMapping("/devises")
		@ResponseStatus(HttpStatus.OK)
		public List<Devise> getCurrencies(@RequestParam(name="code", required=false) String code,
											@RequestParam(name="bankCode", required=false) String bankCode,
											@RequestParam(name="language", required=false) String language) throws NotFoundException
		{
			return service.getCurrencies(code, bankCode, language);
		}
	
	
	//POST

		@PostMapping("/devises")
		@ResponseStatus(HttpStatus.CREATED)
		public void addCurrency(@RequestBody Devise devise) throws AlreadyExistsException
		{
			service.addCurrency(devise);
		}
	
	
	
	//PUT
		
		@PutMapping("/devise/{code}")
		@ResponseStatus(HttpStatus.OK)
		public void updateCurrency(@PathVariable String code , @RequestBody Devise devise)  throws NotFoundException, AlreadyExistsException
		{
			service.updateCurrency(code,devise);
		}
		
		

	//DELETE
		
		@DeleteMapping("/devise/{code}")
		@ResponseStatus(HttpStatus.OK)
		public void deleteCurrency(@PathVariable String code) throws NotFoundException
		{
			service.removeCurrency(code);
		}
		
		
		

}
