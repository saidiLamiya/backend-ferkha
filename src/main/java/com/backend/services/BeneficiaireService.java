package com.backend.services;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.entities.Agent;
import com.backend.entities.Beneficiaire;
import com.backend.entities.Client;
import com.backend.entities.Compte;
import com.backend.exceptions.AlreadyExistsException;
import com.backend.exceptions.NotFoundException;
import com.backend.repositories.BeneficiaireRepository;
import com.backend.repositories.CompteRepository;
import com.itextpdf.text.DocumentException;

@Service
public class BeneficiaireService {
	
	@Autowired
	BeneficiaireRepository rep;
	
	@Autowired
	CompteRepository repCompte;
	
	@Autowired
	ClientService clientService;
	
	@Autowired
	CompteService compteService;
	
	public List<Beneficiaire> getBeneficiaires()  {
		
		Client client = clientService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		
		List<Beneficiaire> beneficiaires= new ArrayList<Beneficiaire>();	

		beneficiaires=clientService.getBeneficiaires(client.getId());

		if(beneficiaires.isEmpty())  throw new NotFoundException("Aucun compte trouvé");
		
		return beneficiaires;
	}
	
		public void addBeneficiaire(String numCompte) {
		
			Compte compte = compteService.getCompteByNumero(numCompte);
			Client clientParent = clientService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
			
			List<Beneficiaire> ben = clientParent.getBeneficiaires();
			
			for(int i=0; i<ben.size(); i++) {
				if(ben.get(i).getNumeroCompte().equals(numCompte)) {
					throw new AlreadyExistsException("ce beneficiaire exist deja");
				}
			}
		
			if(!repCompte.findByNumero(numCompte).isPresent()) {
				throw new NotFoundException("Un compte avec le Numero "+ numCompte +" n'existe pas");
			}
		
			Beneficiaire beneficiaire = new Beneficiaire();
			Client client = compte.getProprietaire();
		
			beneficiaire.setNom(client.getNom());
			beneficiaire.setPrenom(client.getPrenom());
			beneficiaire.setNumeroCompte(numCompte);
			beneficiaire.setParent(clientParent);
		
			rep.save(beneficiaire);
		}
	
	public void removeBeneficiaire(Long id) throws NotFoundException
	{
		//vérifier l'existence du beneficiaire
		Beneficiaire beneficiaire=rep.findById(id).orElseThrow(() -> new NotFoundException("Aucun beneficiaire avec l'id "+id+" n'est trouvé"));
		rep.delete(beneficiaire);
	}
	
}