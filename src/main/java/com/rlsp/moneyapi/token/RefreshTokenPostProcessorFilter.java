package com.rlsp.moneyapi.token;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Usado para Filtrar/ Pegar o refresh do Token e adiciona na REQUISICAO
 * @author rlatorraca
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Mostra que tem a prioridade ALTA
public class RefreshTokenPostProcessorFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		/**
		 * Verifica as condicoes de existencia do Cookie
		 * 1) URL = "/oauth/token
		 * 2) grant_type na requisicao = refresh_token
		 * 3) Se existe um Cookie dentro da requisicao		  
		 */
		HttpServletRequest requisicao = (HttpServletRequest) request;
		if("/oauth/token".equalsIgnoreCase(requisicao.getRequestURI()) 
				&& "refresh_token".equals(requisicao.getParameter("grant_type")) 
				&& requisicao.getCookies() != null) {
			
			//JAVA 8+
			String refreshToken = 
			        Stream.of(requisicao.getCookies()) //Transformar o array de cookies em um Strem, com o comando Stream.of(...)
			            .filter(cookie -> "refreshToken".equals(cookie.getName())) // Filtrar os dados do Stream para que retorne apenas o que tenha o nome refreshToken
			            .findFirst() // Obter o primeiro objeto do Stream (caso exista)
			            .map(cookie -> cookie.getValue()) // Transformá-lo de cookie em uma String com o seu valor.
			            .orElse(null); //Caso não tenha encontrado um cookie com o nome refreshToken, retorna null.
			
			requisicao = new MyServletRequestWrapper(requisicao, refreshToken); // Sobrescreve a requisicao incluindo o refresh_token
			
			//JAVA 7 <
//			for(Cookie cookie : requisicao.getCookies()) {
//				if(cookie.getName().equals("refreshToken")) {
//					String refreshToken = cookie.getValue();  //Pega o valor do Cookie
//					requisicao = new MyServletRequestWrapper(requisicao, refreshToken); // Sobrescreve a requisicao incluindo o refresh_token
//				}
//			}
		}
		
		chain.doFilter(requisicao, response); // Continua a cadeia do fitro com a NOVA Requisicao
		
	}
	
	/**
	 * Essa classe para a inclusao do COOKIE dentro da REQUISICAO
	 * @author rlatorraca
	 *
	 */
	static class MyServletRequestWrapper extends HttpServletRequestWrapper{

		private String refreshToken;
		
		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}
		
		/**
		 * pega os parametros da REQUISICAO
		 */
		@Override
		public Map<String, String[]> getParameterMap() {
			
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			 // Os valores EXISTENTES da requisicao permanecem 
			map.put("refresh_token", new String[] { refreshToken }); // inclui o valor do "COOKIE" = "refresh_token"
			map.setLocked(true); // Mantem / Trava o mapa na requisicao para mantenha as alteracoes
			return map;			
		}
		
	}
	
}
