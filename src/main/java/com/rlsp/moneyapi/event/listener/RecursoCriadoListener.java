package com.rlsp.moneyapi.event.listener;
/**
 * Fica OUVIDO /PROCURANDO o evento que "RecursoCriadoEvent" que lancara o Evento
 * @author rlatorraca
 *
 */

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rlsp.moneyapi.event.RecursoCriadoEvent;

@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {

	@Override
	public void onApplicationEvent(RecursoCriadoEvent event) {
		
		HttpServletResponse response = event.getResponse();
		Long codigo = event.getCodigo();
		
		// Construindo a LOCATION		
		adicionarHeaderLocation(response, codigo);
	}

	private void adicionarHeaderLocation(HttpServletResponse response, Long codigo) {
		URI uri = ServletUriComponentsBuilder
						.fromCurrentRequestUri()    // pega a URI atual
						.path("/{codigo}")
						.buildAndExpand(codigo) // Pega o codigo
						.toUri();
		
		response.setHeader("Location", uri.toASCIIString()); // resposta cm a Location
	}

}
