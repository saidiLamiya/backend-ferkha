package com.backend.entities;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name="RECHARGE")
public @Data class Recharge {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_RECHARGE")
	Long id;
	
	@Column(name="SOMME_ENV_RECHARGE")
	double sommeEnv;
	
	@Column(name="SOMME_RECU_RECHARGE")
	double sommeRecu;
	
	@ManyToOne
	@JoinColumn(name="DEVISE_RECHARGE")
	Devise devise;
	
	@Column(name="TELEPHONE_RECHARGE")
	String telephone;
	
	@Column(name="DATE_RECHARGE")
	LocalDateTime date;
	
	@ManyToOne
	@JoinColumn(name="COMPTE_RECHARGE")
	Compte compte;
	
	@ManyToOne
	@JoinColumn(name="OPERATEUR_RECHARGE")
	Operateur operateur;

}
