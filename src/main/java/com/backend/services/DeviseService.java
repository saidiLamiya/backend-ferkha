package com.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.*;

import com.backend.entities.*;
import com.backend.exceptions.*;
import com.backend.repositories.*;

@Service
public class DeviseService {
	
	@Autowired
	DeviseRepository rep;
	
	@Autowired
	AdminService adminService;
	
	Logger logger = LoggerFactory.getLogger(DeviseService.class.getName());
	
	
	//Rechercher les devises
	public List<Devise> getCurrencies(String code, String bankCode, String language) throws NotFoundException
	{

		//les devises à tester
		List<Devise> allCurrencies= rep.findAll();
		if(allCurrencies.isEmpty()) throw new NotFoundException("Aucune devise trouvée");
		
		//les devises à retourner
		List<Devise> returnedCurrencies= rep.findAll();	
		
		//verifier si les parametres existent et si les devises les safisfont
		for (Devise devise : allCurrencies) {

			if(
					(code!=null && !code.isEmpty() && !devise.getCode().equals(code))
					||(bankCode!=null && !bankCode.isEmpty() && !devise.getBankCode().equals(bankCode))
					||(language!=null && !language.isEmpty() && !devise.getLangue().equals(language))		
			)
			{
				returnedCurrencies.remove(devise);
			}
			
		}	
		
		//Si aucune devise ne satisfait les parametres
		if(returnedCurrencies.isEmpty()) throw new NotFoundException("Aucune devise trouvée");
		
		return returnedCurrencies;
	}
	
	
	
	public List<Devise> getDevises(Long id)
	{
		List<Devise> devises = new ArrayList<Devise>();
		if(id==null) devises = rep.findAll();
		else
			devises.add(rep.findById(id).orElseThrow(() -> new NotFoundException("Aucune devise avec l'id"+id+"trouvée.")));
		
		return devises;
	}


	//Ajouter une devise
	
	public void addCurrency(Devise devise) throws AlreadyExistsException
	{
		
		//vérifier l'existence de la devise
		if(rep.findByCode(devise.getCode()).isPresent()) 
			throw new AlreadyExistsException("Une devise avec le code "+devise.getCode()+" existe déjà");
		
		//vérifier l'unicité du triplet (code ISO, langue et code Banque )
		if(rep.findByIsoCodeAndBankCodeAndLangue(devise.getIsoCode(), devise.getBankCode(), devise.getLangue()).isPresent())
			throw new AlreadyExistsException("Cette devise ne respecte pas l'unicité du Code Iso");
		
		//vérifier l'unicité du triplet (code ALPHA, langue et code Banque )
		if(rep.findByAlphaCodeAndBankCodeAndLangue(devise.getAlphaCode(), devise.getBankCode(), devise.getLangue()).isPresent())
			throw new AlreadyExistsException("Cette devise ne respecte pas l'unicité du Code Alpha");
		
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		devise.setCreationAdmin(admin);
		devise.setModificationAdmin(admin);
		devise.setCreationDate(LocalDateTime.now());
		devise.setModificationDate(LocalDateTime.now());
		
		rep.save(devise);
		
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a créé la devise "+devise.getCode());

	}
	
	
	//modifier une devise
	public void updateCurrency(String code,Devise devise) throws NotFoundException, AlreadyExistsException
	{
		
		//vérifier l'existence de la devise
		Devise updated = rep.findByCode(code).orElseThrow(() -> new NotFoundException("Aucune devise avec le code "+code+" n'est trouvée") );	
		
		//vérifier si les données saisies respectent l'unicité du code
				if(rep.findByCode(devise.getCode()).isPresent() && !(rep.findByCode(devise.getCode()).get()==updated))
				{
					throw new AlreadyExistsException("Une devise avec le code "+devise.getCode()+" existe déjà");
				}
		
		//vérifier si les données saisies respectent l'unicité du triplet (code ISO, langue et code Banque )
		if(rep.findByIsoCodeAndBankCodeAndLangue(devise.getIsoCode(), updated.getBankCode(), devise.getLangue()).isPresent())
		{
			if(!(rep.findByIsoCodeAndBankCodeAndLangue(devise.getIsoCode(), updated.getBankCode(), devise.getLangue()).get()==updated)	)
			throw new AlreadyExistsException("Cette devise ne respecte pas l'unicité du Code Iso");
		}
		
		
		//vérifier si les données saisies respectent l'unicité du triplet (code ALPHA, langue et code Banque )
		if(rep.findByAlphaCodeAndBankCodeAndLangue(devise.getAlphaCode(), updated.getBankCode(), devise.getLangue()).isPresent())
		{
			if(!(rep.findByAlphaCodeAndBankCodeAndLangue(devise.getAlphaCode(), updated.getBankCode(), devise.getLangue()).get()==updated))
			throw new AlreadyExistsException("Cette devise ne respecte pas l'unicité du Code Alpha");
		}
		
		//modifier les données
		
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		updated.setModificationAdmin(admin);
		updated.setModificationDate(LocalDateTime.now());
		updated.setNom(devise.getNom());
		updated.setCode(devise.getCode());
		updated.setIsoCode(devise.getIsoCode());
		updated.setAlphaCode(devise.getAlphaCode());
		updated.setLangue(devise.getLangue());
		
		
		rep.save(updated);
		
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a modifié la devise "+updated.getCode());
	}
	
	
	//supprimer une devise
	public void removeCurrency(String code) throws NotFoundException
	{
		
		//vérifier l'existence de la devise
		Devise devise=rep.findByCode(code).orElseThrow(() -> new NotFoundException("Aucune devise avec le code "+code+" n'est trouvée"));
		rep.delete(devise);		
		
		Admin admin = adminService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		logger.debug("L'administrateur "+admin.getNom()+" "+admin.getPrenom()+" ayant le Username "+admin.getUsername()+" a supprimé la devise "+devise.getCode());
		
	}
	
	

}
