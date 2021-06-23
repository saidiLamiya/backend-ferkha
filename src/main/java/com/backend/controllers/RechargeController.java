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
public class RechargeController {
	
	
	RechargeService service;
	
	@Autowired
	public RechargeController(RechargeService service) {
		
		this.service=service;
	}
	
	//GET
			@GetMapping("/recharges")
			@ResponseStatus(HttpStatus.OK)
			public List<Recharge> getRecharges(@RequestParam(name="id", required=false) Long id) throws NotFoundException
			{
				return service.getRecharges(id);
			}
			
			
		
		
		//POST
			
			@PostMapping("/recharges")
			@ResponseStatus(HttpStatus.CREATED)
			public void addRecharge(@RequestBody Recharge recharge)  throws Exception, AlreadyExistsException
			{
				service.addRecharge(recharge);
			}
		
		
		

}

