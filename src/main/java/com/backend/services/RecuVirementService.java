package com.backend.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.entities.Agence;
import com.backend.entities.Client;
import com.backend.entities.Compte;
import com.backend.entities.Devise;
import com.backend.entities.Virement;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class RecuVirementService {
	
	@Autowired
	CompteService compteService;
	
	@Autowired
	ClientService clientService;
	
	@Autowired
	AgenceService agenceService;
	
	@Autowired
	DeviseService deviseService;
	
	public void CreateRecu(Virement virement) throws DocumentException, URISyntaxException, MalformedURLException, IOException
	{
		Font fontTitre=new Font(FontFamily.TIMES_ROMAN,30f,Font.UNDERLINE,BaseColor.RED);
		Font fontHeader=new Font(FontFamily.HELVETICA,18f,Font.BOLD,BaseColor.BLACK);
		Font fontData=new Font(FontFamily.HELVETICA,16f,Font.NORMAL,BaseColor.BLACK);
		Paragraph titre = new Paragraph("Reçu de virement.",fontTitre);
		titre.setAlignment(Element.ALIGN_CENTER);
		
		Paragraph space = new Paragraph(" ");
		
		LocalDateTime date = virement.getDate();
		Compte debiteur = compteService.getComptes(virement.getDebiteur().getId()).get(0);
		Compte creancier = compteService.getComptes(virement.getCreancier().getId()).get(0);
		Client client = clientService.getClients(debiteur.getProprietaire().getId()).get(0);
		Agence agenceDebiteur = agenceService.getAgences(client.getAgence().getId()).get(0);
		Devise devise = deviseService.getDevises(debiteur.getDevise().getId()).get(0);
		
		Paragraph agence = new Paragraph("Agence : "+agenceDebiteur.getNom(),fontHeader);
		Paragraph virementDate = new Paragraph("Date : "+date,fontHeader);		
		
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		Paragraph h1 = new Paragraph();
		Paragraph h2 = new Paragraph();
		
		h1 = new Paragraph("Client",fontHeader);
		h2= new Paragraph(client.getNom()+" "+client.getPrenom(),fontData);
		
		table.addCell(new PdfPCell(h1));
		table.addCell(new PdfPCell(h2));

		h1 = new Paragraph("Cin ou Passeport",fontHeader);
		h2= new Paragraph(client.getCin(),fontData);
		
		table.addCell(new PdfPCell(h1));
		table.addCell(new PdfPCell(h2));

		h1 = new Paragraph("N° de compte",fontHeader);
		h2= new Paragraph(debiteur.getNumero(),fontData);
		
		table.addCell(new PdfPCell(h1));
		table.addCell(new PdfPCell(h2));
		

		h1 = new Paragraph("N° de compte du bénificiare",fontHeader);
		h2= new Paragraph(creancier.getNumero(),fontData);
		
		table.addCell(new PdfPCell(h1));
		table.addCell(new PdfPCell(h2));

		h1 = new Paragraph("Montant",fontHeader);
		h2= new Paragraph(String.valueOf(virement.getSommeEnv())+" "+devise.getCode(),fontData);
		
		table.addCell(new PdfPCell(h1));
		table.addCell(new PdfPCell(h2));		
		
		Path path = Paths.get(ClassLoader.getSystemResource("recu\\cachet.jpeg").toURI());
		Image img = Image.getInstance(path.toAbsolutePath().toString());
		img.scaleAbsolute(100F, 100F);
		img.setAlignment(Element.ALIGN_CENTER);
		
		Path path2 = FileSystems.getDefault().getPath("").toAbsolutePath();
		
		Document document= new Document();
		PdfWriter.getInstance(document, new FileOutputStream
		(path2+"\\src\\main\\resources\\recu\\recu-virement\\virement_"+debiteur.getNumero()+"_"+date.toString().replace(':', '-')+".pdf"));
		
		document.open();
		
		document.add(titre);
		document.add(space);
		document.add(space);
		document.add(agence);
		document.add(space);
		document.add(virementDate);
		document.add(space);
		document.add(space);
		document.add(space);
		document.add(table);
		document.add(space);
		document.add(space);
		document.add(space);
		document.add(img);
		document.close();
	}

}
