package com.rlsp.moneyapi.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.rlsp.moneyapi.config.token.CustomTokenEnhancer;

@Profile("oauth-security")
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	
	@Autowired
	private AuthenticationManager authenticationManager; // Gerencia a AUTENTICACAO (USER + PWD)
	
	
	@Autowired
	private UserDetailsService userDetailsService;		

	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		//Pode ser em MEMORIA ou DB
		clients.inMemory()
				.withClient("angular") // User
				.secret("$2y$12$ShRWmaWryurPTd/LaDDxCeTV61iS6ouhAPBzluhiReu6So5J0Mvr6")    // Password: @angul@r0			
				.scopes("read", "write")
				.authorizedGrantTypes("password", "refresh_token")   // Usa PASSWORD FLOW (1º Autentica, 2º Recebe o TOKEN)
				.accessTokenValiditySeconds(1800)  // Quantidade de tempo que fluxo fica ativo (no caso 30 minutos = 1800 segundos)
				.refreshTokenValiditySeconds(60 * 60 * 24) // 60(sef) * 60(min) * 24  = 1 dia para expirar		
			.and()
				.withClient("mobile")
				.secret("$2y$12$2EKG47Xwvq7vwmRxP5zdf.BLjIPI47h2h7v4iWAozYVbC3POFvjcq") // m0b1l3
				.scopes("read")
				.authorizedGrantTypes("password", "refresh_token")
				.accessTokenValiditySeconds(1800)
				.refreshTokenValiditySeconds(3600 * 24);
	}
	
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		/**
		 * Incrementando informacoes no TOKEN
		 *  - No caso adicionando o nome do Usuario
		 */
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore()) // Armazena o TOKEN no Token store
			//.accessTokenConverter(accessTokenConverter()) // faz a conversao do TOKEN para JWT
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false) // faz que nao precisemos ficar buscando Novos Refresh token, se utilizarmos a aplicacao dentro de 24 horas, como configurado acima.
			.userDetailsService(this.userDetailsService)
			.authenticationManager(this.authenticationManager); // Validando o Token
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
	    return new CustomTokenEnhancer();
	}


	@Bean // Quem precisar de um accessTokenConverter pode chamar esse BEAN
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("rlsp"); // Senha para validar o TOKEn
		return accessTokenConverter;
	}

	/**
	 * Server para ARMAZENAR os Tokens em MEMORIA
	 *  - pode ser armazendo em DB tambem
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	
}
