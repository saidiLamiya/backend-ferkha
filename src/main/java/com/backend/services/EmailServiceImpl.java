package com.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.backend.entities.Utilisateur;

@Service
public class EmailServiceImpl{
	
	
    public JavaMailSender emailSender;
    
    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}



	public void sendAuthenticationInfos(Utilisateur utilisateur) throws MailException
    {
        
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setTo(utilisateur.getEmail()); 
        message.setSubject("Nom d'utilisateur et mot de passe");
        
        String password = utilisateur.getPassword();
				if(password==null || password.isEmpty()) password = "(Votre mot de passe n'est pas changé)";
		
        message.setText(
        		"Cher "+utilisateur.getRole()+", \n"
        		+"Vous trouvez ci-dessous vos coordonnées d'identification dans notre application :\n"
        		+"\nUsername : "+utilisateur.getUsername()
        		+"\nPassword : "+password
        		+"\nCordialement."
        		);
        emailSender.send(message);
        
    }

}
