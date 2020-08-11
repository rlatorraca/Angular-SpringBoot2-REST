package com.rlsp.moneyapi.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.model.Usuario;
import com.rlsp.moneyapi.repository.LancamentoRepository;

/**
 * Fara o envio de EMAIL
 * @author rlatorraca
 *
 */
@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private LancamentoRepository repo;
	
	
//	@EventListener // Esse metado sera disparado toda vez quea  aplicacao estiver pronta para ser utilizada
//	private void teste(ApplicationReadyEvent event) {
//		this.enviarEmail("rlsprojects.ca@gmail.com", 
//						Arrays.asList("rlatorraca@gmail.com", "rodrigo.latorraca@outlook.com"), 
//						"Teste de Email", 
//						"Bom dia <br/>Brasil e Vietnam");
//		System.out.println("Finalizado o envio de email ... ");
//	}
	
//	@EventListener // Esse metado sera disparado toda vez quea  aplicacao estiver pronta para ser utilizada
//	private void teste(ApplicationReadyEvent event) {
//		
//		String template = "mail/aviso-lancamentos-vencidos";
//		
//		List<Lancamento> lista = repo.findAll();
//		
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put("lancamentos", lista);
//		
//		this.enviarEmail("rlsprojects.ca@gmail.com", 
//						Arrays.asList("rlatorraca@gmail.com", "rodrigo.latorraca@outlook.com"), 
//						"Teste de Email", 
//						template, variaveis);
//		System.out.println("Finalizado o envio de email ... ");
//	}
	
	public void avisarSobreLancamentosVencidos(List<Lancamento> vencidos, List<Usuario> destinatarios) {
		
		//Cria um MAP com a lista de titulos vencidos
		Map<String, Object> variaveis = new HashMap<String, Object>();
		variaveis.put("lancamentos", vencidos);
		
		List<String> emails = destinatarios.stream()
								.map(usuario -> usuario.getEmail())
								.collect(Collectors.toList());
		
		String template = "mail/aviso-lancamentos-vencidos";
		
		this.enviarEmail("rlsprojects.ca@gmail.com", emails, "Titulos Vencidos- RLSPMoney", template, variaveis);
		
	}
	
	//Envio de Mensagem com Template
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String template, Map<String, Object> variaveis) {
		Context context = new Context(new Locale("pt", "BR"));
		variaveis.entrySet().forEach(e -> context.setVariable(e.getKey(), e.getValue())); // adiciona as variaveis ao contexto
		
		//processa HTML pelo Thymeleaf
		String  mensagem = thymeleaf.process(template, context);
		this.enviarEmail(remetente, destinatarios, assunto, mensagem);
		
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
