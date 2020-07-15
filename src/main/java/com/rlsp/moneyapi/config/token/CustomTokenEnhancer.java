package com.rlsp.moneyapi.config.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.rlsp.moneyapi.security.UsuarioSistema;

/**
 * Pega o Usuario logado e acrescenta dentro do TOKEN
 * @author rlatorraca
 *
 */
public class CustomTokenEnhancer implements TokenEnhancer{

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal() ;  // Pega o usuario logado no sistema
		
		System.out.println("------Principal : " + authentication.getPrincipal());
		System.out.println("------Nome : " + usuarioSistema.getUsuario().getNome());
		
		Map<String, Object> addInfo = new HashMap<>();
		addInfo.put("nome", usuarioSistema.getUsuario().getNome());
		
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addInfo); // Adiciona o NOME no Token
		return accessToken;
	}

}
