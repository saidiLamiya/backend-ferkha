package com.backend.entities;


import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name="AGENCE")
public @Data class Agence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_AGENCE")
	Long id;
	
	@Column(name="NOM_AGENCE", unique=true)
	String nom;
	
	@Column(name="ADRESSE_AGENCE")
	String adresse;
	
	@Column(name="TELEPHONE_AGENCE", unique=true)
	String telephone;
	
	@Column(name="FAX_AGENCE", unique=true)
	String fax;
	
	@Column(name="EMAIL_AGENCE")
	String email;
	
	@ManyToOne
	@JoinColumn(name="CREATION_ADMIN_AGENCE")
	Admin creationAdmin;
	
	@JsonIgnore
	@OneToMany(mappedBy="agence",cascade=CascadeType.ALL)
	@Column(name="AGENTS_AGENCE")
	List<Agent> agents;
	
	@JsonIgnore
	@OneToMany(mappedBy="agence",cascade=CascadeType.ALL)
	@Column(name="CLIENTS_AGENCE")
	List<Client> clients;
	
}
