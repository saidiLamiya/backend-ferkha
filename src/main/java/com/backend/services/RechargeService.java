package com.backend.services;

import java.time.LocalDateTime;
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
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class RechargeService {
	
	@Autowired
	RechargeRepository rep;
	
	@Autowired
	CompteService compteService;
	
	@Autowired
	ClientService clientService;
	
	@Autowired
	OperateurService operateurService;
	
	Logger logger = LoggerFactory.getLogger(RechargeService.class.getName());
	
	
	public List<Recharge> getRecharges(Long id)  throws NotFoundException
	{
		
		List<Recharge> recharges= new ArrayList<Recharge>();	
		
		if(id!=null)
			recharges.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune recharge avec l'id "+id+" trouvé")));
		
		else
			recharges=rep.findAll();
		
		if(recharges.isEmpty())  throw new NotFoundException("Aucune recharge trouvé");
		return recharges;
	}
	
	
	
	public void addRecharge(Recharge recharge) throws Exception, AlreadyExistsException
	{
		
		Compte compteClient = compteService.getComptes(recharge.getCompte().getId()).get(0);

		Client client = clientService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		Client proprietaire = clientService.getClients(compteClient.getProprietaire().getId()).get(0);
		
		if(client != proprietaire) throw new Exception("Ce compte ne vous appartient pas !");
				
				
		
		Operateur operateur = operateurService.getOperateurs(recharge.getOperateur().getId()).get(0);
		Compte compteOperateur = compteService.getComptes(operateur.getComptes().get(0).getId()).get(0);
		
		if(compteClient.getSolde() < recharge.getSommeEnv()) throw new Exception("Vous n'avez pas de solde suffisant ! ");
		
		recharge.setDate(LocalDateTime.now());
		
		rep.save(recharge);
		
		compteClient.setSolde(compteClient.getSolde() - recharge.getSommeEnv());
		
		compteOperateur.setSolde(compteOperateur.getSolde() + recharge.getSommeRecu());
		
		compteService.rep.save(compteClient);
		compteService.rep.save(compteOperateur);
//		
//		Twilio.init("","");
//		Message message = Message.creator(
//		    new PhoneNumber("+212642330266"),
//		    new PhoneNumber("+212636196050"),
//		    "Vous avez effectué une recharge")
//		.create();
		

		
		
		logger.debug("Le client "+client.getNom()+" "+client.getPrenom()+" ayant le Username "+client.getUsername()
			+" a effectué une recharge de "+recharge.getSommeEnv()+compteClient.getDevise().getCode()+"vers le numero "
					+recharge.getTelephone()+" de l'opérateur "+operateur.getNom()+ " à la date "+recharge.getDate());
		
	}
	

	
	
}
