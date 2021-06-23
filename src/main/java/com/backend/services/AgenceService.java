package com.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.repositories.*;

@Service
public class AgenceService {
	
	@Autowired
	AgenceRepository rep;
	
	@Autowired
	AdminService adminService;
	
	Logger logger = LoggerFactory.getLogger(AgenceService.class.getName());
	
	
	public List<Agence> getAgences(Long id)  throws NotFoundException
	{
		
		List<Agence> agences= new ArrayList<Agence>();	
		if(id!=null)
			agences.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune agence avec l'id "+id+" trouvée")));
		
		else
			agences=rep.findAll();
		
		if(agences.isEmpty())  throw new NotFoundException("Aucune agence trouvée");
		return agences;
	}
	
	
	
	public List<Agent> getAgents(Long id) throws NotFoundException
	{
		Agence agence= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune agence avec l'id "+id+" trouvée"));
		if(agence.getAgents().isEmpty()) throw new NotFoundException("Cet agence ne contient aucun agent.");
		return agence.getAgents();
		
	}
	
	
	public List<Client> getClients(Long id)  throws NotFoundException
	{
		
		Agence agence= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune agence avec l'id "+id+" trouvée"));
		if(agence.getClients().isEmpty()) throw new NotFoundException("Cet agence ne contient aucun client.");
		return agence.getClients();
	}
	
	
	
	
	public void addAgence(Agence agence) throws AlreadyExistsException, NotFoundException
	{
		if(rep.findByNom(agence.getNom()).isPresent()) {
			throw new AlreadyExistsException("Une agence avec le Nom "+agence.getNom()+" existe déjà");
		}
		
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		agence.setCreationAdmin(admin);
		
		rep.save(agence);
		
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a créé l'agence "+agence.getNom());
		
	}
	
	public void updateAgence(Long id,Agence agence) throws NotFoundException, AlreadyExistsException
	{
		Agence updated = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune agence avec l'id "+id+" trouvé"));
		
		//verifier l'unicité du nouveau nom
		if(rep.findByNom(agence.getNom()).isPresent() && !(rep.findByNom(agence.getNom()).get()==updated))
			throw new AlreadyExistsException("Une agence avec le Nom "+agence.getNom()+" existe déjà");
		
		if(rep.findByTelephone(agence.getTelephone()).isPresent() && !(rep.findByTelephone(agence.getTelephone()).get()==updated))
			throw new AlreadyExistsException("Une agence avec le Telephone "+agence.getTelephone()+" existe déjà");
		
		if(rep.findByFax(agence.getFax()).isPresent() && !(rep.findByFax(agence.getFax()).get()==updated))
			throw new AlreadyExistsException("Une agence avec le Fax "+agence.getFax()+" existe déjà");
		
		if(agence.getNom()!=null && !agence.getNom().isEmpty()) 	updated.setNom(agence.getNom());
		if(agence.getTelephone()!=null && !agence.getTelephone().isEmpty()) 	updated.setTelephone(agence.getTelephone());
		if(agence.getAdresse()!=null && !agence.getAdresse().isEmpty()) 	updated.setAdresse(agence.getAdresse());
		if(agence.getEmail()!=null && !agence.getEmail().isEmpty()) 	updated.setEmail(agence.getEmail());
		if(agence.getFax()!=null && !agence.getFax().isEmpty()) 	updated.setFax(agence.getFax());
		
		rep.save(updated);
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a modifié l'agence "+updated.getNom());
		
	}

	public void removeAgence(Long id) throws NotFoundException
	{
		
		//vérifier l'existence de l'agence
		Agence agence=rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune agence avec l'id "+id+" n'est trouvé"));
		rep.delete(agence);
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a supprimé l'agence "+agence.getNom());
	}

}
