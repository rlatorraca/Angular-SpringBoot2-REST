package com.rlsp.moneyapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Profile("oauth-security")
@Configuration // Apenas pra saber que eh um Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/**
	 * Configuracao de USUARIO ou SENHA (BASIC)
	 */
	// @Autowired
	//public void configure(AuthenticationManagerBuilder auth) throws Exception {

		// Autenticacao em MEMORIA (BASIC)
		/*
		 * auth.inMemoryAuthentication() .withUser("admin") .password("admin") // SEM
		 * CRIPTOGRAFIA, Com CRIPTOGRAFIA usar "{bcrypt}" .roles("ROLE");
		 */
	//}

	/*
	 * Deixa o Servidor SEM ESTADO (Stateless)
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}

	/*
	 * Serve para AUTORIZAR as REQUISICOES de acesso as paginas
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// super.configure(http);
		/**
		 * A CSRF token is a unique, secret, unpredictable value created by the
		 * server-side application and transmitted to the client for subsequent HTTP
		 * request made by the client application. The server-side application validates
		 * the CSRF token in the subsequence request, and rejects the request if the
		 * token is missing or invalid.
		 */

		http.authorizeRequests()
				// .antMatchers(HttpMethod.GET,"/oauth/token").permitAll()
				.antMatchers("/categorias").permitAll() // Em /categorias NAO PRECISA estar autenticado
				// .and()
				// .authorizeRequests()
				.anyRequest().authenticated() // Solicita AUTENTICACAO para qualquer acesso a pagina
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Nao Existe estado
																									// do SESSAO
				.and().csrf().disable(); // Desabilita o CSFR (pois nao eh uma pagina WEB e sim um REST)
	}
	
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
}
