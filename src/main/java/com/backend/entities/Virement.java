package com.backend.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="VIREMENT")
public @Data class Virement {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_VIREMENT")
	Long id;
	
	@JoinColumn(name="CREANCIER_VIREMENT")
	@ManyToOne 
	Compte creancier;
	
	@JoinColumn(name="DEBITEUR_VIREMENT")
	@ManyToOne
	Compte debiteur;
	
	@Column(name="DATE_VIREMENT")
	LocalDateTime date;
	
	@Column(name="SOMME_ENV_VIREMENT")
	double sommeEnv;
	
	@Column(name="SOMME_RECU_VIREMENT")
	double sommeRecu;
}