package com.backend.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.repositories.*;
import com.itextpdf.text.DocumentException;

@Service
public class CompteService {

	@Autowired
	CompteRepository rep;

	@Autowired
	AgentService agentService;

	@Autowired
	ContratService contratService;

	@Autowired
	ClientService clientService;

	@Autowired
	VirementService virementService;



	Logger logger = LoggerFactory.getLogger(CompteService.class.getName());



	public List<Compte> getComptes(Long id)  throws NotFoundException
	{

		List<Compte> comptes= new ArrayList<Compte>();	

		if(id!=null)
			comptes.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" trouvé")));

		else
			comptes=rep.findAll();

		if(comptes.isEmpty())  throw new NotFoundException("Aucun compte trouvé");
		
		return comptes;
	}
	
	public Compte getCompteByNumero(String numero)
	{
		Compte compte = rep.findByNumero(numero).orElseThrow(() -> new NotFoundException("Ce compte n'existe pas"));
				
		return compte;
	}
	
	public List<Virement> getVirements(Long id)  throws NotFoundException
	{
		Compte compte= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" trouvé"));

		List<Virement> virementsEnvoyes = compte.getVirementsEnvoyes();
		List<Virement> virementsRecus = compte.getVirementsRecus();
		List<Virement> virements = new ArrayList<Virement>();
		virements.addAll(virementsEnvoyes);
		virements.addAll(virementsRecus);

		Comparator<Virement> compareByDate = (Virement v1, Virement v2) -> v1.getDate().compareTo( v2.getDate() );
		Collections.sort(virements, compareByDate);

		if(virements.isEmpty()) throw new NotFoundException("Aucun virement effectuté");

		return virements;
	}

	public List<Virement> getVirementsEnvoyes(Long id)  throws NotFoundException
	{

		Compte compte = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" trouvé"));

		List<Virement> virements = compte.getVirementsEnvoyes();

		if(virements.isEmpty())  throw new NotFoundException("Aucun virement envoyé");
		return virements;
	}


	public List<Virement> getVirementsRecus(Long id)  throws NotFoundException
	{

		Compte compte = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" trouvé"));

		List<Virement> virements = compte.getVirementsRecus();

		if(virements.isEmpty())  throw new NotFoundException("Aucun virement recu");
		return virements;
	}


	public List<Recharge> getRecharges(Long id)  throws NotFoundException
	{
		Compte compte= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" trouvé"));

		List<Recharge> recharges = compte.getRecharges();
		if(recharges.isEmpty()) throw new NotFoundException("Aucune recharge effectutée");

		return recharges;
	}
	
	public List<Operation> getOperations(Long id)  throws NotFoundException
	{
		Compte compte= rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" trouvé"));

		List<Operation> operations = compte.getOperations();
		if(operations.isEmpty()) throw new NotFoundException("Aucune opérartion effectutée");

		return operations;
	}



	public void addCompte(Compte compte) throws AlreadyExistsException, DocumentException, FileNotFoundException
	{
		if(rep.findByNumero(compte.getNumero()).isPresent()) {
			throw new AlreadyExistsException("Un compte avec le Numero "+compte.getNumero()+" existe déjà");
		}


		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

		compte.setCreationAgent(agent);
		compte.setCreationDate(LocalDateTime.now());
		compte.setNumero(generateNumCompte());

		Client client= clientService.getClients(compte.getProprietaire().getId()).get(0);

		rep.save(compte);

		contratService.createContrat(compte,client);

		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()
		+" a créé le compte: "+compte.getNumero()+" au client avec le Username "+client.getUsername());

	}

	public void updateCompte(Long id,Compte compte) throws NotFoundException, AlreadyExistsException
	{
		Compte updated = rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" trouvé"));

		//verifier l'unicité du nouveau numero
		if(rep.findByNumero(compte.getNumero()).isPresent() && !(rep.findByNumero(compte.getNumero()).get()==updated))
			throw new AlreadyExistsException("Un compte avec le Numero "+compte.getNumero()+" existe déjà");

		if(compte.getNumero()!=null && !compte.getNumero().isEmpty()) updated.setNumero(compte.getNumero());
		if(compte.getType()!=null && !compte.getType().isEmpty()) updated.setType(compte.getType());
		if(compte.getSolde()!=0) updated.setSolde(compte.getSolde());
		if(compte.getDevise()!=null) updated.setDevise(compte.getDevise());

		rep.save(updated);

		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a modifié le compte: "+compte.getNumero());
	}

	public void removeCompte(Long id) throws NotFoundException
	{

		//vérifier l'existence de l'compte
		Compte compte=rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun compte avec l'id "+id+" n'est trouvé"));
		rep.delete(compte);

		Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a supprimé le compte: "+compte.getNumero());
	}
	
	
	
	public ResponseEntity<InputStreamResource> getContratPDF(Long id) throws IOException
	{
		Compte compte = getComptes(id).get(0);
		
		String fileName = "compte_"+compte.getNumero()+"_"+compte.getCreationDate().toString().replace(':', '-')+".pdf";
		
		Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
		
		PathResource pdfFile = new PathResource(path+"\\src\\main\\resources\\contrats\\"+fileName);
		 
		
		
		  ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(
		    new InputStreamResource(pdfFile.getInputStream()), HttpStatus.OK);
		  
		  Agent agent = agentService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		  logger.debug("L'agent "+agent.getNom()+" "+agent.getPrenom()+" ayant le Username "+agent.getUsername()+" a téléchargé le fichier "+fileName+" à la date: "+LocalDateTime.now());
		  
		  return response;

	}
	
	
	public String generateNumCompte()
	{
		    int leftLimit = 48; // numeral '0'
		    int rightLimit = 57; // letter 'z'
		    int targetStringLength = 20;
		    Random random = new Random();
		    String generatedString;
		    generatedString = random.ints(leftLimit, rightLimit + 1)
		      .filter(i -> i <= 57 && i >= 48)
		      .limit(targetStringLength)
		      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		      .toString();
		    
		    try {
		    	getCompteByNumero(generatedString);
		    	}catch(NotFoundException e)
		    {
		    		return generatedString;
		    }
		 
		    return generateNumCompte();
		
	}
	


}
