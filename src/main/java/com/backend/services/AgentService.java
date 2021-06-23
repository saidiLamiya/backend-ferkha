package com.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.repositories.*;

@Service
public class AgentService {
	
	@Autowired
	AgentRepository rep;
	
	@Autowired
	UserRepository userRep;
	
	
	@Autowired
	AdminService adminService;
	
	@Autowired
	EmailServiceImpl emailService;
	
	Logger logger = LoggerFactory.getLogger(AgentService.class.getName());
	
	public List<Agent> getAgents(Long id)  throws NotFoundException
	{
		
		List<Agent> agents= new ArrayList<Agent>();	
		if(id!=null)
			agents.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun agent avec l'id "+id+" trouvé")));
		
		else
			agents=rep.findAll();
		
		if(agents.isEmpty())  throw new NotFoundException("Aucun agent trouvé");
		return agents;
	}
	
	public Agent getByUsername(String username)
	{
		return rep.findByUsername(username).orElseThrow(() -> new NotFoundException("Aucun agent avec le username "+username+" trouvé"));
	}
	
	
	
	
	public void addAgent(Agent agent) throws AlreadyExistsException
	{
		if(userRep.findByUsername(agent.getUsername()).isPresent()) {
			throw new AlreadyExistsException("Veuillez choisir un autre Username");
		}
		
		if(rep.findByCin(agent.getCin()).isPresent()) {
			throw new AlreadyExistsException("Un agent avec le CIN "+agent.getCin()+" existe déjà");
		}
		String password= agent.getPassword();
		
		agent.setPassword(new BCryptPasswordEncoder().encode(agent.getPassword()));
		agent.setRole("Agent");
		
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		agent.setCreationAdmin(admin);
		
		rep.save(agent);
		
		if(!agent.getEmail().isEmpty() && agent.getEmail()!=null)
		{
			agent.setPassword(password);
			emailService.sendAuthenticationInfos(agent);
		}
		
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a créé l'agent avec le username "+agent.getUsername());
		
	}
	
	public void updateAgent(Long id,Agent agent) throws NotFoundException, AlreadyExistsException
	{
		Agent updated = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun agent avec l'id "+id+" trouvé"));
		
		//verifier l'unicité du nouveau username
		if(userRep.findByUsername(agent.getUsername()).isPresent() && !(userRep.findByUsername(agent.getUsername()).get()==updated))
			throw new AlreadyExistsException("Veuillez choisir un autre Username");
		//verifier l'unicité du nouveau CIN
		if(rep.findByCin(agent.getCin()).isPresent() && !(rep.findByCin(agent.getCin()).get()==updated))
			throw new AlreadyExistsException("Un agent avec le CIN "+agent.getCin()+" existe déjà");
		
		if(agent.getNom()!=null && !agent.getNom().isEmpty()) updated.setNom(agent.getNom());
		if(agent.getPrenom()!=null && !agent.getPrenom().isEmpty()) updated.setPrenom(agent.getPrenom());
		if(agent.getCin()!=null && !agent.getCin().isEmpty()) updated.setCin(agent.getCin());
		if(agent.getTelephone()!=null && !agent.getTelephone().isEmpty()) updated.setTelephone(agent.getTelephone());
		if(agent.getAdresse()!=null && !agent.getAdresse().isEmpty()) updated.setAdresse(agent.getAdresse());
		if(agent.getEmail()!=null && !agent.getEmail().isEmpty()) updated.setEmail(agent.getEmail());
		if(agent.getUsername()!=null && !agent.getUsername().isEmpty()) updated.setUsername(agent.getUsername());
		if(agent.getPassword()!=null && !agent.getPassword().isEmpty()) updated.setPassword(new BCryptPasswordEncoder().encode(agent.getPassword()));
		
		rep.save(updated);
		
		if(agent.getPassword()!=null && !agent.getPassword().isEmpty()) updated.setPassword(agent.getPassword());
		else updated.setPassword(null);
		emailService.sendAuthenticationInfos(updated);
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a modifié l'agent avec le username "+updated.getUsername());
		
	}

	public void removeAgent(Long id) throws NotFoundException
	{
		
		//vérifier l'existence de l'agent
		Agent agent=rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun agent avec l'id "+id+" n'est trouvé"));
		rep.delete(agent);
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a supprimé l'agent avec le username "+agent.getUsername());
	}

}
