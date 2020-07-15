package com.rlsp.moneyapi.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.rlsp.moneyapi.model.Usuario;

public class UsuarioSistema extends User{

	private static final long serialVersionUID = 1L;

	private Usuario usuario;
	
	public UsuarioSistema(Usuario username, Collection<? extends GrantedAuthority> authorities) {
		super(username.getEmail(), username.getSenha(), authorities);
		this.usuario = usuario;		
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	

}
