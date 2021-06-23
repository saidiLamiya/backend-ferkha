package com.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class VersionController {	
	
	
	@Autowired
	BuildProperties buildProperties;
	
	@GetMapping("/version")
	public String buildInfos()
	{
		return buildProperties.getVersion();
	}
}
