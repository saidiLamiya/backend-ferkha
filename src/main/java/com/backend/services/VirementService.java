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

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.repositories.*;

import javassist.compiler.ast.NewExpr;

@Service
public class VirementService {
	
	@Autowired
	VirementRepository rep;
	
	@Autowired
	CompteService compteService;

	@Autowired
	ClientService clientService;
	
	@Autowired
	RecuVirementService recuService;
	
	@Autowired
	DeviseService deviseService;
	
	Logger logger = LoggerFactory.getLogger(RechargeService.class.getName());
	
	public List<Virement> getVirements(Long id)  throws NotFoundException
	{
		
		List<Virement> virements= new ArrayList<Virement>();	
		
		if(id!=null)
			virements.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun virement avec l'id "+id+" trouvé")));
		
		else
			virements=rep.findAll();
		
		if(virements.isEmpty())  throw new NotFoundException("Aucun virement trouvé");
		return virements;
	}
	
	public boolean isEnough(List<Virement> virements, Compte debiteur) {
		
		boolean enough = false;
		Double somme = 0.0;
		
		for(int i=0; i<virements.size(); i++) {
			
			Virement virement = virements.get(i);
			somme = virement.getSommeEnv() + somme;
		}
		
		if(debiteur.getSolde() > somme) {
			enough = true;
		}
		
		return enough;
	}
	
	public void addVirement(List<Virement> virements) throws Exception, AlreadyExistsException
	{
		Compte creancier = compteService.getComptes(virements.get(0).getCreancier().getId()).get(0);
		List<Compte> debiteurs;
		
		//verify if there is enough money
		if(!isEnough(virements, creancier)) {
			throw new Exception("Vous n'avez pas de solde suffisant !");
		}
		
		for(int i=0; i<virements.size(); i++) {
			Virement virement = virements.get(i);
			Compte debiteur = compteService.getComptes(virement.getDebiteur().getId()).get(0);
			
			Client client = clientService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
			Client clientCreancier = clientService.getClients(creancier.getProprietaire().getId()).get(0);
			if(client != clientCreancier) throw new Exception("Ce compte ne vous appartient pas !");
			
			virement.setDate(LocalDateTime.now());
			rep.save(virement);
			
			creancier.setSolde(creancier.getSolde() - virement.getSommeEnv());
			debiteur.setSolde(debiteur.getSolde() + virement.getSommeRecu());
			
			compteService.rep.save(debiteur);
			
			Devise devise = deviseService.getDevises(creancier.getDevise().getId()).get(0);
			
			logger.debug("Le client "+client.getNom()+" "+client.getPrenom()+" ayant le Username "+client.getUsername()
				+" a effectué un virement de "+virement.getSommeEnv()+devise.getCode()+" à la date "+virement.getDate()+" du compte "
					+creancier.getNumero()+" vers le compte "+debiteur.getNumero());
		}
		
		compteService.rep.save(creancier);
	}
	
	public ResponseEntity<InputStreamResource> getRecuVirementPDF(Long id) throws IOException
	{
		Virement virement = getVirements(id).get(0);
		Compte debiteur = compteService.getComptes(virement.getDebiteur().getId()).get(0);
		
		String fileName = "virement_"+debiteur.getNumero()+"_"+virement.getDate().toString().replace(':', '-')+".pdf";
		
		Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
		
		PathResource pdfFile = new PathResource(path+"\\src\\main\\resources\\recu\\recu-virement\\"+fileName);
		 
		  ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(
		    new InputStreamResource(pdfFile.getInputStream()), HttpStatus.OK);
		  
		  Client client = clientService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("Le client "+client.getNom()+" "+client.getPrenom()+" ayant le Username "+client.getUsername()+" a téléchargé le fichier "+fileName+" à la date: "+LocalDateTime.now());
			
		  return response;

	}
	
}
