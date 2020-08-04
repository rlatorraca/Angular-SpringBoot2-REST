package com.rlsp.moneyapi.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.rlsp.moneyapi.config.property.RlspMoneyApiProperty;

/**
 * Filtro que habilita o CORS
 * @author rlatorraca
 *
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter{

	//private String originPermitida = "http://localhost:8000"; // Configurar para diferentes ambientes
	
	@Autowired
	private RlspMoneyApiProperty rlspMoneyApiProperty; 
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest requisicao = (HttpServletRequest) request;
		HttpServletResponse resposta = (HttpServletResponse) response;
		
		//Essa parte deve sempre ser enviada
		resposta.setHeader("Access-Control-Allow-Origin", rlspMoneyApiProperty.getOriginPermitida()); // Origem (hosts) que sao permitidas acessar
		resposta.setHeader("Access-Control-Allow-Credentials", "true"); //Para que o COOKIE do token/ refresh_token seja enviado
		
		if ("OPTIONS".equals(requisicao.getMethod()) && rlspMoneyApiProperty.getOriginPermitida().equals(requisicao.getHeader("Origin"))) { // Em casode Existir "OPTIONS" na requisicao
			resposta.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS"); // Verbos permitidos na requisicao
			resposta.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept"); // Hearders permitidos na requisicao
			resposta.setHeader("Access-Control-Max-Age", "3600"); // Tempo de buffer do CorsFilter em segundos
			
			resposta.setStatus(HttpServletResponse.SC_OK); //Status code (200) indicating the request succeeded normally.
		} else {
			chain.doFilter(requisicao, resposta);
		}
				
	}

}
