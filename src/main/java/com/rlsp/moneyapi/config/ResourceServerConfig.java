package com.rlsp.moneyapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration // Apenas pra saber que eh um Configuration
@EnableWebSecurity
@EnableResourceServer
//public class ResourceServerConfig extends WebSecurityConfigurerAdapter{
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Bean
	public UserDetailsService userDetailsService() {
	    User.UserBuilder builder = User.withDefaultPasswordEncoder();
	    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
	    manager.createUser(builder.username("admin").password("admin").roles("ROLE").build());
	    return manager;
	}
	
	/**
	 * Configuracao de USUARIO ou SENHA (BASIC)
	 */
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
				
		// Autenticacao em MEMORIA (BASIC)
		auth.inMemoryAuthentication()
			.withUser("admin")
			.password("admin") // SEM CRIPTOGRAFIA, Com CRIPTOGRAFIA usar "{bcrypt}"
			.roles("ROLE");		
	}
	/*8
	 * Deixa o Servidor SEM ESTADO (Stateless)
	 */
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception{
		resources.stateless(true);
	}
	
	/*
	 * Serve para AUTORIZAR as REQUISICOES de acesso as paginas
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {		
		//super.configure(http);
		/**
		 * A CSRF token is a unique, secret, unpredictable value created by the server-side application 
		 * and transmitted to the client for subsequent HTTP request made by the client application. 
		 * The server-side application validates the CSRF token in the subsequence request, 
		 * and rejects the request if the token is missing or invalid.
		 */
		
		 
		http.authorizeRequests()
				//.antMatchers(HttpMethod.GET,"/oauth/token").permitAll()
				.antMatchers("/categorias").permitAll() // Em /categorias NAO PRECISA estar autenticado 
			.and()
			.authorizeRequests()
				.anyRequest().authenticated() // Solicita AUTENTICACAO para qualquer acesso a pagina			
			.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Nao Existe estado do SESSAO
			.and()
				.csrf().disable(); // Desabilita o CSFR (pois nao eh uma pagina WEB e sim um REST)
	}
}
