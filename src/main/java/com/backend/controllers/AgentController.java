package com.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.services.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class AgentController {
	
	
	AgentService service;
	
	@Autowired
	public AgentController(AgentService service) {
		
		this.service=service;
	}
	
	//GET
			@GetMapping("/agents")
			@ResponseStatus(HttpStatus.OK)
			public List<Agent> getAgents(@RequestParam(name="id", required=false) Long id) throws NotFoundException
			{
				return service.getAgents(id);
			}
			
			
			@GetMapping("/agent/username/{username}")
			@ResponseStatus(HttpStatus.OK)
			public Agent getByUsername(@PathVariable(name="username") String username)
			{
				return service.getByUsername(username);
			}
			
			
			
		
		
		//POST
			
			@PostMapping("/agents")
			@ResponseStatus(HttpStatus.CREATED)
			public void addAgent(@RequestBody Agent agent)  throws AlreadyExistsException
			{
				service.addAgent(agent);
			}
		
		
		
		//PUT
			
			@PutMapping("/agent/{id}")
			@ResponseStatus(HttpStatus.OK)
			public void updateAgent(@PathVariable Long id , @RequestBody(required=false) Agent agent)  throws NotFoundException, AlreadyExistsException
			{
				service.updateAgent(id,agent);
			}
	
		
			
		//DELETE
			
			@DeleteMapping("/agent/{id}")
			@ResponseStatus(HttpStatus.OK)
			public void deleteAgent(@PathVariable Long id) throws NotFoundException
			{
				service.removeAgent(id);
			}
			
	

}

