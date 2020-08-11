package com.rlsp.moneyapi.mail;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Fara o envio de EMAIL
 * @author rlatorraca
 *
 */
@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;
	
	@EventListener // Esse metado sera disparado toda vez quea  aplicacao estiver pronta para ser utilizada
	private void teste(ApplicationReadyEvent event) {
		this.enviarEmail("rlsprojects.ca@gmail.com", 
						Arrays.asList("rlatorraca@gmail.com", "rodrigo.latorraca@outlook.com"), 
						"Teste de Email", 
						"Bom dia <br/>Brasil e Vietnam");
		System.out.println("Finalizado o envio de email ... ");
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {
		
		try {
			//Configurando a Mensagem de Email
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			//Configura Remetente, Destinatario, e outras coisas necessarias
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			//Aqui de fato o email sera enviado
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("Problemas com o envio do email : ", e);
		}
		
	}
}
