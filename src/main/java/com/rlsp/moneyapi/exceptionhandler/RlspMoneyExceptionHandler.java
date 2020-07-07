package com.rlsp.moneyapi.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	 * Trata as Mensagens que nao foram / nao consiguiram LER e CUSTOMIZA-SE as Mensagens (BAD REQUEST)
	 *  - ERRO de BAD REQUEST
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		/**
		 * LocaleContextHolder.getLocale() ==> pega o locale corrente (internacionalizacao) do message.properties
		 */
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale()); 
		String mensagemDesenvolvedor = ex.getCause().toString(); 
		List<MensagemErro> listaErros = Arrays.asList(new MensagemErro(mensagemUsuario, mensagemDesenvolvedor));
		
		return handleExceptionInternal(ex, listaErros, headers, HttpStatus.BAD_REQUEST, request); // Passando um BODY (Mensagem) que se queria
	}
	
	/**
	 * Trata as mensagem de ERRO NOS CAMPOS / ARGUMENTOS DE METODOS que NAO sao VALIDOS 
	 * @author rlatorraca
	 *
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<MensagemErro> listaErros = criarListaDeErros(ex.getBindingResult());
		
		return handleExceptionInternal(ex, listaErros, headers, HttpStatus.BAD_REQUEST, request); // Passando um BODY (Mensagem) que se queria
	}
	
	/**
	 * bindingResult ==> onde sera colocada a LISTA DE TODOS os ERROS
	 * @param bindingResult
	 * @return
	 */
	private List<MensagemErro> criarListaDeErros(BindingResult bindingResult){
	
		List<MensagemErro> listaErros = new ArrayList<>();
		
		for (FieldError fieldError : bindingResult.getFieldErrors()) {    // Pega todos o ERROS EXISTENTES
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			listaErros.add(new MensagemErro(mensagemUsuario, mensagemDesenvolvedor));
		}
		return listaErros;
	
	}
	
	/**
	 * Trata o ERRO ao DELETAR alguma informacao (codigo) que nao existe no DB
	 * @author rlatorraca
	 * - @ExceptionHandler ==> mostra qual o tipo de Excecao que ira tratar.
	 *  --> EmptyResultDataAccessException ==> nao retorna pelo menos 1 valor do DB
	 *
	 */
	@ExceptionHandler({EmptyResultDataAccessException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND) // retorna 404 
	private ResponseEntity<Object> handleEmptyResultaDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
	
		//Resposta ao USUARIO e DESENVOLVEDOR
		String mensagemUsuario = messageSource.getMessage("recurso.nao.achado", null, LocaleContextHolder.getLocale()); 
		String mensagemDesenvolvedor = ex.toString(); // Nao tem o .getCause() , pois a Excecao eh postada direto
		List<MensagemErro> listaErros = Arrays.asList(new MensagemErro(mensagemUsuario, mensagemDesenvolvedor));
		
		return handleExceptionInternal(ex, listaErros, new HttpHeaders(), HttpStatus.NOT_FOUND, request); // Passando um BODY (Mensagem) que se queria
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
