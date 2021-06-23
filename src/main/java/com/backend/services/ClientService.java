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
public class ClientService {
	
	@Autowired
	ClientRepository rep;
	
	@Autowired
	UserRepository userRep;
	
	@Autowired
	AgentService agentService;
	
	@Autowired
	AgenceService agenceService;
	
	@Autowired
	EmailServiceImpl emailService;
	
	
	Logger logger = LoggerFactory.getLogger(ClientService.class.getName());
	
	
	public List<Client> getClients(Long id)  throws NotFoundException
	{
		
		List<Client> clients= new ArrayList<Client>();	
		
		if(id!=null)
			clients.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun client avec l'id "+id+" trouvé")));
		
		else
			clients=rep.findAll();
		
		if(clients.isEmpty())  throw new NotFoundException("Aucun client trouvé");
		return clients;
	}
	
	public Client getByUsername(String username)
	{
		return rep.findByUsername(username).orElseThrow(() -> new NotFoundException("Aucun client avec le username "+username+" trouvé"));
	}
	
	
	
	public List<Compte> getComptes(Long id) throws NotFoundException
	{
		Client client= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun client avec l'id "+id+" trouvé"));
		if(client.getComptes().isEmpty()) throw new NotFoundException("Cet client n'a aucun compte.");
		return client.getComptes();
		
	}
	
	public List<Beneficiaire> getBeneficiaires(Long id) throws NotFoundException
	{
		Client client= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun client avec l'id "+id+" trouvé"));
		if(client.getBeneficiaires().isEmpty()) throw new NotFoundException("Cet client n'a aucun beneficiaire.");
		return client.getBeneficiaires();
		
	}
	
	public void addClient(Client client) throws AlreadyExistsException
	{
		if(userRep.findByUsername(client.getUsername()).isPresent()) {
			throw new AlreadyExistsException("Veuillez choisir un autre Username");
		}
		
		if(rep.findByCin(client.getCin()).isPresent()) {
			throw new AlreadyExistsException("Un client avec le CIN "+client.getCin()+" existe déjà");
		}
		
		String password= client.getPassword();
		
		client.setPassword(new BCryptPasswordEncoder().encode(client.getPassword()));
		client.setRole("Client");
		
		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		client.setCreationAgent(agent);
		Agence agence = agenceService.getAgences(agent.getAgence().getId()).get(0);
		client.setAgence(agence);
		client.setEstOperateur("Faux");
		
		rep.save(client);
		
		if(!client.getEmail().isEmpty() && client.getEmail()!=null)
		{
			client.setPassword(password);
			emailService.sendAuthenticationInfos(client);
		}
		
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a créé le client avec le username "+client.getUsername());
		
		
		
	}
	
	public void updateClient(Long id,Client client) throws NotFoundException, AlreadyExistsException
	{
		Client updated = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun client avec l'id "+id+" trouvé"));
		
		//verifier l'unicité du nouveau username
		if(userRep.findByUsername(client.getUsername()).isPresent() && !(userRep.findByUsername(client.getUsername()).get()==updated))
			throw new AlreadyExistsException("Veuillez choisir un autre Username");
		//verifier l'unicité du nouveau CIN
		if(rep.findByCin(client.getCin()).isPresent() && !(rep.findByCin(client.getCin()).get()==updated))
			throw new AlreadyExistsException("Un client avec le CIN "+client.getCin()+" existe déjà");
		
		if(client.getNom()!=null && !client.getNom().isEmpty()) updated.setNom(client.getNom());
		if(client.getPrenom()!=null && !client.getPrenom().isEmpty()) updated.setPrenom(client.getPrenom());
		if(client.getCin()!=null && !client.getCin().isEmpty()) updated.setCin(client.getCin());
		if(client.getTelephone()!=null && !client.getTelephone().isEmpty()) updated.setTelephone(client.getTelephone());
		if(client.getAdresse()!=null && !client.getAdresse().isEmpty()) updated.setAdresse(client.getAdresse());
		if(client.getEmail()!=null && !client.getEmail().isEmpty()) updated.setEmail(client.getEmail());
		if(client.getUsername()!=null && !client.getUsername().isEmpty()) updated.setUsername(client.getUsername());
		if(client.getPassword()!=null && !client.getPassword().isEmpty()) updated.setPassword(new BCryptPasswordEncoder().encode(client.getPassword()));
		
		rep.save(updated);
		
		if(client.getPassword()!=null && !client.getPassword().isEmpty()) updated.setPassword(client.getPassword());
		else updated.setPassword(null);
		emailService.sendAuthenticationInfos(updated);
		
		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a modifié le client avec le username "+updated.getUsername());

		
	}

	public void removeClient(Long id) throws NotFoundException
	{
		
		//vérifier l'existence du client
		Client client=rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun client avec l'id "+id+" n'est trouvé"));
		rep.delete(client);
		
		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a supprimé le client avec le username "+client.getUsername());

	}
	
	
}
