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
@Table(name="OPERATION")
public @Data class Operation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_OPERATION")
	Long id;
	
	@JoinColumn(name="COMPTE_OPERATION")
	@ManyToOne 
	Compte compte;

	
	@Column(name="DATE_OPERATION")
	LocalDateTime date;
	
	@Column(name="SOMME_ESPECE_OPERATION")
	double sommeEspece;
	
	@Column(name="SOMME_COMPTE_OPERATION")
	double sommeCompte;
	
	@Column(name="TYPE_OPERATION")
	String type;
	
	@ManyToOne
	@JoinColumn(name="DEVISE_OPERATION")
	Devise devise;

}
