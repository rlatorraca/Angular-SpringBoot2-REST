package com.rlsp.moneyapi.exceptionhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
/**
 * Captura EXCEÇÔES de uma RESPOSTA ERRADA de alguma Entidade
 * @author rlatorraca
 * 
 *  @ControllerAdvice ==> fica ANALISANDO TODA a aplicacao para entao lançar a Excecção
 *  - Fica disponivel para aplicacao
 *
 */
@ControllerAdvice
public class RlspMoneyExceptionHandler extends ResponseEntityExceptionHandler{
	
	@Autowired
	private MessageSource messageSource; // Pega as MENSAGENS presentes no arquivo "messages.proporties"
	
	
	/**
	 * Captura as Mensagens que nao foram / nao consiguiram LER e CUSTOMIZA-SE as Mensagens
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		/**
		 * LocaleContextHolder.getLocale() ==> pega o locale corrente (internacionalizacao) do message.properties
		 */
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale()); 
		String mensagemDesenvolvedor = ex.getCause().toString(); 
		
		return handleExceptionInternal(ex, new MensagemErro(mensagemUsuario, mensagemDesenvolvedor), headers, HttpStatus.BAD_REQUEST, request); // Passando um BODY (Mensagem) que se queria
	}
	
	public static class MensagemErro {
		
		private String mensagemUsuario;
		private String mensagemDesenvolvedor;
		
		public MensagemErro(String mensagemUsuario, String mensagemDesenvolvedor) {
			super();
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemUsuario() {
			return mensagemUsuario;
		}

		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}
		
		
		
	}
	
	
}
