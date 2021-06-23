package com.backend.services;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.entities.Agent;
import com.backend.entities.Client;
import com.backend.entities.Compte;
import com.backend.entities.Devise;
import com.backend.entities.Operation;
import com.backend.entities.Virement;
import com.backend.exceptions.AlreadyExistsException;
import com.backend.exceptions.NotFoundException;
import com.backend.repositories.OperationRepository;

@Service
public class OperationService {
	

	@Autowired
	OperationRepository rep;
	
	@Autowired
	CompteService compteService;
	
	@Autowired
	AgentService agentService;
	
	@Autowired
	DeviseService deviseService;
	
	@Autowired
	RecuOperationService recuService;
	
	
	Logger logger = LoggerFactory.getLogger(RechargeService.class.getName());
	
	
	public List<Operation> getOperations(Long id)  throws NotFoundException
	{
		
		List<Operation> operations= new ArrayList<Operation>();	
		
		if(id!=null)
			operations.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun operation avec l'id "+id+" trouvé")));
		
		else
			operations=rep.findAll();
		
		if(operations.isEmpty())  throw new NotFoundException("Aucun operation trouvé");
		return operations;
	}
		
	
	
	public void addOperation(Operation operation) throws Exception, AlreadyExistsException
	{
		Compte compte = compteService.getComptes(operation.getCompte().getId()).get(0);
		Devise devise = deviseService.getDevises(operation.getDevise().getId()).get(0);
		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		if(operation.getType().equals("Retrait"))
		{
			if(compte.getSolde() < operation.getSommeCompte()) throw new Exception("Vous n'avez pas de solde suffisant ! ");
			
			operation.setDate(LocalDateTime.now());
			rep.save(operation);
			compte.setSolde(compte.getSolde() - operation.getSommeCompte());
			

		}
		if(operation.getType().equals("Versement"))
		{
			
			operation.setDate(LocalDateTime.now());
			rep.save(operation);
			compte.setSolde(compte.getSolde() + operation.getSommeCompte());			
			
		}		
		
		compteService.rep.save(compte);
		
		recuService.CreateRecu(operation);
		
		
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()
		+" a effectué un "+operation.getType()+" de "+operation.getSommeEspece()+devise.getCode()+" à la date "+operation.getDate()+" en faveur du compte "
			+compte.getNumero());
		
		
	}
	
	
	public ResponseEntity<InputStreamResource> getRecuOperationPDF(Long id) throws IOException
	{
		Operation operation = getOperations(id).get(0);
		Compte compte = compteService.getComptes(operation.getCompte().getId()).get(0);
		
		String fileName = operation.getType()+"_"+compte.getNumero()+"_"+operation.getDate().toString().replace(':', '-')+".pdf";
		Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
		
		PathResource pdfFile = new PathResource(path+"\\src\\main\\resources\\recu\\recu-operation\\"+fileName);
		 	
		
		  ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(
		    new InputStreamResource(pdfFile.getInputStream()), HttpStatus.OK);
		  
		  Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a téléchargé le fichier "+fileName+" à la date: "+LocalDateTime.now());
			
		  
		  return response;

	}
	

}
