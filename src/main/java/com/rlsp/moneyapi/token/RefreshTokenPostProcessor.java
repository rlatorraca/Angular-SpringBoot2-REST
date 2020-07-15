package com.rlsp.moneyapi.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.rlsp.moneyapi.config.property.RlspMoneyApiProperty;

/**
 * Fara a interceptacao do REFRESH_TOKEN e coloca dentro de um COOKIE protegido para que nao seja acessado pelo JavaScript
 * criando uma maior segurança dentro da aplicação
 * 
 * OAuth2AccessToken ==> o tipo de dado que queremos tratar e colocar dentro do Cookie
 * @author rlatorraca
 *
 */

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken>{

	@Autowired	
	private RlspMoneyApiProperty rlspMoneyApiProperty;
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		
		return returnType.getMethod().getName().equals("postAccessToken"); // O nome do metodo deve ser "postAccessToken" para entao chamar o metodo "abaixo beforeBodyWrite"
	}

	
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
		MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
		ServerHttpRequest request, ServerHttpResponse response) {
		
		HttpServletRequest requisicao = ((ServletServerHttpRequest) request).getServletRequest();		// Requisacao do Token
		HttpServletResponse resposta =  ((ServletServerHttpResponse) response).getServletResponse();	// Resposta do Token
				
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body; // Faz um CAST do "body" de OAuth2AccessToken para "DefaultOAuth2AccessToken"
		
		String refreshToken = body.getRefreshToken().getValue(); // Pega o REFRESH TOKEN na resposta da requisicao
		
		incluirRefreshTokenNoCookieSeguro(refreshToken, requisicao, resposta); // Incluido Refresh Token no Cookie Seguro
		removerRefreshTokenDoBody(token);
			
		return body;
	}


	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null); // remove o refresh token do Body  (do corpo da mensagem)		
	}


	/**
	 * Cria o cookie seguro 
	 */
	private void incluirRefreshTokenNoCookieSeguro(String refreshToken, HttpServletRequest requisicao,
			HttpServletResponse resposta) {
		
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);  // Cria um COOKIE
		refreshTokenCookie.setHttpOnly(true); // Cookie apenas acessivel em HTTP)
		//refreshTokenCookie.setSecure(false);  // Cookie deve ser apenas seguro (HTTPS)
		refreshTokenCookie.setSecure(rlspMoneyApiProperty.getSeguranca().isEnableHttps());
		refreshTokenCookie.setPath(requisicao.getContextPath() + "/oauth/token"); // Para qual caminho sera usado o COOKIE
		//refreshTokenCookie.setMaxAge(60*60*24*30);   // Tempo para EXPIRAR o COOKIE (no caso 30 dias)
		refreshTokenCookie.setMaxAge(259200);   // Tempo para EXPIRAR o COOKIE (no caso 30 dias)
		resposta.addCookie(refreshTokenCookie); // Adiciona o  Cookie na resposta
		
	}

}
