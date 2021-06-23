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
public class OperateurService {
	
	@Autowired
	OperateurRepository rep;
	
	@Autowired
	UserRepository userRep;
	
	@Autowired
	AgentService agentService;
	
	@Autowired
	EmailServiceImpl emailService;
	
	@Autowired
	AgenceService agenceService;
	
	
	Logger logger = LoggerFactory.getLogger(OperateurService.class.getName());
	
	
	public List<Operateur> getOperateurs(Long id)  throws NotFoundException
	{
		
		List<Operateur> operateurs= new ArrayList<Operateur>();	
		
		if(id!=null)
			operateurs.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun opérateur avec l'id "+id+" trouvé")));
		
		else
			operateurs=rep.findAll();
		
		if(operateurs.isEmpty())  throw new NotFoundException("Aucun opérateur trouvé");
		return operateurs;
	}
	
	
	public Operateur getByUsername(String username)
	{
		return rep.findByUsername(username).orElseThrow(() -> new NotFoundException("Aucun opérateur avec le username "+username+" trouvé"));
	}
	
	
	
	public List<Compte> getComptes(Long id) throws NotFoundException
	{
		Operateur operateur= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun opérateur avec l'id "+id+" trouvé"));
		if(operateur.getComptes().isEmpty()) throw new NotFoundException("Cet opérateur n'a aucun compte.");
		return operateur.getComptes();
		
	}
	
	
	
	public void addOperateur(Operateur operateur) throws AlreadyExistsException
	{
		if(userRep.findByUsername(operateur.getUsername()).isPresent()) {
			throw new AlreadyExistsException("Veuillez choisir un autre Username");
		}
		
		
		String password= operateur.getPassword();
		
		operateur.setPassword(new BCryptPasswordEncoder().encode(operateur.getPassword()));
		operateur.setRole("Operateur");
		
		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		operateur.setCreationAgent(agent);
		
		Agence agence = agenceService.getAgences(agent.getAgence().getId()).get(0);
		operateur.setAgence(agence);
		
		operateur.setAgence(agent.getAgence());
		operateur.setEstOperateur("Vrai");
		
		rep.save(operateur);
		
		if(!operateur.getEmail().isEmpty() && operateur.getEmail()!=null)
		{
			operateur.setPassword(password);
			emailService.sendAuthenticationInfos(operateur);
		}
		
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a créé l'opérateur avec le username "+operateur.getUsername());
		
		
		
	}
	
	public void updateOperateur(Long id,Operateur operateur) throws NotFoundException, AlreadyExistsException
	{
		Operateur updated = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun opérateur avec l'id "+id+" trouvé"));
		
		//verifier l'unicité du nouveau username
		if(userRep.findByUsername(operateur.getUsername()).isPresent() && !(userRep.findByUsername(operateur.getUsername()).get()==updated))
			throw new AlreadyExistsException("Veuillez choisir un autre Username");
		//verifier l'unicité du nouveau CIN
		if(rep.findByCin(operateur.getCin()).isPresent() && !(rep.findByCin(operateur.getCin()).get()==updated))
			throw new AlreadyExistsException("Un opérateur avec le CIN "+operateur.getCin()+" existe déjà");
		
		if(operateur.getNom()!=null && !operateur.getNom().isEmpty()) updated.setNom(operateur.getNom());
		if(operateur.getPrenom()!=null && !operateur.getPrenom().isEmpty()) updated.setPrenom(operateur.getPrenom());
		if(operateur.getCin()!=null && !operateur.getCin().isEmpty()) updated.setCin(operateur.getCin());
		if(operateur.getTelephone()!=null && !operateur.getTelephone().isEmpty()) updated.setTelephone(operateur.getTelephone());
		if(operateur.getAdresse()!=null && !operateur.getAdresse().isEmpty()) updated.setAdresse(operateur.getAdresse());
		if(operateur.getEmail()!=null && !operateur.getEmail().isEmpty()) updated.setEmail(operateur.getEmail());
		if(operateur.getUsername()!=null && !operateur.getUsername().isEmpty()) updated.setUsername(operateur.getUsername());
		if(operateur.getPassword()!=null && !operateur.getPassword().isEmpty()) updated.setPassword(new BCryptPasswordEncoder().encode(operateur.getPassword()));
		
		rep.save(updated);
		
		
		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a modifié l'opérateur avec le username "+updated.getUsername());

		
	}

	public void removeOperateur(Long id) throws NotFoundException
	{
		
		//vérifier l'existence du operateur
		Operateur operateur=rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun opérateur avec l'id "+id+" n'est trouvé"));
		rep.delete(operateur);
		
		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a supprimé l'opérateur avec le username "+operateur.getUsername());

	}
	
	
}
