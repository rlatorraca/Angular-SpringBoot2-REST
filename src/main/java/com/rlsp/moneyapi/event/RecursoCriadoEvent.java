package com.rlsp.moneyapi.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;
/**
 * Essa classe tera a missao de Criar os LOCATIOSNS / URI para ser remetida a quem SOLICITOU a REQUISICAO com URL da LOCACAO
 *  - Criado na forma de um EVENTO, que fica esperando ser chamado
 * @author rlatorraca
 *
 */
public class RecursoCriadoEvent extends ApplicationEvent {


	private static final long serialVersionUID = 1L;
	
	private HttpServletResponse response;
	private Long codigo;

	//Chama o construtor do PAI (ApplicationEvent) passado o response e o codigo
	public RecursoCriadoEvent(Object source, HttpServletResponse response, Long codigo) {
		super(source);
		this.response = response;
		this.codigo = codigo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Long getCodigo() {
		return codigo;
	}
	
	

}
