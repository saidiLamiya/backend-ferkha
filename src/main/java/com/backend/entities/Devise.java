package com.backend.entities;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.springframework.format.annotation.*;
import lombok.Data;

@Entity
@Table(name="DEVISE")
public @Data class Devise {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_DEVISE")
	Long id;
	
	@Column(name="CODE_DEVISE", unique=true)
	String code;
	
	@Column(name="NOM_DEVISE")
	String nom;
	
	@Column(name="LANGUE_DEVISE")
	String langue;
	
	@Column(name="ALPHA_CODE_DEVISE")
	String alphaCode;
	
	@Column(name="ISO_CODE_DEVISE")
	String isoCode;
	
	@Column(name="BANK_CODE_DEVISE")
	String bankCode="MA001";
	
	@Column(name="PAYS_CODE_DEVISE")
	String paysCode="MA";
	
	@Column(name="CREATION_DATE_DEVISE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDateTime creationDate;
	
	@ManyToOne
	@JoinColumn(name="CREATION_ADMIN_DEVISE")
	Admin creationAdmin;
	
	@Column(name="MODIFICATION_DATE_DEVISE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDateTime modificationDate;
	
	@ManyToOne
	@JoinColumn(name="MODIFICATION_ADMIN_DEVISE")
	Admin modificationAdmin;
	

}
